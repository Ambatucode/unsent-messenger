package com.unsent.messenger.service

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.unsent.messenger.data.MediaStorageHelper

data class ParsedMessage(
    val conversationId: String,
    val conversationTitle: String,
    val senderName: String,
    val messageText: String,
    val timestamp: Long,
    val packageName: String,
    val isUnsentNotification: Boolean = false,
    val imageBitmap: Bitmap? = null,
    val notificationKey: String? = null
)

object NotificationParser {

    private const val TAG = "NotificationParser"

    private val SUPPORTED_PACKAGES = setOf(
        "com.facebook.orca",      // Facebook Messenger
        "com.facebook.mlite",     // Messenger Lite
        "com.facebook.katana",    // Facebook App
        "com.instagram.android",  // Instagram Direct
        "com.whatsapp",           // WhatsApp
        "org.telegram.messenger"  // Telegram
    )

    private val UNSENT_KEYWORDS = listOf(
        "unsent a message",
        "unsent a photo",
        "unsent an attachment",
        "unsent a video",
        "unsent",
        "unsend",
        "deleted this message",
        "this message was deleted",
        "message was removed",
        "message was deleted",
        "retracted a message",
        "retracted an attachment",
        "nag-unsend ng mensahe",
        "nag-unsend",
        "binura ang mensahe",
        "binura ang larawan",
        "message deleted",
        "message recalled",
        "recalled a message"
    )

    fun isSupportedPackage(packageName: String): Boolean {
        return SUPPORTED_PACKAGES.contains(packageName) || packageName.contains("orca")
    }

    fun parse(context: Context, sbn: StatusBarNotification): List<ParsedMessage> {
        val notification = sbn.notification ?: return emptyList()
        val extras = notification.extras ?: return emptyList()
        val packageName = sbn.packageName
        val postTime = sbn.postTime
        val notificationKey = sbn.key

        val results = mutableListOf<ParsedMessage>()

        // 1. Extract picture from all possible notification extra keys
        val attachedBitmap = extractPictureBitmap(context, notification, extras)

        // 2. Try extracting MessagingStyle messages (modern Android notifications)
        val messagingStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)
        if (messagingStyle != null) {
            val convTitle = messagingStyle.conversationTitle?.toString()
            val userDisplayName = messagingStyle.user.name?.toString() ?: "User"

            for (msg in messagingStyle.messages) {
                var text = msg.text?.toString() ?: ""
                val sender = msg.person?.name?.toString()
                    ?: if (messagingStyle.isGroupConversation) "Group Member" else (convTitle ?: userDisplayName)
                val timestamp = if (msg.timestamp > 0) msg.timestamp else postTime
                val title = convTitle ?: sender
                val convId = generateConversationId(packageName, title)

                val isUnsentNotice = isUnsentText(text)

                // Try extracting photo from MessagingStyle.Message data URI (content:// stream)
                var messageBitmap = attachedBitmap
                if (messageBitmap == null && msg.dataUri != null) {
                    messageBitmap = loadBitmapFromUri(context, msg.dataUri)
                }

                if (text.isBlank() && messageBitmap != null) {
                    text = "📷 [Photo]"
                }

                results.add(
                    ParsedMessage(
                        conversationId = convId,
                        conversationTitle = title,
                        senderName = sender,
                        messageText = text,
                        timestamp = timestamp,
                        packageName = packageName,
                        isUnsentNotification = isUnsentNotice,
                        imageBitmap = messageBitmap,
                        notificationKey = notificationKey
                    )
                )
            }

            if (results.isNotEmpty()) {
                return results
            }
        }

        // 3. Fallback to standard extras (Title, Text, BigText)
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
            ?: "Unknown Chat"

        var text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: ""

        if (text.isBlank() && attachedBitmap != null) {
            text = "📷 [Photo]"
        }

        if (text.isNotBlank() || attachedBitmap != null) {
            val convId = generateConversationId(packageName, title)
            val isUnsentNotice = isUnsentText(text)

            // Extract sender name if formatted as "Sender: Message" in group chats
            var sender = title
            var messageBody = text
            if (text.contains(": ")) {
                val parts = text.split(": ", limit = 2)
                if (parts.size == 2 && parts[0].length < 30) {
                    sender = parts[0].trim()
                    messageBody = parts[1].trim()
                }
            }

            results.add(
                ParsedMessage(
                    conversationId = convId,
                    conversationTitle = title,
                    senderName = sender,
                    messageText = messageBody,
                    timestamp = postTime,
                    packageName = packageName,
                    isUnsentNotification = isUnsentNotice,
                    imageBitmap = attachedBitmap,
                    notificationKey = notificationKey
                )
            )
        }

        return results
    }

    private fun extractPictureBitmap(context: Context, notification: Notification, extras: Bundle): Bitmap? {
        try {
            extras.get(Notification.EXTRA_PICTURE)?.let { pic ->
                if (pic is Bitmap) return pic
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed reading EXTRA_PICTURE", e)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                extras.get(Notification.EXTRA_PICTURE_ICON)?.let { icon ->
                    if (icon is Icon) {
                        val bmp = MediaStorageHelper.iconToBitmap(context, icon)
                        if (bmp != null) return bmp
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading EXTRA_PICTURE_ICON", e)
            }
        }

        try {
            extras.get(Notification.EXTRA_LARGE_ICON_BIG)?.let { bigIcon ->
                if (bigIcon is Bitmap) return bigIcon
                if (bigIcon is Icon) {
                    val bmp = MediaStorageHelper.iconToBitmap(context, bigIcon)
                    if (bmp != null) return bmp
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed reading EXTRA_LARGE_ICON_BIG", e)
        }

        try {
            notification.getLargeIcon()?.let { icon ->
                val bmp = MediaStorageHelper.iconToBitmap(context, icon)
                if (bmp != null && bmp.width > 120 && bmp.height > 120) {
                    return bmp
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed reading getLargeIcon", e)
        }

        return null
    }

    private fun loadBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        if (uri == null) return null
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not open dataUri stream for photo: $uri", e)
            null
        }
    }

    fun isUnsentText(text: String): Boolean {
        val lower = text.lowercase().trim()
        return UNSENT_KEYWORDS.any { lower.contains(it) }
    }

    fun generateConversationId(packageName: String, title: String): String {
        val cleanTitle = title.trim().lowercase()
        return "$packageName::$cleanTitle"
    }
}
