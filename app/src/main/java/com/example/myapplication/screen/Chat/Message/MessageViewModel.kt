package com.example.myapplication.screen.Chat.Message

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.getColumnIndex
import com.example.myapplication.data.repository.ChatRepository
import com.example.myapplication.model.ChatInfo
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.network.WebSocketManager
import com.example.myapplication.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


// screen/Chat/Message/MessageViewModel.kt
@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _chatName = MutableStateFlow("")
    val chatName: StateFlow<String> = _chatName.asStateFlow()

    private val _chatAvatar = MutableStateFlow("")
    val chatAvatar: StateFlow<String> = _chatAvatar.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentChatId: String? = null

    // Подписка на новые сообщения через WebSocket
    private val _newMessage = MutableSharedFlow<ChatMessage>()
    val newMessage: SharedFlow<ChatMessage> = _newMessage.asSharedFlow()

    init {
        // Подключаемся к WebSocket при создании ViewModel
        viewModelScope.launch {
            val token = TokenManager.getAccessToken()
            if (!token.isNullOrEmpty()) {
                WebSocketManager.connect(token)
            }
        }

        // Слушаем новые сообщения
        viewModelScope.launch {
            WebSocketManager.newMessageFlow.collect { message ->
                // Обновляем список сообщений, если сообщение из текущего чата
                if (message.chatId.toString() == currentChatId) {
                    val currentList = _messages.value.toMutableList()
                    currentList.add(message)
                    _messages.value = currentList
                    Log.d("MessageViewModel", "New message added via WebSocket: ${message.content}")
                }
            }
        }
    }

    fun loadMessages(chatId: String) {
        currentChatId = chatId
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                chatRepository.getMessages(chatId).collect { messageList ->
                    _messages.value = messageList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки"
                _isLoading.value = false
            }
        }
    }

    fun loadChatInfo(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepository.getChatInfo(chatId).collect { info ->
                    info?.let {
                        val (name, avatar) = getChatDisplayInfo(it)
                        _chatName.value = name
                        _chatAvatar.value = avatar
                    }
                }
            } catch (e: Exception) {
                _chatName.value = "Чат"
                _chatAvatar.value = "?"
            }
        }
    }

    private fun getChatDisplayInfo(chatInfo: ChatInfo): Pair<String, String> {
        val currentUserId = TokenManager.getUserId()

        if (chatInfo.isGroup) {
            val name = chatInfo.name.ifEmpty { "Группа" }
            return name to name.take(1)
        }

        val otherUser = chatInfo.participants.find {
            it.id != currentUserId
        }

        return if (otherUser != null) {
            val name = "${otherUser.surname} ${otherUser.name}".trim()
            val displayName = if (name.isNotEmpty()) name else "Пользователь"
            val avatar = otherUser.name.take(1).ifEmpty {
                otherUser.surname.take(1).ifEmpty { "?" }
            }
            displayName to avatar
        } else {
            val name = chatInfo.name.ifEmpty { "Чат" }
            name to name.take(1)
        }
    }

    fun sendMessage(chatId: String, content: String) {
        viewModelScope.launch {
            try {
                val message = ChatMessage(
                    chatId = chatId.toIntOrNull() ?: 0,
                    content = content,
                    createdAt = System.currentTimeMillis().toString()
                )
                chatRepository.sendMessage(message)
                // Не обновляем список сразу, ждем WebSocket
                // loadMessages(chatId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка отправки"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Не отключаем WebSocket при закрытии одного чата,
        // чтобы другие чаты тоже получали сообщения
    }
}