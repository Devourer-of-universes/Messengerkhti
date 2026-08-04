package com.example.myapplication.data.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.MediaResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

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
    fun getFolders(): Flow<List<ChatFolder>>
    suspend fun createFolder(name: String): Result<ChatFolder>
    suspend fun deleteFolder(folderId: Int): Result<Unit>
    suspend fun moveChatToFolder(chatId: Int, folderId: Int?): Result<Unit>
    suspend fun uploadFile(chatId: Int, file: File): Flow<Result<ChatMessage>>
    suspend fun getChatMedia(chatId: Int): com.example.myapplication.model.MediaResponse

}