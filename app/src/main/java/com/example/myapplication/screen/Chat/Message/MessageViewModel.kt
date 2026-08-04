package com.example.myapplication.screen.Chat.Message

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.getColumnIndex
import com.example.myapplication.data.repository.ChatRepository
import com.example.myapplication.model.ChatInfo
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val application: Application  // ← Добавляем Application
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _chatName = MutableStateFlow("")
    val chatName: StateFlow<String> = _chatName.asStateFlow()

    private val _chatAvatar = MutableStateFlow("")
    val chatAvatar: StateFlow<String> = _chatAvatar.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

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
                loadMessages(chatId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка отправки"
            }
        }
    }

    fun uploadFile(chatId: String, uri: Uri) {
        viewModelScope.launch {
            _isUploading.value = true
            _error.value = null

            try {
                val file = uriToFile(uri)
                chatRepository.uploadFile(chatId.toInt(), file).collect { result ->
                    result.onSuccess {
                        loadMessages(chatId)
                    }.onFailure {
                        _error.value = it.message ?: "Ошибка загрузки файла"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки файла"
            } finally {
                _isUploading.value = false
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val context = application.applicationContext
        val contentResolver = context.contentResolver

        // Пробуем получить имя файла
        var fileName = "temp_file"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                fileName = it.getString(nameIndex) ?: "temp_file"
            }
        }

        // Создаем временный файл
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, fileName)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun clearError() {
        _error.value = null
    }
}