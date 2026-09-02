package com.unsent.messenger.service

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.service.notification.StatusBarNotification
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
    val imageBitmap: Bitmap? = null
)

object NotificationParser {

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
        "unsent",
        "deleted this message",
        "this message was deleted",
        "message was removed",
        "retracted a message",
        "nag-unsend ng mensahe",
        "nag-unsend"
    )

    fun isSupportedPackage(packageName: String): Boolean {
        return SUPPORTED_PACKAGES.contains(packageName) || packageName.contains("orca")
    }

    fun parse(context: Context, sbn: StatusBarNotification): List<ParsedMessage> {
        val notification = sbn.notification ?: return emptyList()
        val extras = notification.extras ?: return emptyList()
        val packageName = sbn.packageName
        val postTime = sbn.postTime

        val results = mutableListOf<ParsedMessage>()

        // Check if there is an attached picture in notification extras
        val attachedBitmap = extractPictureBitmap(context, extras)

        // 1. Try extracting MessagingStyle messages (modern Android notifications)
        val messagingStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)
        if (messagingStyle != null) {
            val convTitle = messagingStyle.conversationTitle?.toString()
            val userDisplayName = messagingStyle.user.name?.toString() ?: "User"

            for (msg in messagingStyle.messages) {
                val text = msg.text?.toString() ?: ""
                val sender = msg.person?.name?.toString()
                    ?: if (messagingStyle.isGroupConversation) "Group Member" else (convTitle ?: userDisplayName)
                val timestamp = if (msg.timestamp > 0) msg.timestamp else postTime
                val title = convTitle ?: sender
                val convId = generateConversationId(packageName, title)

                val isUnsentNotice = isUnsentText(text)

                results.add(
                    ParsedMessage(
                        conversationId = convId,
                        conversationTitle = title,
                        senderName = sender,
                        messageText = text,
                        timestamp = timestamp,
                        packageName = packageName,
                        isUnsentNotification = isUnsentNotice,
                        imageBitmap = attachedBitmap
                    )
                )
            }

            if (results.isNotEmpty()) {
                return results
            }
        }

        // 2. Fallback to standard extras (Title, Text, BigText)
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
            ?: "Unknown Chat"

        val text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: ""

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
                    imageBitmap = attachedBitmap
                )
            )
        }

        return results
    }

    private fun extractPictureBitmap(context: Context, extras: Bundle): Bitmap? {
        // 1. Check EXTRA_PICTURE (Bitmap)
        extras.get(Notification.EXTRA_PICTURE)?.let { pic ->
            if (pic is Bitmap) return pic
        }

        // 2. Check EXTRA_PICTURE_ICON (API 31+ Icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            extras.get(Notification.EXTRA_PICTURE_ICON)?.let { icon ->
                if (icon is Icon) {
                    return MediaStorageHelper.iconToBitmap(context, icon)
                }
            }
        }

        // 3. Check EXTRA_BIG_TEXT / Large icon if it looks like a media preview
        extras.get(Notification.EXTRA_LARGE_ICON_BIG)?.let { bigIcon ->
            if (bigIcon is Bitmap) return bigIcon
            if (bigIcon is Icon) return MediaStorageHelper.iconToBitmap(context, bigIcon)
        }

        return null
    }

    private fun isUnsentText(text: String): Boolean {
        val lower = text.lowercase()
        return UNSENT_KEYWORDS.any { lower.contains(it) }
    }

    fun generateConversationId(packageName: String, title: String): String {
        val cleanTitle = title.trim().lowercase()
        return "$packageName::$cleanTitle"
    }
}
