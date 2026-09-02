package com.unsent.messenger.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.unsent.messenger.UnsentApp
import com.unsent.messenger.data.MediaStorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MessengerNotificationListener : NotificationListenerService() {

    private val TAG = "MessengerListener"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification Listener connected successfully.")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val packageName = sbn.packageName ?: return
        if (!NotificationParser.isSupportedPackage(packageName)) {
            return
        }

        serviceScope.launch {
            try {
                val repository = UnsentApp.getRepository(applicationContext)
                val parsedMessages = NotificationParser.parse(applicationContext, sbn)

                for (msg in parsedMessages) {
                    if (msg.isUnsentNotification) {
                        // Mark the latest real message in this conversation as unsent!
                        Log.i(TAG, "⚡ Detected unsend notification in '${msg.conversationTitle}' -> Marking message as UNSENT")
                        repository.markLastMessageAsUnsent(msg.conversationId)
                    } else {
                        // Save image to disk if present
                        var savedImagePath: String? = null
                        if (msg.imageBitmap != null) {
                            savedImagePath = MediaStorageHelper.saveBitmap(applicationContext, msg.imageBitmap)
                            Log.d(TAG, "Saved image to: $savedImagePath")
                        }

                        // Store incoming message with notification key
                        Log.d(TAG, "Captured message from ${msg.senderName}: ${msg.messageText}")
                        repository.saveIncomingMessage(
                            conversationId = msg.conversationId,
                            conversationTitle = msg.conversationTitle,
                            senderName = msg.senderName,
                            messageText = msg.messageText,
                            timestamp = msg.timestamp,
                            packageName = msg.packageName,
                            isUnsent = false,
                            mediaFilePath = savedImagePath,
                            mediaType = if (savedImagePath != null) "image" else null,
                            notificationKey = msg.notificationKey
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling notification posted", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap?, reason: Int) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
        if (sbn == null) return

        val packageName = sbn.packageName ?: return
        if (!NotificationParser.isSupportedPackage(packageName)) return

        // Reason 8 (REASON_APP_CANCEL) = Messenger retracted the notification due to unsend!
        // Reason 1 (REASON_CLICK)
        // Reason 2 (REASON_CANCEL = user swiped away)
        // Reason 3 (REASON_CANCEL_ALL = user cleared all)
        Log.d(TAG, "Notification removed for $packageName with reason $reason (key: ${sbn.key})")

        // When reason is REASON_APP_CANCEL (8), or when the app retracts it
        if (reason == REASON_APP_CANCEL || reason == 8) {
            serviceScope.launch {
                try {
                    val repository = UnsentApp.getRepository(applicationContext)
                    val parsed = NotificationParser.parse(applicationContext, sbn)
                    val convId = parsed.firstOrNull()?.conversationId

                    Log.i(TAG, "⚡ Notification retracted by app ($packageName, key ${sbn.key}) -> Marking as UNSENT")
                    repository.markAsUnsentByNotificationKey(sbn.key, convId)
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling notification removal", e)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "MessengerNotificationListener destroyed.")
    }
}
