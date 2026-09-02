// data/repository/AuthRepository.kt
package com.example.myapplication.data.repository

import com.example.myapplication.model.AuthResponse
import com.example.myapplication.model.LoginRequest
import com.example.myapplication.model.RegisterRequest
import com.example.myapplication.model.User
import com.example.myapplication.network.ApiService
import com.example.myapplication.network.ChangePasswordRequest
import com.example.myapplication.network.UpdateProfileRequest
import com.example.myapplication.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// data/repository/AuthRepository.kt
// data/repository/AuthRepository.kt
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

                // Загружаем полную информацию о пользователе
                val userResponse = apiService.getUserById(response.user.id)
                _currentUser.value = userResponse.user

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

                val userResponse = apiService.getUserById(response.user.id)
                _currentUser.value = userResponse.user

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
            val userId = TokenManager.getUserId()
            if (userId == 0) return null

            val response = apiService.getUserById(userId)
            val user = response.user

            _currentUser.value = user

            if (user.avatarUri.isNotEmpty()) {
                TokenManager.saveAvatarUri(user.avatarUri)
            }

            android.util.Log.d("AuthRepository", "User loaded: ${user.surname} ${user.name}")
            android.util.Log.d("AuthRepository", "Post: ${user.postName}, Department: ${user.departmentName}")

            user
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error loading user: ${e.message}")
            null
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Boolean {
        return try {
            val response = apiService.updateProfile(request)
            if (response.success) {
                // Обновляем локальные данные
                val current = _currentUser.value
                if (current != null) {
                    _currentUser.value = current.copy(
                        email = request.email ?: current.email,
                        telNum = request.tel_num ?: current.telNum,
                        surname = request.surname ?: current.surname,
                        name = request.name ?: current.name,
                        patronymic = request.patronymic ?: current.patronymic,
                        birthday = request.birthday ?: current.birthday
                    )
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        return try {
            val response = apiService.changePassword(
                ChangePasswordRequest(
                    currentPassword,
                    newPassword
                )
            )
            response.success
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        TokenManager.clearTokens()
        _currentUser.value = null
    }
}