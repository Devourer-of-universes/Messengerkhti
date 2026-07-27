package com.example.myapplication.screen.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.User
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.utils.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val tasksCount by viewModel.tasksCount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        viewModel.loadUnreadCount()
        viewModel.loadTasksCount()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Профиль",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки",
                            tint = MaterialTheme.colorScheme.onPrimary
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
        ) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ошибка: $error")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text("Повторить")
                        }
                    }
                }
                user != null -> {
                    ProfileContent(
                        user = user!!,
                        unreadCount = unreadCount,
                        tasksCount = tasksCount,
                        onItemClick = { route ->
                            navController.navigate(route)
                        },
                        onLogout = {
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Пользователь не найден")
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text("Обновить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    unreadCount: Int,
    tasksCount: Int,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ===== ШАПКА ПРОФИЛЯ =====
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Аватар
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = colors.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.name.take(1).uppercase(),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ФИО
                    Text(
                        text = "${user.surname} ${user.name} ${user.patronymic}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )

                    // Должность
                    if (user.postName.isNotEmpty()) {
                        Text(
                            text = user.postName,
                            fontSize = 14.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    // Отдел
                    if (user.departmentName.isNotEmpty()) {
                        Text(
                            text = "🏢 ${user.departmentName}",
                            fontSize = 14.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    // Статус
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (user.status == "active")
                            Color.Green.copy(alpha = 0.1f)
                        else
                            Color.Gray.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = if (user.status == "active") "🟢 Активен" else "⚪ Не в сети",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = if (user.status == "active") Color.Green else Color.Gray
                        )
                    }
                }
            }
        }

        // ===== СТАТИСТИКА =====
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    value = unreadCount.toString(),
                    label = "Уведомления",
                    icon = Icons.Default.Notifications,
                    color = colors.primary,
                    onClick = { onItemClick("notifications") },
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = tasksCount.toString(),
                    label = "Задачи",
                    icon = Icons.Default.Task,
                    color = colors.tertiary,
                    onClick = { onItemClick("tasks") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ===== МЕНЮ =====
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MenuItem(
                        icon = Icons.Default.Info,
                        title = "Личная информация",
                        subtitle = "Редактировать профиль",
                        onClick = { onItemClick("profile_info") }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(
                        icon = Icons.Default.Dashboard,
                        title = "Дашборд",
                        subtitle = "Статистика и задачи",
                        onClick = { onItemClick("dashboard") }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(
                        icon = Icons.Default.Notifications,
                        title = "Уведомления",
                        subtitle = if (unreadCount > 0) "$unreadCount непрочитанных" else "Нет новых",
                        badge = if (unreadCount > 0) unreadCount.toString() else null,
                        onClick = { onItemClick("notifications") }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(
                        icon = Icons.Default.Settings,
                        title = "Настройки",
                        subtitle = "Тема, безопасность",
                        onClick = { onItemClick("settings") }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(
                        icon = Icons.Default.Help,
                        title = "О приложении",
                        subtitle = "Версия 1.0.0",
                        onClick = { onItemClick("appinfo") }
                    )
                }
            }
        }

        // ===== ВЫХОД =====
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Выход",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Выйти из аккаунта",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (badge != null) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}