import { onCall, HttpsError, CallableRequest } from 'firebase-functions/v2/https';
import * as admin from 'firebase-admin';

admin.initializeApp();

interface UpdateOrderStatusData {
  orderId: string;
  newStatus: string;
}

interface CancelOrderData {
  orderId: string;
  reason: string;
}

interface ExtendETAData {
  orderId: string;
}

interface FunctionResponse {
  success: boolean;
  message?: string;
}

/** updateOrderStatus: Called by staff client to advance order status.
 *  Validates the role is staff or admin.
 *  Validates the status transition is legal (see state machine in spec).
 *  Writes new status and timestamp to Firestore.
 *  Sends FCM push notification to the student.
 */
export const updateOrderStatus = onCall(async (request: CallableRequest<UpdateOrderStatusData>): Promise<FunctionResponse> => {
  try {
    // 1. Auth check: context.auth must exist
    if (!request.auth) {
      throw new HttpsError('unauthenticated', 'User must be authenticated');
    }

    const callerUid = request.auth.uid;
    const data = request.data;

    // 2. Fetch caller's user document, check role == staff or admin
    const userDoc = await admin.firestore().collection('users').doc(callerUid).get();
    if (!userDoc.exists) {
      throw new HttpsError('not-found', 'User document not found');
    }
    const userData = userDoc.data()!;
    const role = userData.role;
    if (role !== 'staff' && role !== 'admin') {
      throw new HttpsError('permission-denied', 'Only staff or admin can update order status');
    }

    // 3. Fetch order document
    const orderDoc = await admin.firestore().collection('orders').doc(data.orderId).get();
    if (!orderDoc.exists) {
      throw new HttpsError('not-found', 'Order not found');
    }
    const orderData = orderDoc.data()!;

    // 4. Validate cafeteriaId matches staff's assigned cafeteria
    if (role === 'staff' && orderData.cafeteriaId !== userData.cafeteriaId) {
      throw new HttpsError('permission-denied', 'Staff can only update orders for their cafeteria');
    }

    // 5. Validate status transition
    const currentStatus = orderData.status;
    const validTransitions: { [key: string]: string[] } = {
      pending: ['confirmed', 'preparing'],
      confirmed: ['preparing'],
      preparing: ['ready'],
      ready: ['collected']
    };
    if (!validTransitions[currentStatus] || !validTransitions[currentStatus].includes(data.newStatus)) {
      throw new HttpsError('invalid-argument', 'Invalid status transition');
    }

    // 6. Write new status + timestamp field
    const timestampField = `${data.newStatus}At`;
    const updateData: any = {
      status: data.newStatus,
      [timestampField]: admin.firestore.FieldValue.serverTimestamp()
    };

    // 7. If status == ready: generate 6-char alphanumeric collectionToken, write to order
    if (data.newStatus === 'ready') {
      const collectionToken = generateCollectionToken();
      updateData.collectionToken = collectionToken;
    }

    await orderDoc.ref.update(updateData);

    // 8. Fetch student fcmToken from users document
    const studentDoc = await admin.firestore().collection('users').doc(orderData.studentId).get();
    const studentData = studentDoc.data()!;
    const fcmToken = studentData.fcmToken;

    // 9. Send FCM message to student
    if (fcmToken) {
      const message = {
        token: fcmToken,
        notification: {
          title: 'Order Status Update',
          body: `Your order status has been updated to ${data.newStatus}`
        }
      };
      await admin.messaging().send(message);
    }

    // 10. Write notification document to notifications collection
    await admin.firestore().collection('notifications').add({
      userId: orderData.studentId,
      type: 'order_status_update',
      message: `Order ${data.orderId} status updated to ${data.newStatus}`,
      timestamp: admin.firestore.FieldValue.serverTimestamp()
    });

    return { success: true };
  } catch (error) {
    console.error('Error in updateOrderStatus:', error);
    if (error instanceof HttpsError) {
      throw error;
    }
    throw new HttpsError('internal', 'Internal server error');
  }
});

/** cancelOrder: Called by student or staff to cancel an order.
 *  Students can only cancel Pending or Confirmed orders.
 *  Staff can cancel any status.
 *  Restores stock if status was Pending or Confirmed.
 */
export const cancelOrder = onCall(async (request: CallableRequest<CancelOrderData>): Promise<FunctionResponse> => {
  try {
    // 1. Auth check
    if (!request.auth) {
      throw new HttpsError('unauthenticated', 'User must be authenticated');
    }

    const callerUid = request.auth.uid;
    const data = request.data;

    // 2. Fetch order document
    const orderDoc = await admin.firestore().collection('orders').doc(data.orderId).get();
    if (!orderDoc.exists) {
      throw new HttpsError('not-found', 'Order not found');
    }
    const orderData = orderDoc.data()!;

    // 3. Check caller role
    const userDoc = await admin.firestore().collection('users').doc(callerUid).get();
    const userData = userDoc.data()!;
    const role = userData.role;

    let canCancel = false;
    if (role === 'student') {
      canCancel = orderData.studentId === callerUid && ['pending', 'confirmed'].includes(orderData.status);
    } else if (role === 'staff' || role === 'admin') {
      canCancel = orderData.cafeteriaId === userData.cafeteriaId;
    }

    if (!canCancel) {
      throw new HttpsError('permission-denied', 'Cannot cancel this order');
    }

    // 4. Use Firestore transaction
    await admin.firestore().runTransaction(async (transaction) => {
      // a. Set order status = cancelled, cancelledAt = now, cancellationReason = reason
      transaction.update(orderDoc.ref, {
        status: 'cancelled',
        cancelledAt: admin.firestore.FieldValue.serverTimestamp(),
        cancellationReason: data.reason
      });

      // b. If status was pending or confirmed: restore stockCount for each item
      if (['pending', 'confirmed'].includes(orderData.status)) {
        for (const item of orderData.items) {
          const menuItemRef = admin.firestore().collection('menuItems').doc(item.itemId);
          const menuItemDoc = await transaction.get(menuItemRef);
          const currentStock = menuItemDoc.data()!.stockCount;
          transaction.update(menuItemRef, { stockCount: currentStock + item.quantity });
        }
      }
    });

    // 5. Send FCM cancellation push to student
    const studentDoc = await admin.firestore().collection('users').doc(orderData.studentId).get();
    const studentData = studentDoc.data()!;
    const fcmToken = studentData.fcmToken;
    if (fcmToken) {
      const message = {
        token: fcmToken,
        notification: {
          title: 'Order Cancelled',
          body: `Your order has been cancelled: ${data.reason}`
        }
      };
      await admin.messaging().send(message);
    }

    return { success: true };
  } catch (error) {
    console.error('Error in cancelOrder:', error);
    if (error instanceof HttpsError) {
      throw error;
    }
    throw new HttpsError('internal', 'Internal server error');
  }
});

/** extendETA: Staff adds 5 minutes to estimated pickup time.
 *  Sends updated ETA push notification to student.
 */
export const extendETA = onCall(async (request: CallableRequest<ExtendETAData>): Promise<FunctionResponse> => {
  try {
    // 1. Auth check, role check (staff/admin only)
    if (!request.auth) {
      throw new HttpsError('unauthenticated', 'User must be authenticated');
    }

    const callerUid = request.auth.uid;
    const data = request.data;

    const userDoc = await admin.firestore().collection('users').doc(callerUid).get();
    const userData = userDoc.data()!;
    const role = userData.role;
    if (role !== 'staff' && role !== 'admin') {
      throw new HttpsError('permission-denied', 'Only staff or admin can extend ETA');
    }

    // 2. Increment order.estimatedPickupMinutes by 5
    const orderRef = admin.firestore().collection('orders').doc(data.orderId);
    const orderDoc = await orderRef.get();
    const orderData = orderDoc.data()!;
    const newETA = (orderData.estimatedPickupMinutes || 0) + 5;
    await orderRef.update({ estimatedPickupMinutes: newETA });

    // 3. Send FCM: Your order is taking a little longer — updated time: {newETA}
    const studentDoc = await admin.firestore().collection('users').doc(orderData.studentId).get();
    const studentData = studentDoc.data()!;
    const fcmToken = studentData.fcmToken;
    if (fcmToken) {
      const message = {
        token: fcmToken,
        notification: {
          title: 'Order Delay',
          body: `Your order is taking a little longer — updated pickup time: ${newETA} minutes`
        }
      };
      await admin.messaging().send(message);
    }

    return { success: true };
  } catch (error) {
    console.error('Error in extendETA:', error);
    if (error instanceof HttpsError) {
      throw error;
    }
    throw new HttpsError('internal', 'Internal server error');
  }
});

function generateCollectionToken(): string {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let result = '';
  for (let i = 0; i < 6; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}
