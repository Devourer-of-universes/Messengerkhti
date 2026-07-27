package com.example.myapplication.screen.Profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Task
import com.example.myapplication.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TasksScreenViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задачи") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && tasks.isEmpty() -> LoadingIndicator()
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ошибка: $error")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadTasks() }) {
                            Text("Повторить")
                        }
                    }
                }
                tasks.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("✅", fontSize = 48.sp)
                        Text("Нет задач", style = MaterialTheme.typography.titleMedium)
                        Text("Все задачи будут отображаться здесь", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasks) { task ->
                            TaskCard(task = task)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Medium
                )
                when (task.priority) {
                    "critical" -> Text("🔴 Критический", color = MaterialTheme.colorScheme.error)
                    "high" -> Text("🟠 Высокий", color = MaterialTheme.colorScheme.tertiary)
                    "medium" -> Text("🟡 Средний", color = MaterialTheme.colorScheme.secondary)
                    else -> Text("🟢 Низкий", color = MaterialTheme.colorScheme.primary)
                }
            }

            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Прогресс: ${task.progress}%",
                    style = MaterialTheme.typography.bodySmall
                )
                if (task.dueDate != null) {
                    Text(
                        text = "📅 ${task.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Прогресс-бар
            LinearProgressIndicator(
                progress = task.progress / 100f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
