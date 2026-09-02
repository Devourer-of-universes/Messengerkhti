package com.example.myapplication.screen.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Session
import com.example.myapplication.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()

    private val _currentSessionId = MutableStateFlow<Int?>(null)
    val currentSessionId: StateFlow<Int?> = _currentSessionId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = apiService.getSessions()
                _sessions.value = response.activeSessions
                _currentSessionId.value = response.currentSessionId
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки сессий"
                _isLoading.value = false
            }
        }
    }

    fun terminateSession(sessionId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.terminateSession(sessionId)
                if (response.success) {
                    // Обновляем список сессий
                    loadSessions()
                } else {
                    _error.value = response.message ?: "Ошибка завершения сессии"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка завершения сессии"
            }
        }
    }

    fun terminateOtherSessions() {
        viewModelScope.launch {
            try {
                val response = apiService.terminateOtherSessions()
                if (response.success) {
                    loadSessions()
                } else {
                    _error.value = response.message ?: "Ошибка завершения сессий"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка завершения сессий"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}