package com.example.myapplication.screen.Dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.TaskRepository
import com.example.myapplication.model.DashboardStats
import com.example.myapplication.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true

            taskRepository.loadDashboardStats()
            taskRepository.loadTasks(status = "pending,in_progress")

            _stats.value = taskRepository.dashboardStats.value
            _tasks.value = taskRepository.tasks.value

            _isLoading.value = false
        }
    }
}