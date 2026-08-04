package com.example.myapplication.screen.Chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.ChatRepository
import com.example.myapplication.model.Chat
import com.example.myapplication.model.ChatFolder
import com.example.myapplication.model.MediaItem
import com.example.myapplication.model.MediaResponse
import com.example.myapplication.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _folders = MutableStateFlow<List<ChatFolder>>(emptyList())
    val folders: StateFlow<List<ChatFolder>> = _folders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadChats(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            chatRepository.getChats(userId).collect { chatList ->
                _chats.value = chatList
                _isLoading.value = false
            }
        }
    }

    fun loadFolders() {
        viewModelScope.launch {
            chatRepository.getFolders().collect { folderList ->
                _folders.value = folderList
            }
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            try {
                chatRepository.createFolder(name)
                loadFolders()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteFolder(folderId: Int) {
        viewModelScope.launch {
            try {
                chatRepository.deleteFolder(folderId)
                loadFolders()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun moveChatToFolder(chatId: Int, folderId: Int?) {
        viewModelScope.launch {
            try {
                chatRepository.moveChatToFolder(chatId, folderId)
                loadChats(TokenManager.getUserId().toString())
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun createChat(name: String, userIds: List<Int>) {
        viewModelScope.launch {
            try {
                val chat = Chat(
                    name = name,
                    isGroup = userIds.size > 1,
                    participants = userIds.map {
                        com.example.myapplication.model.ChatParticipant(id = it)
                    }
                )
                chatRepository.createChat(chat)
                loadChats(TokenManager.getUserId().toString())
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}