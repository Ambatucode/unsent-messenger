package com.unsent.messenger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversation(conversation: ConversationEntity)

    @Query("SELECT * FROM conversations ORDER BY lastTimestamp DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE conversationId = :conversationId LIMIT 1")
    suspend fun getConversationById(conversationId: String): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE title LIKE '%' || :query || '%' OR lastMessage LIKE '%' || :query || '%' ORDER BY lastTimestamp DESC")
    fun searchConversations(query: String): Flow<List<ConversationEntity>>

    @Query("UPDATE conversations SET unsentCount = :unsentCount, totalMessagesCount = :totalCount WHERE conversationId = :conversationId")
    suspend fun updateCounts(conversationId: String, unsentCount: Int, totalCount: Int)

    @Query("DELETE FROM conversations WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("DELETE FROM conversations")
    suspend fun clearAllConversations()
}
