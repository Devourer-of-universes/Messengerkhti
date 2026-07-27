// data/repository/AuthRepository.kt
package com.example.myapplication.data.repository

import com.example.myapplication.model.AuthResponse
import com.example.myapplication.model.LoginRequest
import com.example.myapplication.model.RegisterRequest
import com.example.myapplication.model.User
import com.example.myapplication.network.ApiService
import com.example.myapplication.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun login(emailOrUsername: String, password: String): Boolean {
        _isLoading.value = true
        _error.value = null

        return try {
            val response = apiService.login(LoginRequest(emailOrUsername, password))
            if (response.success) {
                TokenManager.saveTokens(
                    accessToken = response.token,
                    userId = response.user.id,
                    username = response.user.username,
                    email = response.user.email
                )
                _currentUser.value = response.user
                true
            } else {
                _error.value = "Ошибка входа"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Ошибка соединения"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun register(request: RegisterRequest): Boolean {
        _isLoading.value = true
        _error.value = null

        return try {
            val response = apiService.register(request)
            if (response.success) {
                TokenManager.saveTokens(
                    accessToken = response.token,
                    userId = response.user.id,
                    username = response.user.username,
                    email = response.user.email
                )
                _currentUser.value = response.user
                true
            } else {
                _error.value = "Ошибка регистрации"
                false
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Ошибка соединения"
            false
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun getCurrentUser(): User? {
        return try {
            val response = apiService.getCurrentUser()
            _currentUser.value = response.user
            response.user
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        TokenManager.clearTokens()
        _currentUser.value = null
    }
}