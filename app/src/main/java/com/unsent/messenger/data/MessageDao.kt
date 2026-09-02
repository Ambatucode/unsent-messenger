package com.unsent.messenger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestMessage(conversationId: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND isUnsent = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestActiveMessage(conversationId: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE notificationKey = :key ORDER BY timestamp DESC LIMIT 1")
    suspend fun getMessageByNotificationKey(key: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND isUnsent = 1 ORDER BY timestamp DESC")
    fun getUnsentMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET isUnsent = :isUnsent WHERE id = :id")
    suspend fun setUnsentStatus(id: Long, isUnsent: Boolean)

    @Query("UPDATE messages SET isUnsent = 1 WHERE id = :id")
    suspend fun markAsUnsent(id: Long)

    @Query("UPDATE messages SET isUnsent = 1 WHERE notificationKey = :key")
    suspend fun markAsUnsentByNotificationKey(key: String): Int

    @Query("UPDATE messages SET isUnsent = 1 WHERE conversationId = :conversationId AND messageText = :messageText")
    suspend fun markAsUnsentByText(conversationId: String, messageText: String)

    @Query("UPDATE messages SET mediaFilePath = :filePath, mediaType = :mediaType WHERE id = :id")
    suspend fun updateMediaFile(id: Long, filePath: String, mediaType: String)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteConversationMessages(conversationId: String)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)

    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()

    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId AND isUnsent = 1")
    suspend fun countUnsentMessages(conversationId: String): Int

    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
    suspend fun countTotalMessages(conversationId: String): Int

    @Query("SELECT * FROM messages WHERE messageText LIKE '%' || :query || '%' OR senderName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<MessageEntity>>
}
