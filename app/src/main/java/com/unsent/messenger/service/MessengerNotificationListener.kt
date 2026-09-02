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

        // Check if supported messaging app
        if (!NotificationParser.isSupportedPackage(packageName)) {
            return
        }

        serviceScope.launch {
            try {
                val repository = UnsentApp.instance.repository
                val parsedMessages = NotificationParser.parse(applicationContext, sbn)

                for (msg in parsedMessages) {
                    if (msg.isUnsentNotification) {
                        // Mark the latest message in this conversation as unsent/deleted!
                        Log.i(TAG, "Detected unsent notification event in ${msg.conversationTitle}")
                        repository.markLastMessageAsUnsent(msg.conversationId)
                    } else {
                        // Save image to disk if present
                        var savedImagePath: String? = null
                        if (msg.imageBitmap != null) {
                            savedImagePath = MediaStorageHelper.saveBitmap(applicationContext, msg.imageBitmap)
                            Log.d(TAG, "Saved image to: $savedImagePath")
                        }

                        // Store the incoming message
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
                            mediaType = if (savedImagePath != null) "image" else null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling notification", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap?, reason: Int) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
        if (sbn == null) return

        val packageName = sbn.packageName ?: return
        if (!NotificationParser.isSupportedPackage(packageName)) return

        Log.d(TAG, "Notification removed for $packageName with reason $reason")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "MessengerNotificationListener destroyed.")
    }
}
