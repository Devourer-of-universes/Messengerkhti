package com.example.myapplication.screen.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.myapplication.model.Notification
import com.example.myapplication.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val stats by viewModel.stats.collectAsState()  // ← Добавляем
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        viewModel.loadNotifications()
        viewModel.loadTasks()
        viewModel.loadStats()
    }

    Scaffold(
        containerColor = colors.background,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 0.dp)
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
                            onClick = { viewModel.loadProfile() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
                user != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Шапка с аватаром
                        item {
                            ProfileHeader(
                                user = user!!,
                                colors = colors
                            )
                        }

                        // Кнопки действий
                        item {
                            ProfileActionsRow(
                                onLogout = {
                                    viewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                colors = colors,
                                navController = navController
                            )
                        }

                        // Дашборд
                        item {
                            DashboardCard(
                                tasks = tasks,
                                stats = stats,  // ← Передаем статистику
                                onSeeAll = { navController.navigate("dashboard") },
                                colors = colors
                            )
                        }

                        // Уведомления
                        item {
                            NotificationsCard(
                                notifications = notifications,
                                onSeeAll = { navController.navigate("notifications") },
                                colors = colors
                            )
                        }

                        // О приложении
                        item {
                            AppInfoCard(
                                onClick = { navController.navigate("appinfo") },
                                colors = colors
                            )
                        }

                        // Отступ снизу
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: com.example.myapplication.model.User,
    colors: ColorScheme
) {
    val displayName = "${user.surname} ${user.name}".trim()
    val avatarLetter = user.name.take(1).uppercase()
    val avatarUri = user.avatarUri

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Аватар
        Box(
            modifier = Modifier
                .size(96.dp)
                .shadow(
                    elevation = 8.dp,
                    spotColor = colors.primary.copy(alpha = 0.3f),
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUri.isNotEmpty()) {
                // TODO: Загрузить изображение через Coil
                Text(
                    text = "🖼️",
                    fontSize = 32.sp
                )
            } else {
                Text(
                    text = avatarLetter,
                    color = colors.primary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Имя
        Text(
            text = displayName.ifEmpty { user.username },
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Должность
        if (user.postName.isNotEmpty()) {
            Text(
                text = user.postName,
                fontSize = 14.sp,
                color = colors.onSurfaceVariant
            )
        }
        if (user.departmentName.isNotEmpty()) {
            Text(
                text = "🏢 ${user.departmentName}",
                fontSize = 14.sp,
                color = colors.onSurfaceVariant
            )
        }

        // Дополнительная информация (можно показать в виде чипов)
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (user.birthday != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant
                ) {
                    Text(
                        text = "🎂 ${user.birthday}",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            if (user.startDate != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant
                ) {
                    Text(
                        text = "📅 С ${user.startDate}",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            if (user.isSuperAdmin) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "👑 Супер-админ",
                        fontSize = 11.sp,
                        color = colors.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileActionsRow(
    onLogout: () -> Unit,
    colors: ColorScheme,
    navController: NavController  // ← Исправлено: передаем NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ProfileActionButton(
            icon = Icons.Default.Settings,
            label = "Настройки",
            onClick = { navController.navigate("settings") },
            colors = colors
        )
        ProfileActionButton(
            icon = Icons.Default.Person,
            label = "Личные данные",
            onClick = { navController.navigate("profile_info") },
            colors = colors
        )
        ProfileActionButton(
            icon = Icons.Default.Logout,
            label = "Выйти",
            onClick = onLogout,
            colors = colors
        )
    }
}

@Composable
fun ProfileActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    colors: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .border(
                    width = 2.dp,
                    color = colors.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
fun DashboardCard(
    tasks: List<Task>,
    stats: com.example.myapplication.model.DashboardStats?,
    onSeeAll: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Дашборд",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                }
                TextButton(onClick = onSeeAll) {
                    Text("Подробнее", fontSize = 13.sp, color = colors.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Статистика в виде сетки
            val tasksInProgress = tasks.count { it.status == "in_progress" || it.status == "pending" }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardStatItem(
                    value = tasksInProgress.toString(),
                    label = "В работе",
                    icon = Icons.Default.Task,
                    color = Color(0xFF3B82F6),
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatItem(
                    value = stats?.documentsTotal?.toString() ?: "0",
                    label = "Документов",
                    icon = Icons.Default.DocumentScanner,
                    color = Color(0xFF10B981),
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardStatItem(
                    value = stats?.activeProcesses?.toString() ?: "0",
                    label = "Процессов",
                    icon = Icons.Default.Autorenew,
                    color = Color(0xFF8B5CF6),
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatItem(
                    value = stats?.unreadNotifications?.toString() ?: "0",
                    label = "Уведомлений",
                    icon = Icons.Default.Notifications,
                    color = Color(0xFFEF4444),
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DashboardStatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    colors: ColorScheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
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
fun NotificationsCard(
    notifications: List<NotificationItem>,
    onSeeAll: () -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Уведомления",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                    if (notifications.count { !it.isRead } > 0) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = notifications.count { !it.isRead }.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                TextButton(onClick = onSeeAll) {
                    Text("Все", fontSize = 13.sp, color = colors.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val unreadNotifications = notifications.filter { !it.isRead }
            val displayNotifications = if (unreadNotifications.isNotEmpty()) {
                unreadNotifications.take(3)
            } else {
                notifications.take(3)
            }

            if (displayNotifications.isEmpty()) {
                Text(
                    text = "Нет уведомлений 🔔",
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                displayNotifications.forEachIndexed { index, notification ->
                    NotificationItemRow(
                        notification = notification,
                        colors = colors
                    )
                    if (index < displayNotifications.size - 1) {
                        Divider(
                            color = colors.onSurface.copy(alpha = 0.08f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItemRow(
    notification: NotificationItem,
    colors: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Открыть уведомление */ }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.title,
                fontSize = 14.sp,
                fontWeight = if (!notification.isRead) FontWeight.Medium else FontWeight.Normal,
                color = colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = notification.message,
                fontSize = 13.sp,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = notification.time,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
fun AppInfoCard(
    onClick: () -> Unit,
    colors: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "О приложении",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onBackground
                )
                Text(
                    text = "Версия 1.0.0",
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.onSurfaceVariant
            )
        }
    }
}