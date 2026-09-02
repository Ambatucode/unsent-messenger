package com.unsent.messenger.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["conversationId"]),
        Index(value = ["timestamp"]),
        Index(value = ["conversationId", "senderName", "messageText", "timestamp"], unique = true)
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: String,
    val conversationTitle: String,
    val senderName: String,
    val messageText: String,
    val timestamp: Long,
    val isUnsent: Boolean = false,
    val packageName: String = "com.facebook.orca",
    val mediaFilePath: String? = null,
    val mediaType: String? = null // "image", etc.
)
