import { onSchedule } from 'firebase-functions/v2/scheduler';
import * as admin from 'firebase-admin';

admin.initializeApp();

/** dailyStockReset: Runs every day at 05:00 Africa/Johannesburg.
 *  Resets all menuItems.stockCount to defaultStockCount.
 *  Sets isAvailable = true on all items.
 */
export const dailyStockReset = onSchedule({ schedule: '0 5 * * *', timeZone: 'Africa/Johannesburg' }, async () => {
    try {
      // Get all menuItems
      const menuItemsSnapshot = await admin.firestore().collection('menuItems').get();

      // Use batch to update all
      const batch = admin.firestore().batch();
      menuItemsSnapshot.forEach(doc => {
        const data = doc.data();
        batch.update(doc.ref, {
          stockCount: data.defaultStockCount,
          isAvailable: true
        });
      });

      await batch.commit();
      console.log('Daily stock reset completed');
    } catch (error) {
      console.error('Error in dailyStockReset:', error);
    }
  });

/** autoMarkUncollected: Runs every 5 minutes.
 *  Finds Ready orders older than 20 minutes.
 *  Sets status = uncollected, increments users.noShowCount.
 */
export const autoMarkUncollected = onSchedule('every 5 minutes', async () => {
    try {
      const now = admin.firestore.Timestamp.now();
      const twentyMinutesAgo = new admin.firestore.Timestamp(now.seconds - 20 * 60, now.nanoseconds);

      // Query orders where status == ready AND readyAt < twentyMinutesAgo
      const ordersSnapshot = await admin.firestore()
        .collection('orders')
        .where('status', '==', 'ready')
        .where('readyAt', '<', twentyMinutesAgo)
        .get();

      if (ordersSnapshot.empty) {
        return;
      }

      // Use batch for updates
      const batch = admin.firestore().batch();
      const userUpdates: { [userId: string]: number } = {};

      ordersSnapshot.forEach(doc => {
        const orderData = doc.data();
        const studentId = orderData.studentId;

        // Set status = uncollected
        batch.update(doc.ref, { status: 'uncollected' });

        // Count for user
        if (!userUpdates[studentId]) {
          userUpdates[studentId] = 0;
        }
        userUpdates[studentId]++;
      });

      // Commit order updates
      await batch.commit();

      // Now update users
      const userBatch = admin.firestore().batch();
      for (const [userId, increment] of Object.entries(userUpdates)) {
        const userRef = admin.firestore().collection('users').doc(userId);
        // Get current noShowCount
        const userDoc = await userRef.get();
        const userData = userDoc.data()!;
        const newNoShowCount = (userData.noShowCount || 0) + increment;
        const updateData: any = { noShowCount: newNoShowCount };
        if (newNoShowCount >= 3) {
          updateData.isActive = false;
        }
        userBatch.update(userRef, updateData);
      }

      await userBatch.commit();
      console.log(`Marked ${ordersSnapshot.size} orders as uncollected`);
    } catch (error) {
      console.error('Error in autoMarkUncollected:', error);
    }
  });
