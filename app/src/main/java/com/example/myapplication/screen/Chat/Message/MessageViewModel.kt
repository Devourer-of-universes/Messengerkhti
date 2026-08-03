package com.example.myapplication.screen.Chat.Message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.ChatRepository
import com.example.myapplication.model.ChatInfo
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.utils.TokenManager
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

    private val _chatName = MutableStateFlow("")
    val chatName: StateFlow<String> = _chatName.asStateFlow()

    private val _chatAvatar = MutableStateFlow("")
    val chatAvatar: StateFlow<String> = _chatAvatar.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadMessages(chatId: String) {
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
                        // Получаем имя и аватар
                        val (name, avatar) = getChatDisplayInfo(it)
                        _chatName.value = name
                        _chatAvatar.value = avatar
                    }
                }
            } catch (e: Exception) {
                // Если не загрузилось, ставим значения по умолчанию
                _chatName.value = "Чат"
                _chatAvatar.value = "?"
            }
        }
    }

    private fun getChatDisplayInfo(chatInfo: ChatInfo): Pair<String, String> {
        val currentUserId = TokenManager.getUserId()

        // Если групповой чат - берем название группы
        if (chatInfo.isGroup) {
            val name = chatInfo.name.ifEmpty { "Группа" }
            return name to name.take(1)
        }

        // Если личный чат - ищем собеседника
        val otherUser = chatInfo.participants.find { it.id != currentUserId}
        return if (otherUser != null) {
            val name = "${otherUser.surname} ${otherUser.name}".trim()
            val avatar = otherUser.name.take(1).ifEmpty { "?" }
            name to avatar
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
                loadMessages(chatId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка отправки"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}