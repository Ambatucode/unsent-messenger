package com.unsent.messenger.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsent.messenger.UnsentApp
import com.unsent.messenger.data.ConversationEntity
import com.unsent.messenger.data.MediaStorageHelper
import com.unsent.messenger.data.MessageEntity
import com.unsent.messenger.data.MessageRepository
import com.unsent.messenger.service.MessengerNotificationListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: MessageRepository = UnsentApp.instance.repository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val conversations: StateFlow<List<ConversationEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allConversations
            } else {
                repository.searchConversations(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isNotificationAccessGranted = MutableStateFlow(false)
    val isNotificationAccessGranted: StateFlow<Boolean> = _isNotificationAccessGranted.asStateFlow()

    private val _isBatteryOptimizationIgnored = MutableStateFlow(false)
    val isBatteryOptimizationIgnored: StateFlow<Boolean> = _isBatteryOptimizationIgnored.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun checkPermissions(context: Context) {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        val myListener = ComponentName(context, MessengerNotificationListener::class.java).flattenToString()
        _isNotificationAccessGranted.value = enabledListeners != null && enabledListeners.contains(myListener)

        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        _isBatteryOptimizationIgnored.value = pm?.isIgnoringBatteryOptimizations(context.packageName) ?: false
    }

    fun getMessagesForConversation(conversationId: String): StateFlow<List<MessageEntity>> {
        return repository.getMessages(conversationId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            repository.deleteConversation(conversationId)
        }
    }

    fun deleteMessage(messageId: Long, conversationId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId, conversationId)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    // Diagnostic/Testing function: Simulates receiving text and photos, then unsending them
    fun simulateTestConversation(context: Context) {
        viewModelScope.launch {
            val convId = "com.facebook.orca::john_doe"
            val title = "John Doe (Test)"
            val now = System.currentTimeMillis()

            // 1. Normal message
            repository.saveIncomingMessage(
                conversationId = convId,
                conversationTitle = title,
                senderName = "John Doe",
                messageText = "Hey! Check out this document screenshot:",
                timestamp = now - 90000,
                packageName = "com.facebook.orca"
            )

            // 2. Unsent Photo
            val sampleBitmap = MediaStorageHelper.createSampleTestBitmap()
            val sampleImagePath = MediaStorageHelper.saveBitmap(context, sampleBitmap)

            repository.saveIncomingMessage(
                conversationId = convId,
                conversationTitle = title,
                senderName = "John Doe",
                messageText = "Oops, sent to wrong person! (unsent)",
                timestamp = now - 60000,
                packageName = "com.facebook.orca",
                isUnsent = true,
                mediaFilePath = sampleImagePath,
                mediaType = "image"
            )

            // 3. Unsent Text message
            repository.saveIncomingMessage(
                conversationId = convId,
                conversationTitle = title,
                senderName = "John Doe",
                messageText = "Secret password: super_secret_1234",
                timestamp = now - 30000,
                packageName = "com.facebook.orca",
                isUnsent = true
            )

            // 4. Followup message
            repository.saveIncomingMessage(
                conversationId = convId,
                conversationTitle = title,
                senderName = "John Doe",
                messageText = "Ignore that, let's talk tomorrow instead.",
                timestamp = now,
                packageName = "com.facebook.orca"
            )
        }
    }
}
