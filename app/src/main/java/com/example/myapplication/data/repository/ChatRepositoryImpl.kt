package com.example.myapplication.data.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService
import com.example.myapplication.network.CreateChatRequest
import com.example.myapplication.network.SendMessageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ChatRepository {

    override fun getChats(userId: String): Flow<List<Chat>> = flow {
        try {
            val response = apiService.getChats()
            emit(response.chats)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getChatInfo(chatId: String): Flow<ChatInfo?> = flow {
        try {
            val response = apiService.getChat(chatId.toInt())
            val chat = response.chat
            emit(
                ChatInfo(
                    id = chat.id.toString(),
                    name = chat.name,
                    isGroup = chat.isGroup,
                    avatarUri = chat.avatarUri,
                    createdBy = chat.createdBy.toString(),
                    createdAt = chat.createdAt,
                    participants = chat.participants.map { participant ->
                        ChatParticipant(
                            id = participant.id,
                            surname = participant.surname,
                            name = participant.name,
                            avatarUri = participant.avatarUri,
                            isOnline = participant.isOnline,
                            lastSeenAt = participant.lastSeenAt
                        )
                    },
                    lastMessage = chat.lastMessage?.content ?: "",
                    lastMessageAt = chat.lastMessageAt,
                    messageCount = 0
                )
            )
        } catch (e: Exception) {
            emit(null)
        }
    }

    override fun getMessages(chatId: String): Flow<List<ChatMessage>> = flow {
        try {
            val response = apiService.getMessages(chatId.toInt())
            emit(response.messages)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun createChat(chat: Chat): Result<Chat> {
        return try {
            val request = CreateChatRequest(
                userIds = chat.participants.map { it.id.toInt() },
                name = chat.name.takeIf { it.isNotEmpty() },
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
}