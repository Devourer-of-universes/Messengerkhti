package com.example.myapplication.screen.Tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Task
import com.example.myapplication.ui.components.AppTopBar
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.ui.theme.txtMainWhite
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TasksScreenViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val filter by viewModel.filter.collectAsState()

    val colors = MaterialTheme.colorScheme
    var showFilterDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            AppTopBar(
                title = "Задачи",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Фильтр */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Фильтр",
                            tint = txtMainWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.refresh() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить",
                            tint = txtMainWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Создать задачу */ },
                containerColor = colors.primary,
                contentColor = txtMainWhite,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Создать задачу"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colors.background)
        ) {
            when {
                isLoading && tasks.isEmpty() -> LoadingIndicator()
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ошибка: $error", color = colors.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
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
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Нет задач",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurface
                        )
                        Text(
                            text = "Все задачи выполнены или еще не созданы",
                            fontSize = 14.sp,
                            color = colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* TODO: Создать задачу */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Создать задачу")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // Статистика
                        item {
                            TaskStatsBar(
                                total = tasks.size,
                                pending = tasks.count { it.status == "pending" },
                                inProgress = tasks.count { it.status == "in_progress" },
                                completed = tasks.count { it.status == "completed" },
                                colors = colors
                            )
                        }

                        // Список задач
                        items(tasks) { task ->
                            TaskItem(
                                task = task,
                                onClick = { /* TODO: Открыть задачу */ },
                                colors = colors
                            )
                        }
                    }
                }
            }
        }
    }

    // Диалог фильтра
    if (showFilterDialog) {
        FilterDialog(
            currentFilter = filter,
            onDismiss = { showFilterDialog = false },
            onFilterSelected = { status ->
                viewModel.setFilter(status)
                showFilterDialog = false
            },
            colors = colors
        )
    }
}

@Composable
fun TaskStatsBar(
    total: Int,
    pending: Int,
    inProgress: Int,
    completed: Int,
    colors: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TaskStatItem(
                value = total.toString(),
                label = "Всего",
                color = colors.onSurface,
                colors = colors
            )
            TaskStatItem(
                value = pending.toString(),
                label = "Ожидают",
                color = Color(0xFFF59E0B),
                colors = colors
            )
            TaskStatItem(
                value = inProgress.toString(),
                label = "В работе",
                color = Color(0xFF3B82F6),
                colors = colors
            )
            TaskStatItem(
                value = completed.toString(),
                label = "Выполнены",
                color = Color(0xFF10B981),
                colors = colors
            )
        }
    }
}

@Composable
fun TaskStatItem(
    value: String,
    label: String,
    color: Color,
    colors: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    colors: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Заголовок с приоритетом
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(priority = task.priority)
            }

            // Описание
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Мета-информация
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (task.assigneeName != null) {
                    MetaChip(
                        icon = Icons.Default.Person,
                        text = task.assigneeName,
                        colors = colors
                    )
                }
                if (task.dueDate != null) {
                    val daysLeft = calculateDaysLeft(task.dueDate)
                    MetaChip(
                        icon = if (daysLeft < 0) Icons.Default.Warning else Icons.Default.DateRange,
                        text = when {
                            daysLeft < 0 -> "Просрочена"
                            daysLeft == 0 -> "Сегодня"
                            else -> "${daysLeft} дн."
                        },
                        color = if (daysLeft < 0) Color(0xFFEF4444) else null,
                        colors = colors
                    )
                }
            }

            // Прогресс
            if (task.progress > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = task.progress / 100f,
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp),
                        color = colors.primary,
                        trackColor = colors.surfaceVariant
                    )
                    Text(
                        text = "${task.progress}%",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Статус
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusChip(status = task.status, colors = colors)
                Text(
                    text = "ID: #${task.id}",
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PriorityChip(priority: String) {
    val (color, label) = when (priority) {
        "critical" -> Color(0xFFEF4444) to "Критический"
        "high" -> Color(0xFFF59E0B) to "Высокий"
        "medium" -> Color(0xFF10B981) to "Средний"
        else -> Color(0xFF3B82F6) to "Низкий"
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatusChip(status: String, colors: ColorScheme) {
    val (color, label) = when (status) {
        "pending" -> Color(0xFFF59E0B) to "⏳ Ожидает"
        "in_progress" -> Color(0xFF3B82F6) to "🔄 В работе"
        "completed" -> Color(0xFF10B981) to "✅ Выполнена"
        "cancelled" -> Color(0xFFEF4444) to "❌ Отменена"
        else -> colors.onSurfaceVariant to status
    }

    Text(
        text = label,
        fontSize = 12.sp,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun MetaChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color? = null,
    colors: ColorScheme
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color ?: colors.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = color ?: colors.onSurfaceVariant
        )
    }
}

@Composable
fun FilterDialog(
    currentFilter: String?,
    onDismiss: () -> Unit,
    onFilterSelected: (String?) -> Unit,
    colors: ColorScheme
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Фильтр задач",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                listOf(
                    null to "📋 Все задачи",
                    "pending" to "⏳ Ожидают",
                    "in_progress" to "🔄 В работе",
                    "completed" to "✅ Выполнены",
                    "cancelled" to "❌ Отменены"
                ).forEach { (status, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFilterSelected(status) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = if (currentFilter == status) colors.primary else colors.onSurface
                        )
                        if (currentFilter == status) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = colors.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        },
        containerColor = colors.surface
    )
}

// Вспомогательная функция
fun calculateDaysLeft(dateString: String): Int {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return 0
        val daysLeft = ((date.time - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
        daysLeft
    } catch (e: Exception) {
        0
    }
}