package com.example.myapplication.screen.Chat.Message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.ChatRepository
import com.example.myapplication.model.ChatInfo
import com.example.myapplication.model.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatInfoViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatInfo = MutableStateFlow<ChatInfo?>(null)
    val chatInfo: StateFlow<ChatInfo?> = _chatInfo.asStateFlow()

    private val _images = MutableStateFlow<List<MediaItem>>(emptyList())
    val images: StateFlow<List<MediaItem>> = _images.asStateFlow()

    private val _files = MutableStateFlow<List<MediaItem>>(emptyList())
    val files: StateFlow<List<MediaItem>> = _files.asStateFlow()

    private val _links = MutableStateFlow<List<MediaItem>>(emptyList())
    val links: StateFlow<List<MediaItem>> = _links.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadChatInfo(chatId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                chatRepository.getChatInfo(chatId).collect { info ->
                    _chatInfo.value = info
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки"
                _isLoading.value = false
            }
        }
    }

    fun loadMedia(chatId: String) {
        viewModelScope.launch {
            try {
                val response = chatRepository.getChatMedia(chatId.toInt())

                // Исправлено: используем response.images и response.files
                val images = response.images.map { file ->
                    MediaItem(
                        id = file.id.toString(),
                        name = file.name,
                        url = file.url,
                        size = file.size,
                        date = formatDate(file.created_at),
                        type = "image"
                    )
                }
                _images.value = images

                val files = response.files.map { file ->
                    MediaItem(
                        id = file.id.toString(),
                        name = file.name,
                        url = file.url,
                        size = file.size,
                        date = formatDate(file.created_at),
                        type = "file"
                    )
                }
                _files.value = files

                // Ссылки - пока заглушка
                _links.value = emptyList()

            } catch (e: Exception) {
                // Игнорируем ошибку загрузки медиа
                android.util.Log.e("ChatInfo", "Error loading media: ${e.message}")
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString) ?: return dateString
            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    fun clearError() {
        _error.value = null
    }
}