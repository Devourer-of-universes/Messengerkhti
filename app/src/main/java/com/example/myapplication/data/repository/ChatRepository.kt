package com.example.myapplication.data.repository

import com.example.myapplication.model.*
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(userId: String): Flow<List<Chat>>
    fun getChatInfo(chatId: String): Flow<ChatInfo?>
    fun getMessages(chatId: String): Flow<List<ChatMessage>>
    suspend fun createChat(chat: Chat): Result<Chat>
    suspend fun sendMessage(message: ChatMessage): Result<ChatMessage>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    suspend fun addParticipant(chatId: String, userId: String): Result<Unit>
    suspend fun removeParticipant(chatId: String, userId: String): Result<Unit>
    suspend fun leaveChat(chatId: String, userId: String): Result<Unit>
    suspend fun updateChatInfo(chatId: String, updates: Map<String, Any>): Result<Unit>
}