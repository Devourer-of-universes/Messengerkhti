package com.example.myapplication.screen.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.TaskRepository
import com.example.myapplication.model.User
import com.example.myapplication.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _tasksCount = MutableStateFlow(0)
    val tasksCount: StateFlow<Int> = _tasksCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Загружаем пользователя из кэша
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

    fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                // TODO: Реализовать получение количества непрочитанных
                // Пока заглушка
                _unreadCount.value = 3
            } catch (e: Exception) {
                // Игнорируем
            }
        }
    }

    fun loadTasksCount() {
        viewModelScope.launch {
            try {
                taskRepository.loadTasks(status = "pending,in_progress")
                _tasksCount.value = taskRepository.tasks.value.size
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