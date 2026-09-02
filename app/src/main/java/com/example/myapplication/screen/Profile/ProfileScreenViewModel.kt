package com.example.myapplication.screen.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.TaskRepository
import com.example.myapplication.model.DashboardStats
import com.example.myapplication.model.Notification
import com.example.myapplication.model.Task
import com.example.myapplication.model.User
import com.example.myapplication.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// screen/Profile/ProfileScreenViewModel.kt
@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _stats = MutableStateFlow<DashboardStats?>(null)
    val stats: StateFlow<DashboardStats?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val cachedUser = authRepository.currentUser.value
        if (cachedUser != null) {
            _user.value = cachedUser
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _user.value = user
                } else {
                    _error.value = "Не удалось загрузить профиль"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            try {
                // TODO: Загружать из API
                _notifications.value = listOf(
                    NotificationItem(
                        id = "1",
                        title = "Новое сообщение",
                        message = "Анна Иванова: Привет! Как дела?",
                        time = "5 мин назад",
                        type = "message",
                        isRead = false
                    ),
                    NotificationItem(
                        id = "2",
                        title = "Напоминание о задаче",
                        message = "Срок выполнения задачи 'Отчет за месяц' приближается",
                        time = "1 час назад",
                        type = "task",
                        isRead = false
                    ),
                    NotificationItem(
                        id = "3",
                        title = "Системное обновление",
                        message = "Доступна новая версия приложения",
                        time = "2 дня назад",
                        type = "system",
                        isRead = true
                    )
                )
            } catch (e: Exception) {
                // Игнорируем
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            try {
                taskRepository.loadTasks(status = "pending,in_progress")
                _tasks.value = taskRepository.tasks.value.take(5)
            } catch (e: Exception) {
                // Игнорируем
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            try {
                taskRepository.loadDashboardStats()
                _stats.value = taskRepository.dashboardStats.value
            } catch (e: Exception) {
                // Игнорируем
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (e: Exception) {
                // Игнорируем
            } finally {
                TokenManager.clearTokens()
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}