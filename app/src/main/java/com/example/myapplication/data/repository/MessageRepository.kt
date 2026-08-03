// data/repository/MessageRepository.kt
package com.example.myapplication.data.repository

import com.example.myapplication.model.ChatMessage
import com.example.myapplication.model.ReadReceipt
import com.example.myapplication.network.ApiService
import com.example.myapplication.network.SendMessageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
// data/repository/MessageRepository.kt
@Singleton
class MessageRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getMessages(chatId: String): Flow<List<ChatMessage>> = flow {
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
                    readers = apiMessage.readers?.map { receipt ->
                        ReadReceipt(
                            userId = receipt.user_id,
                            readAt = receipt.read_at
                        )
                    } ?: emptyList()
                )
            }
            emit(messages)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun sendMessage(message: ChatMessage): Result<ChatMessage> {
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

    suspend fun markAsRead(messageId: Int, userId: Int): Result<Unit> {
        return try {
            val response = apiService.markMessageAsRead(messageId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark as read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}