package com.example.myapplication.screen.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Временная модель для уведомлений
data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val time: String = "",
    val isRead: Boolean = false,
    val type: String = "system" // message, task, system, report
)

@HiltViewModel
class NotificationsScreenViewModel @Inject constructor(
    // TODO: Добавить NotificationRepository когда будет готов
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // TODO: Загружать из API когда будет готово
                // Пока заглушка
                val mockNotifications = listOf(
                    NotificationItem(
                        id = "1",
                        title = "Новое сообщение",
                        message = "Иван Иванов: Привет! Как дела?",
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
                        message = "Доступна новая версия приложения 1.0.1",
                        time = "2 дня назад",
                        type = "system",
                        isRead = true
                    )
                )
                _notifications.value = mockNotifications
                _unreadCount.value = mockNotifications.count { !it.isRead }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки уведомлений"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                // TODO: Отправить на сервер
                val updated = _notifications.value.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(isRead = true)
                    } else {
                        notification
                    }
                }
                _notifications.value = updated
                _unreadCount.value = updated.count { !it.isRead }
            } catch (e: Exception) {
                // Игнорируем
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                // TODO: Отправить на сервер
                val updated = _notifications.value.map { it.copy(isRead = true) }
                _notifications.value = updated
                _unreadCount.value = 0
            } catch (e: Exception) {
                // Игнорируем
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}