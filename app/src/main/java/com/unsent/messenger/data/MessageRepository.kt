package com.unsent.messenger.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class MessageRepository(
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao
) {

    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForConversation(conversationId)
    }

    fun getUnsentMessages(conversationId: String): Flow<List<MessageEntity>> {
        return messageDao.getUnsentMessagesForConversation(conversationId)
    }

    fun searchConversations(query: String): Flow<List<ConversationEntity>> {
        return conversationDao.searchConversations(query)
    }

    suspend fun saveIncomingMessage(
        conversationId: String,
        conversationTitle: String,
        senderName: String,
        messageText: String,
        timestamp: Long,
        packageName: String,
        isUnsent: Boolean = false,
        mediaFilePath: String? = null,
        mediaType: String? = null
    ) = withContext(Dispatchers.IO) {
        val trimmedText = messageText.trim()
        if (trimmedText.isEmpty() && mediaFilePath == null) return@withContext

        val displaySnippet = if (trimmedText.isNotEmpty()) {
            trimmedText
        } else if (mediaFilePath != null) {
            "📷 [Photo]"
        } else {
            "Message"
        }

        val message = MessageEntity(
            conversationId = conversationId,
            conversationTitle = conversationTitle,
            senderName = senderName,
            messageText = if (trimmedText.isNotEmpty()) trimmedText else "📷 [Photo]",
            timestamp = timestamp,
            isUnsent = isUnsent,
            packageName = packageName,
            mediaFilePath = mediaFilePath,
            mediaType = mediaType
        )

        // Insert message
        messageDao.insertMessage(message)

        // Count unsent and total
        val unsentCount = messageDao.countUnsentMessages(conversationId)
        val totalCount = messageDao.countTotalMessages(conversationId)

        // Upsert conversation summary
        val conversation = ConversationEntity(
            conversationId = conversationId,
            title = conversationTitle,
            lastMessage = displaySnippet,
            lastSender = senderName,
            lastTimestamp = timestamp,
            unsentCount = unsentCount,
            totalMessagesCount = totalCount,
            packageName = packageName
        )
        conversationDao.upsertConversation(conversation)
    }

    suspend fun markLastMessageAsUnsent(conversationId: String) = withContext(Dispatchers.IO) {
        val latest = messageDao.getLatestMessage(conversationId)
        if (latest != null) {
            messageDao.markAsUnsent(latest.id)
            val unsentCount = messageDao.countUnsentMessages(conversationId)
            val totalCount = messageDao.countTotalMessages(conversationId)
            conversationDao.updateCounts(conversationId, unsentCount, totalCount)
        }
    }

    suspend fun deleteConversation(conversationId: String) = withContext(Dispatchers.IO) {
        messageDao.deleteConversationMessages(conversationId)
        conversationDao.deleteConversation(conversationId)
    }

    suspend fun deleteMessage(messageId: Long, conversationId: String) = withContext(Dispatchers.IO) {
        messageDao.deleteMessageById(messageId)
        val unsentCount = messageDao.countUnsentMessages(conversationId)
        val totalCount = messageDao.countTotalMessages(conversationId)
        conversationDao.updateCounts(conversationId, unsentCount, totalCount)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        messageDao.clearAllMessages()
        conversationDao.clearAllConversations()
    }
}
