// data/repository/TaskRepository.kt
package com.example.myapplication.data.repository

import com.example.myapplication.model.DashboardStats
import com.example.myapplication.model.Task
import com.example.myapplication.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskRepository @Inject constructor(
    private val apiService: ApiService
) {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _dashboardStats = MutableStateFlow<DashboardStats?>(null)
    val dashboardStats: StateFlow<DashboardStats?> = _dashboardStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    suspend fun loadDashboardStats() {
        try {
            val response = apiService.getDashboardStats()
            android.util.Log.d("TaskRepo", "=== DASHBOARD STATS RESPONSE ===")
            android.util.Log.d("TaskRepo", "tasksInProgress: ${response.tasksInProgress}")
            android.util.Log.d("TaskRepo", "documentsTotal: ${response.documentsTotal}")
            android.util.Log.d("TaskRepo", "activeProcesses: ${response.activeProcesses}")
            android.util.Log.d("TaskRepo", "unreadNotifications: ${response.unreadNotifications}")

            _dashboardStats.value = DashboardStats(
                tasksInProgress = response.tasksInProgress,
                documentsTotal = response.documentsTotal,
                activeProcesses = response.activeProcesses,
                unreadNotifications = response.unreadNotifications
            )
        } catch (e: Exception) {
            android.util.Log.e("TaskRepo", "Error loading dashboard stats: ${e.message}")
        }
    }

    suspend fun loadTasks(status: String? = null) {
        _isLoading.value = true

        try {
            val response = apiService.getTasks(status = status)
            android.util.Log.d("TaskRepo", "=== TASKS RESPONSE ===")
            android.util.Log.d("TaskRepo", "Total tasks: ${response.tasks.size}")
            response.tasks.forEach { task ->
                android.util.Log.d("TaskRepo", "Task: ${task.id} - ${task.title} - status: ${task.status}")
            }
            _tasks.value = response.tasks
        } catch (e: Exception) {
            android.util.Log.e("TaskRepo", "Error loading tasks: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }
}