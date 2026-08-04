package com.example.myapplication.network

import com.example.myapplication.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // ========== АУТЕНТИФИКАЦИЯ ==========
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun getCurrentUser(): UserResponse

    @POST("api/auth/logout")
    suspend fun logout(): SimpleResponse

    // ========== ПОЛЬЗОВАТЕЛИ ==========
    @GET("api/users")
    suspend fun getUsers(@Query("search") search: String? = null): UsersResponse

    @GET("api/users/contacts")
    suspend fun getContacts(): ContactsResponse

    @POST("api/users/contacts/{userId}")
    suspend fun addContact(@Path("userId") userId: Int): SimpleResponse

    @DELETE("api/users/contacts/{userId}")
    suspend fun removeContact(@Path("userId") userId: Int): SimpleResponse

    // ========== ЧАТЫ ==========
    @GET("api/chats")
    suspend fun getChats(): ChatsResponse

    @GET("api/chats/{chatId}")
    suspend fun getChat(@Path("chatId") chatId: Int): ChatResponse

    @POST("api/chats")
    suspend fun createChat(@Body request: CreateChatRequest): CreateChatResponse

    @GET("api/chats/{chatId}/messages")
    suspend fun getMessages(
        @Path("chatId") chatId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): MessagesResponse

    @POST("api/chats/{chatId}/messages")
    suspend fun sendMessage(
        @Path("chatId") chatId: Int,
        @Body request: SendMessageRequest
    ): SendMessageResponse

    @PUT("api/chats/messages/{messageId}")
    suspend fun editMessage(
        @Path("messageId") messageId: Int,
        @Body request: EditMessageRequest
    ): SimpleResponse

    @DELETE("api/chats/messages/{messageId}")
    suspend fun deleteMessage(@Path("messageId") messageId: Int): SimpleResponse

    @POST("api/chats/{chatId}/upload")
    @Multipart
    suspend fun uploadFile(
        @Path("chatId") chatId: Int,
        @Part file: MultipartBody.Part
    ): UploadResponse
    @POST("api/messages/{messageId}/read")
    suspend fun markMessageAsRead(
        @Path("messageId") messageId: Int
    ): SimpleResponse

    @GET("api/messages/{messageId}/reads")
    suspend fun getMessageReads(
        @Path("messageId") messageId: Int
    ): MessageReadsResponse
    // ========== ПАПКИ ==========
    @GET("api/chats/folders")
    suspend fun getFolders(): FoldersResponse

    @POST("api/chats/folders")
    suspend fun createFolder(@Body request: CreateFolderRequest): CreateFolderResponse

    @DELETE("api/chats/folders/{folderId}")
    suspend fun deleteFolder(@Path("folderId") folderId: Int): SimpleResponse

    @POST("api/chats/folders/{folderId}/chats/{chatId}")
    suspend fun moveChatToFolder(
        @Path("folderId") folderId: Int,
        @Path("chatId") chatId: Int
    ): SimpleResponse

    @DELETE("api/chats/folders/{folderId}/chats/{chatId}")
    suspend fun removeChatFromFolder(
        @Path("folderId") folderId: Int,
        @Path("chatId") chatId: Int
    ): SimpleResponse


    @GET("api/chats/{chatId}/media")
    suspend fun getChatMedia(@Path("chatId") chatId: Int): NetworkMediaResponse
    // ========== ЗАДАЧИ ==========
    @GET("api/tasks")
    suspend fun getTasks(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): TasksResponse

    @GET("api/tasks/{taskId}")
    suspend fun getTask(@Path("taskId") taskId: Int): TaskResponse

    @POST("api/tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): TaskResponse

    @PUT("api/tasks/{taskId}")
    suspend fun updateTask(
        @Path("taskId") taskId: Int,
        @Body request: UpdateTaskRequest
    ): TaskResponse

    // ========== СТАТИСТИКА ==========
    @GET("api/dashboard/stats")
    suspend fun getDashboardStats(): DashboardStatsResponse
}

// ========== RESPONSE CLASSES ==========
data class UserResponse(val user: User)
data class UsersResponse(val users: List<User>)
data class ContactsResponse(val contacts: List<User>)
data class UploadResponse(
    val success: Boolean,
    val message: ChatMessage
)

data class MediaResponse(
    val files: List<MediaFile>,
    val images: List<MediaFile>
)

data class MediaFile(
    val id: Int = 0,
    val name: String = "",
    val url: String = "",
    val size: String = "",
    val type: String = "",
    val created_at: String = ""
)
data class NetworkMediaResponse(
    val files: List<NetworkMediaFile>,
    val images: List<NetworkMediaFile>
)

data class NetworkMediaFile(
    val id: Int = 0,
    val name: String = "",
    val url: String = "",
    val size: String = "",
    val type: String = "",
    val created_at: String = ""
)
// === ИСПРАВЛЕНО: ChatsResponse использует ApiChat ===
data class ChatsResponse(val chats: List<ApiChat>)

// === ИСПРАВЛЕНО: ChatResponse использует ApiChat ===
data class ChatResponse(val chat: ApiChat)

data class MessagesResponse(
    val messages: List<ApiMessage>
)data class TasksResponse(val tasks: List<Task>)
data class TaskResponse(val task: Task)
data class SimpleResponse(val success: Boolean)
data class CreateChatResponse(val success: Boolean, val chatId: Int)
data class SendMessageResponse(val success: Boolean, val message: ChatMessage)
data class MessageReadsResponse(
    val reads: List<ReadReceipt>
)
// === ПАПКИ ===
data class FoldersResponse(val folders: List<ChatFolder>)
data class CreateFolderResponse(val success: Boolean, val folder: ChatFolder)
data class CreateFolderRequest(val name: String)

data class DashboardStatsResponse(
    val tasksInProgress: Int,
    val documentsTotal: Int,
    val activeProcesses: Int,
    val unreadNotifications: Int
)

// ========== REQUEST CLASSES ==========
data class CreateChatRequest(
    val userIds: List<Int>,
    val name: String? = null,
    val isGroup: Boolean = false
)

data class SendMessageRequest(
    val content: String
)

data class EditMessageRequest(
    val content: String
)

data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val priority: String = "medium",
    val startDate: String? = null,
    val dueDate: String? = null,
    val assignedTo: Int,
    val observers: List<Int> = emptyList()
)

data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val progress: Int? = null,
    val dueDate: String? = null
)

// ========== МОДЕЛИ СЕРВЕРА ==========
data class ApiChat(
    val id: Int = 0,
    val name: String? = null,
    val is_group: Boolean = false,
    val avatar_uri: String? = null,
    val created_by: Int = 0,
    val created_at: String = "",
    val last_message_at: String = "",
    val unread_count: Int = 0,
    val last_message: LastMessage? = null,
    val folder_id: Int? = null,
    val participants: List<ApiParticipant>? = null  // ← Добавляем
)

data class ApiParticipant(
    val id: Int = 0,
    val surname: String? = null,
    val name: String? = null,
    val avatar_uri: String? = null,
    val status: String? = null,
    val is_online: Boolean = false
)
data class LastMessage(
    val id: Int = 0,
    val content: String = "",
    val created_at: String = "",  // ← String
    val user_id: Int = 0
)
data class ChatFolder(
    val id: Int = 0,
    val name: String = "",
    val user_id: Int = 0,
    val created_at: String = "",
    val updated_at: String = ""
)
data class ApiMessage(
    val id: Int = 0,
    val chat_id: Int = 0,
    val user_id: Int = 0,
    val content: String = "",
    val created_at: String = "",
    val surname: String = "",
    val name: String = "",
    val avatar_uri: String? = null,
    val is_edited: Boolean = false,
    val is_deleted: Boolean = false,
    val readers: List<ReadReceipt>? = null  // Может быть null
)

data class ReadReceipt(
    val user_id: Int,
    val read_at: String
)
data class ChatInfo(
    val id: String = "",
    val name: String = "",
    val is_group: Boolean = false,
    val avatar_uri: String? = null,
    val created_by: Int = 0,
    val created_at: String = "",  // ← String
    val last_message_at: String = "",  // ← String
    val last_message: String? = null,
    val folder_id: Int? = null
)