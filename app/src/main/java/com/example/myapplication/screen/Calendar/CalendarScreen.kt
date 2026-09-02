package com.example.myapplication.screen.Calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Используем rememberSaveable чтобы сохранить состояние при повороте
    var currentMonth by rememberSaveable { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showDateModal by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            AppTopBar(
                title = "Календарь",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(
                        onClick = {
                            currentMonth = Calendar.getInstance()
                            viewModel.loadTasks()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Сегодня",
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
                            onClick = { viewModel.loadTasks() },
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
                        // Календарь
                        item {
                            CalendarView(
                                currentMonth = currentMonth,
                                onPrevMonth = {
                                    // Создаем новую копию календаря
                                    val newCalendar = currentMonth.clone() as Calendar
                                    newCalendar.add(Calendar.MONTH, -1)
                                    currentMonth = newCalendar
                                },
                                onNextMonth = {
                                    val newCalendar = currentMonth.clone() as Calendar
                                    newCalendar.add(Calendar.MONTH, 1)
                                    currentMonth = newCalendar
                                },
                                onDateClick = { date ->
                                    selectedDate = date
                                    showDateModal = true
                                },
                                tasks = tasks,
                                colors = colors
                            )
                        }

                        // Задачи на сегодня
                        item {
                            TodayTasksSection(
                                tasks = tasks,
                                colors = colors
                            )
                        }
                    }
                }
            }
        }
    }

    // Модалка с задачами на дату
    if (showDateModal && selectedDate != null) {
        DateTasksModal(
            date = selectedDate!!,
            tasks = tasks,
            onDismiss = { showDateModal = false },
            onCreateTask = { /* TODO: Создать задачу */ },
            colors = colors
        )
    }
}

// ========== КАЛЕНДАРЬ ==========

@Composable
fun CalendarView(
    currentMonth: Calendar,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (Date) -> Unit,
    tasks: List<Task>,
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
            // Заголовок с навигацией
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Предыдущий месяц",
                        tint = colors.onSurface
                    )
                }
                Text(
                    text = formatMonthYear(currentMonth),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onBackground
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Следующий месяц",
                        tint = colors.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Дни недели
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val dayNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                dayNames.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Дни месяца
            val calendar = currentMonth.clone() as Calendar
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val today = Calendar.getInstance()
            val todayStr = SimpleDateFormat("yyyy-MM-dd").format(today.time)

            val offset = if (firstDayOfWeek == 0) 6 else firstDayOfWeek - 1
            val totalCells = kotlin.math.ceil((offset + daysInMonth) / 7.0).toInt() * 7

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (week in 0 until totalCells / 7) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (dayInWeek in 0 until 7) {
                            val dayIndex = week * 7 + dayInWeek
                            val dayNumber = dayIndex - offset + 1

                            if (dayIndex < offset || dayNumber > daysInMonth) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                )
                            } else {
                                val date = Calendar.getInstance().apply {
                                    time = currentMonth.time
                                    set(Calendar.DAY_OF_MONTH, dayNumber)
                                }
                                val dateStr = SimpleDateFormat("yyyy-MM-dd").format(date.time)
                                val isToday = dateStr == todayStr
                                val dayTasks = getTasksForDate(dateStr, tasks)
                                val deadlines = dayTasks.filter {
                                    it.dueDate == dateStr && it.status != "completed"
                                }

                                DateCell(
                                    day = dayNumber,
                                    isToday = isToday,
                                    tasksCount = dayTasks.size,
                                    deadlinesCount = deadlines.size,
                                    onClick = {
                                        onDateClick(date.time)
                                    },
                                    colors = colors,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Функция для форматирования месяца в именительном падеже
fun formatMonthYear(calendar: Calendar): String {
    val monthNames = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    return "${monthNames[month]} $year"
}

@Composable
fun DateCell(
    day: Int,
    isToday: Boolean,
    tasksCount: Int,
    deadlinesCount: Int,
    onClick: () -> Unit,
    colors: ColorScheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isToday) Modifier
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.15f))
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 14.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) colors.primary else colors.onSurface
                )

                if (tasksCount > 0 || deadlinesCount > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (tasksCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary)
                            )
                        }
                        if (deadlinesCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========== ЗАДАЧИ НА СЕГОДНЯ ==========

@Composable
fun TodayTasksSection(
    tasks: List<Task>,
    colors: ColorScheme
) {
    val today = Calendar.getInstance()
    val todayStr = SimpleDateFormat("yyyy-MM-dd").format(today.time)

    val todayTasks = tasks.filter {
        (it.startDate == todayStr || it.dueDate == todayStr) && it.status != "completed"
    }

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
                        imageVector = Icons.Default.Today,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Задачи на сегодня",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                    if (todayTasks.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = colors.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = todayTasks.size.toString(),
                                fontSize = 12.sp,
                                color = colors.primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (todayTasks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Нет задач на сегодня 🎉",
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            } else {
                todayTasks.forEachIndexed { index, task ->
                    TodayTaskItem(
                        task = task,
                        colors = colors
                    )
                    if (index < todayTasks.size - 1) {
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
fun TodayTaskItem(
    task: Task,
    colors: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Открыть задачу */ }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(2.dp))
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (task.assigneeName != null) {
                    Text(
                        text = "👤 ${task.assigneeName}",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                Text(
                    text = "🎯 ${getPriorityLabel(task.priority)}",
                    fontSize = 12.sp,
                    color = getPriorityColor(task.priority)
                )
            }
        }

        when (task.status) {
            "pending" -> Text("⏳", fontSize = 16.sp)
            "in_progress" -> Text("🔄", fontSize = 16.sp)
            "completed" -> Text("✅", fontSize = 16.sp)
            else -> Text("❌", fontSize = 16.sp)
        }
    }
}

// ========== МОДАЛКА ЗАДАЧ НА ДАТУ ==========

@Composable
fun DateTasksModal(
    date: Date,
    tasks: List<Task>,
    onDismiss: () -> Unit,
    onCreateTask: () -> Unit,
    colors: ColorScheme
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd").format(date)
    val dayTasks = getTasksForDate(dateStr, tasks)
    val todayTasks = dayTasks.filter { it.status != "completed" }
    val deadlines = todayTasks.filter { it.dueDate == dateStr }

    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
    val formattedDate = dateFormat.format(date)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "📅 ${formattedDate}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip(
                        count = todayTasks.size,
                        label = "Задачи",
                        color = colors.primary,
                        colors = colors
                    )
                    StatChip(
                        count = deadlines.size,
                        label = "Дедлайны",
                        color = Color(0xFFEF4444),
                        colors = colors
                    )
                }

                if (todayTasks.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Нет задач на этот день",
                            color = colors.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(todayTasks) { task ->
                            TaskRow(
                                task = task,
                                colors = colors
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCreateTask,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Создать задачу")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
fun StatChip(
    count: Int,
    label: String,
    color: Color,
    colors: ColorScheme
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = count.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    colors: ColorScheme
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        when (task.priority) {
                            "critical" -> Color(0xFFEF4444)
                            "high" -> Color(0xFFF59E0B)
                            "medium" -> Color(0xFF10B981)
                            else -> Color(0xFF3B82F6)
                        }
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "👤 ${task.assigneeName ?: "Не назначен"}",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "🎯 ${getPriorityLabel(task.priority)}",
                        fontSize = 11.sp,
                        color = getPriorityColor(task.priority)
                    )
                }
            }

            when (task.status) {
                "pending" -> Text("⏳", fontSize = 16.sp)
                "in_progress" -> Text("🔄", fontSize = 16.sp)
                "completed" -> Text("✅", fontSize = 16.sp)
                else -> Text("❌", fontSize = 16.sp)
            }
        }
    }
}

// ========== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ==========

fun getTasksForDate(dateStr: String, tasks: List<Task>): List<Task> {
    return tasks.filter { task ->
        task.startDate == dateStr || task.dueDate == dateStr
    }
}

fun getPriorityLabel(priority: String): String {
    return when (priority) {
        "critical" -> "Критический"
        "high" -> "Высокий"
        "medium" -> "Средний"
        else -> "Низкий"
    }
}

fun getPriorityColor(priority: String): Color {
    return when (priority) {
        "critical" -> Color(0xFFEF4444)
        "high" -> Color(0xFFF59E0B)
        "medium" -> Color(0xFF10B981)
        else -> Color(0xFF3B82F6)
    }
}