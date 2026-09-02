package com.example.myapplication.screen.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
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
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            AppTopBar(
                title = "Дашборд",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Обновить */ },
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colors.background)
        ) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ошибка: $error", color = colors.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadData() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text("Повторить")
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
                            StatsGrid(stats = stats, colors = colors)
                        }

                        // Активные задачи
                        item {
                            SectionHeader(
                                title = "Активные задачи",
                                count = tasks.size,
                                onSeeAll = { navController.navigate("tasks") },
                                colors = colors
                            )
                        }

                        if (tasks.isEmpty()) {
                            item {
                                EmptyState(
                                    icon = Icons.Default.CheckCircle,
                                    title = "Нет активных задач",
                                    subtitle = "Отлично! Все задачи выполнены",
                                    colors = colors
                                )
                            }
                        } else {
                            items(tasks.take(5)) { task ->
                                DashboardTaskItem(
                                    task = task,
                                    onClick = { /* TODO: Открыть задачу */ },
                                    colors = colors
                                )
                            }
                        }

                        // Быстрые действия
                        item {
                            SectionHeader(
                                title = "Быстрые действия",
                                count = null,
                                onSeeAll = null,
                                colors = colors
                            )
                        }

                        item {
                            QuickActionsGrid(colors = colors, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsGrid(
    stats: com.example.myapplication.model.DashboardStats?,
    colors: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatsCard(
            icon = Icons.Default.Task,
            value = stats?.tasksInProgress ?: 0,
            label = "Задач в работе",
            color = Color(0xFF3B82F6),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            icon = Icons.Default.DocumentScanner,
            value = stats?.documentsTotal ?: 0,
            label = "Документов",
            color = Color(0xFF10B981),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatsCard(
            icon = Icons.Default.Autorenew,
            value = stats?.activeProcesses ?: 0,
            label = "Процессов",
            color = Color(0xFF8B5CF6),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            icon = Icons.Default.Notifications,
            value = stats?.unreadNotifications ?: 0,
            label = "Уведомлений",
            color = Color(0xFFEF4444),
            colors = colors,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Int,
    label: String,
    color: Color,
    colors: ColorScheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = Color.Black.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = value.toString(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    count: Int?,
    onSeeAll: (() -> Unit)?,
    colors: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground
            )
            if (count != null && count > 0) {
                Surface(
                    shape = CircleShape,
                    color = colors.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = count.toString(),
                        fontSize = 12.sp,
                        color = colors.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
        if (onSeeAll != null) {
            TextButton(onClick = onSeeAll) {
                Text("Все", fontSize = 13.sp, color = colors.primary)
            }
        }
    }
}

@Composable
fun DashboardTaskItem(
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Статус иконка
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when (task.priority) {
                            "critical" -> Color(0xFFEF4444)
                            "high" -> Color(0xFFF59E0B)
                            "medium" -> Color(0xFF10B981)
                            else -> Color(0xFF3B82F6)
                        }
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (task.assigneeName != null) {
                        Text(
                            text = "👤 ${task.assigneeName}",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                    if (task.dueDate != null) {
                        val daysLeft = calculateDaysLeft(task.dueDate)
                        Text(
                            text = when {
                                daysLeft < 0 -> "⚠️ Просрочена"
                                daysLeft == 0 -> "⏰ Сегодня"
                                else -> "📅 ${daysLeft} дн."
                            },
                            fontSize = 12.sp,
                            color = if (daysLeft < 0) Color(0xFFEF4444) else colors.onSurfaceVariant
                        )
                    }
                }
            }

            // Прогресс
            if (task.progress > 0) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${task.progress}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.primary
                    )
                    LinearProgressIndicator(
                        progress = task.progress / 100f,
                        modifier = Modifier
                            .width(40.dp)
                            .height(3.dp),
                        color = colors.primary,
                        trackColor = colors.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid(colors: ColorScheme, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionItem(
            icon = Icons.Default.Create,
            label = "Новая задача",
            onClick = { /* TODO: Открыть создание задачи */ },
            colors = colors,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            icon = Icons.Default.CalendarToday,
            label = "Календарь",
            onClick = { navController.navigate("calendar") },
            colors = colors,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    colors: ColorScheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = colors.onSurfaceVariant,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    colors: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface
        )
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = colors.onSurfaceVariant
        )
    }
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