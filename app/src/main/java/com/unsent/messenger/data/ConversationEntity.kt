package com.unsent.messenger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val conversationId: String,
    val title: String,
    val lastMessage: String,
    val lastSender: String,
    val lastTimestamp: Long,
    val unsentCount: Int = 0,
    val totalMessagesCount: Int = 0,
    val packageName: String = "com.facebook.orca"
)
