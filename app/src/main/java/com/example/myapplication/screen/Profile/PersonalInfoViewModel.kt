package com.example.myapplication.screen.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OldProfileScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

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
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // TODO: Отправить на сервер
                _user.value = updatedUser
                _isEditing.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка обновления"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}