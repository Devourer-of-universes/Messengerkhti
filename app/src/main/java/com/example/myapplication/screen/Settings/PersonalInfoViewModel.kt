package com.example.myapplication.screen.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.model.User
import com.example.myapplication.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OldProfileScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _showSaveDialog = MutableStateFlow(false)
    val showSaveDialog: StateFlow<Boolean> = _showSaveDialog.asStateFlow()

    private val _showPasswordDialog = MutableStateFlow(false)
    val showPasswordDialog: StateFlow<Boolean> = _showPasswordDialog.asStateFlow()

    private val _editedUser = MutableStateFlow<User?>(null)
    val editedUser: StateFlow<User?> = _editedUser.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _user.value = user
                    _editedUser.value = user
                } else {
                    _error.value = "Не удалось загрузить данные"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value
        if (_isEditing.value) {
            // Копируем текущие данные в редактируемые
            _editedUser.value = _user.value
        }
    }

    fun cancelEditing() {
        _isEditing.value = false
        _editedUser.value = _user.value
    }

    fun updateField(field: String, value: String) {
        val current = _editedUser.value ?: return
        _editedUser.value = when (field) {
            "surname" -> current.copy(surname = value)
            "name" -> current.copy(name = value)
            "patronymic" -> current.copy(patronymic = value)
            "birthday" -> current.copy(birthday = value)
            "phone" -> current.copy(telNum = value)
            "email" -> current.copy(email = value)
            else -> current
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val updatedUser = _editedUser.value ?: return@launch

                // Отправляем изменения на сервер
                val response = apiService.updateUserProfile(
                    updatedUser.id,
                    updatedUser.surname,
                    updatedUser.name,
                    updatedUser.patronymic,
                    updatedUser.telNum,
                    updatedUser.email
                )

                if (response.success) {
                    // Обновляем локальные данные
                    _user.value = updatedUser
                    _isEditing.value = false
                    _showSaveDialog.value = true

                    // TODO: Создать уведомление для администратора
                    // createNotificationForAdmin("Пользователь ${updatedUser.username} запросил изменение данных")
                } else {
                    _error.value = response.message ?: "Ошибка сохранения"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка сохранения"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun showChangePasswordDialog() {
        _showPasswordDialog.value = true
    }

    fun closePasswordDialog() {
        _showPasswordDialog.value = false
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = apiService.changePassword(currentPassword, newPassword)
                if (response.success) {
                    _showPasswordDialog.value = false
                    _showSaveDialog.value = true
                } else {
                    _error.value = response.message ?: "Ошибка смены пароля"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка смены пароля"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun closeSaveDialog() {
        _showSaveDialog.value = false
    }

    fun clearError() {
        _error.value = null
    }
}