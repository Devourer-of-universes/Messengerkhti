package com.example.myapplication.screen.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.TaskRepository
import com.example.myapplication.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksScreenViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _filter = MutableStateFlow<String?>(null)
    val filter: StateFlow<String?> = _filter.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks(status: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                taskRepository.loadTasks(status = status ?: "pending,in_progress")
                _tasks.value = taskRepository.tasks.value
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки задач"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(status: String?) {
        _filter.value = status
        loadTasks(status)
    }

    fun refresh() {
        loadTasks(_filter.value)
    }

    fun clearError() {
        _error.value = null
    }

    fun getTasksByStatus(status: String): List<Task> {
        return _tasks.value.filter { it.status == status }
    }

    fun getTasksByPriority(priority: String): List<Task> {
        return _tasks.value.filter { it.priority == priority }
    }
}