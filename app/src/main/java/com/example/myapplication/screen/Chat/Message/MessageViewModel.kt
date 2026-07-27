package com.example.myapplication.screen.Chat.Message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.ChatRepository
import com.example.myapplication.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            chatRepository.getMessages(chatId).collect { messageList ->
                _messages.value = messageList
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(chatId: String, content: String) {
        viewModelScope.launch {
            val message = ChatMessage(
                chatId = chatId.toIntOrNull() ?: 0,
                content = content,
                createdAt = System.currentTimeMillis().toString()
            )
            chatRepository.sendMessage(message)
            loadMessages(chatId)
        }
    }

    fun clearError() {
        _error.value = null
    }
}