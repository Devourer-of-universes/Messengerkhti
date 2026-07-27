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
data class ChatsResponse(val chats: List<Chat>)
data class ChatResponse(val chat: Chat)
data class MessagesResponse(val messages: List<ChatMessage>)
data class TasksResponse(val tasks: List<Task>)
data class TaskResponse(val task: Task)
data class SimpleResponse(val success: Boolean)
data class CreateChatResponse(val success: Boolean, val chatId: Int)
data class SendMessageResponse(val success: Boolean, val message: ChatMessage)
data class UploadResponse(val success: Boolean, val message: ChatMessage)
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