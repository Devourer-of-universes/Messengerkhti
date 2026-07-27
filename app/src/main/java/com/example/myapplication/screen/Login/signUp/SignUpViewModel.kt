package com.example.myapplication.screen.Login.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.model.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val success = authRepository.register(request)
            if (success) {
                _isRegistered.value = true
            } else {
                _error.value = authRepository.error.value ?: "Ошибка регистрации"
            }

            _isLoading.value = false
        }
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }
}