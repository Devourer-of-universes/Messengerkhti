// data/repository/ChatRepositoryImpl.kt
package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService
import com.example.myapplication.network.CreateChatRequest
import com.example.myapplication.network.CreateFolderRequest
import com.example.myapplication.network.MediaResponse
import com.example.myapplication.network.SendMessageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ChatRepository {

    // data/repository/ChatRepositoryImpl.kt
    override fun getChats(userId: String): Flow<List<Chat>> = flow {
        try {
            val response = apiService.getChats()

            val chats = response.chats.map { apiChat ->
                val chatName = if (apiChat.is_group) {
                    apiChat.name ?: "Группа"  // ← Если null, ставим "Группа"
                } else {
                    apiChat.name ?: "Пользователь"  // ← Если null, ставим "Пользователь"
                }

                val lastMessageText = apiChat.last_message?.content ?: ""

                Chat(
                    id = apiChat.id,
                    name = chatName,
                    isGroup = apiChat.is_group,
                    avatarUri = apiChat.avatar_uri ?: "",
                    createdBy = apiChat.created_by,
                    createdAt = apiChat.created_at,
                    lastMessageAt = apiChat.last_message_at,
                    unreadCount = apiChat.unread_count,
                    participants = emptyList(),
                    lastMessage = lastMessageText,
                    lastMessageUserId = apiChat.last_message?.user_id ?: 0,
                    folderId = apiChat.folder_id
                )
            }

            emit(chats)
        } catch (e: Exception) {
            android.util.Log.e("ChatDebug", "Error loading chats: ${e.message}")
            emit(emptyList())
        }
    }

    override fun getChatInfo(chatId: String): Flow<ChatInfo?> = flow {
        try {
            val response = apiService.getChat(chatId.toInt())
            val apiChat = response.chat

            // Преобразуем участников
            val participants = apiChat.participants?.map { participant ->
                ChatParticipant(
                    id = participant.id,
                    surname = participant.surname ?: "",
                    name = participant.name ?: "",
                    avatarUri = participant.avatar_uri ?: "",
                    isOnline = participant.is_online ?: false,
                    lastSeenAt = 0L
                )
            } ?: emptyList()

            emit(
                ChatInfo(
                    id = apiChat.id.toString(),
                    name = apiChat.name ?: "",  // Может быть null
                    isGroup = apiChat.is_group,
                    avatarUri = apiChat.avatar_uri ?: "",
                    createdBy = apiChat.created_by.toString(),
                    createdAt = apiChat.created_at,
                    participants = participants,
                    lastMessage = apiChat.last_message?.content ?: "",
                    lastMessageAt = apiChat.last_message_at,
                    messageCount = 0
                )
            )
        } catch (e: Exception) {
            android.util.Log.e("ChatDebug", "Error loading chat info: ${e.message}")
            emit(null)
        }
    }

    override fun getMessages(chatId: String): Flow<List<ChatMessage>> = flow {
        try {
            val response = apiService.getMessages(chatId.toInt())
            val messages = response.messages.map { apiMessage ->
                ChatMessage(
                    id = apiMessage.id,
                    chatId = apiMessage.chat_id,
                    userId = apiMessage.user_id,
                    content = apiMessage.content,
                    createdAt = apiMessage.created_at,
                    surname = apiMessage.surname,
                    name = apiMessage.name,
                    avatarUri = apiMessage.avatar_uri ?: "",
                    isEdited = apiMessage.is_edited,
                    isDeleted = apiMessage.is_deleted,
                    readers = emptyList() // Временно, пока нет поля в БД
                )
            }
            emit(messages)
        } catch (e: Exception) {
            android.util.Log.e("MessageDebug", "Error loading messages: ${e.message}")
            emit(emptyList())
        }
    }

    override suspend fun createChat(chat: Chat): Result<Chat> {
        return try {
            val request = CreateChatRequest(
                userIds = emptyList(),
                name = chat.name,
                isGroup = chat.isGroup
            )
            val response = apiService.createChat(request)
            if (response.success) {
                Result.success(chat.copy(id = response.chatId))
            } else {
                Result.failure(Exception("Failed to create chat"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(message: ChatMessage): Result<ChatMessage> {
        return try {
            val request = SendMessageRequest(content = message.content)
            val response = apiService.sendMessage(message.chatId, request)
            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception("Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            val response = apiService.deleteMessage(messageId.toInt())
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addParticipant(chatId: String, userId: String): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeParticipant(chatId: String, userId: String): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun leaveChat(chatId: String, userId: String): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateChatInfo(chatId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== ПАПКИ ==========

    override fun getFolders(): Flow<List<ChatFolder>> = flow {
        try {
            val response = apiService.getFolders()
            // Маппим network.ChatFolder в model.ChatFolder
            val folders = response.folders.map { apiFolder ->
                ChatFolder(
                    id = apiFolder.id,
                    name = apiFolder.name,
                    userId = apiFolder.user_id,
                    createdAt = apiFolder.created_at,
                    updatedAt = apiFolder.updated_at
                )
            }
            emit(folders)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun createFolder(name: String): Result<ChatFolder> {
        return try {
            val response = apiService.createFolder(CreateFolderRequest(name))
            if (response.success) {
                val folder = ChatFolder(
                    id = response.folder.id,
                    name = response.folder.name,
                    userId = response.folder.user_id,
                    createdAt = response.folder.created_at,
                    updatedAt = response.folder.updated_at
                )
                Result.success(folder)
            } else {
                Result.failure(Exception("Failed to create folder"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFolder(folderId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteFolder(folderId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete folder"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moveChatToFolder(chatId: Int, folderId: Int?): Result<Unit> {
        return try {
            if (folderId != null) {
                val response = apiService.moveChatToFolder(folderId, chatId)
                if (response.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to move chat"))
                }
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun uploadFile(chatId: Int, file: File): Flow<Result<ChatMessage>> = flow {
        try {
            val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = apiService.uploadFile(chatId, part)
            if (response.success) {
                emit(Result.success(response.message))
            } else {
                emit(Result.failure(Exception("Failed to upload file")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getChatMedia(chatId: Int): com.example.myapplication.model.MediaResponse {
        return try {
            val response = apiService.getChatMedia(chatId) // Это NetworkMediaResponse

            // Преобразуем из network в model
            com.example.myapplication.model.MediaResponse(
                files = response.files.map { file ->
                    com.example.myapplication.model.MediaFile(
                        id = file.id,
                        name = file.name,
                        url = file.url,
                        size = file.size,
                        type = file.type,
                        created_at = file.created_at
                    )
                },
                images = response.images.map { file ->
                    com.example.myapplication.model.MediaFile(
                        id = file.id,
                        name = file.name,
                        url = file.url,
                        size = file.size,
                        type = file.type,
                        created_at = file.created_at
                    )
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("ChatRepository", "Error loading media: ${e.message}")
            com.example.myapplication.model.MediaResponse(emptyList(), emptyList())
        }
    }
}