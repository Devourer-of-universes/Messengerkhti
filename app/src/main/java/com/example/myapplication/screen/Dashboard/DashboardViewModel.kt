package com.example.myapplication.screen.Dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.DashboardStats
import com.example.myapplication.model.Task
import com.example.myapplication.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// screen/Dashboard/DashboardViewModel.kt
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _stats = MutableStateFlow<DashboardStats?>(null)
    val stats: StateFlow<DashboardStats?> = _stats.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Загружаем статистику
                taskRepository.loadDashboardStats()
                val statsValue = taskRepository.dashboardStats.value
                android.util.Log.d("Dashboard", "Stats loaded: tasksInProgress=${statsValue?.tasksInProgress}, documentsTotal=${statsValue?.documentsTotal}")
                _stats.value = statsValue

                // Загружаем задачи
                taskRepository.loadTasks(status = "pending,in_progress")
                val tasksValue = taskRepository.tasks.value
                android.util.Log.d("Dashboard", "Tasks loaded: ${tasksValue.size}")
                _tasks.value = tasksValue
            } catch (e: Exception) {
                android.util.Log.e("Dashboard", "Error loading data: ${e.message}")
                _error.value = e.message ?: "Ошибка загрузки"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}