"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.sendNotificationToUser = void 0;
const admin = require("firebase-admin");
/**
 * Send a push notification to a user via FCM.
 * @param userId The user ID to send the notification to.
 * @param title The notification title.
 * @param body The notification body.
 */
async function sendNotificationToUser(userId, title, body) {
    try {
        const userDoc = await admin.firestore().collection('users').doc(userId).get();
        if (!userDoc.exists) {
            console.warn(`User ${userId} not found`);
            return;
        }
        const userData = userDoc.data();
        const fcmToken = userData.fcmToken;
        if (!fcmToken) {
            console.warn(`No FCM token for user ${userId}`);
            return;
        }
        const message = {
            token: fcmToken,
            notification: {
                title,
                body
            }
        };
        await admin.messaging().send(message);
        console.log(`Notification sent to user ${userId}`);
    }
    catch (error) {
        console.error(`Error sending notification to user ${userId}:`, error);
    }
}
exports.sendNotificationToUser = sendNotificationToUser;
//# sourceMappingURL=fcmFunctions.js.map