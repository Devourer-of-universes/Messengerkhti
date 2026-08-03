package com.example.myapplication.screen.Chat.Message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.utils.TokenManager
import com.example.myapplication.ui.theme.txtMainWhite
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    navController: NavController,
    chatId: String,
    viewModel: MessageViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val chatName by viewModel.chatName.collectAsState()
    val chatAvatar by viewModel.chatAvatar.collectAsState()

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
        viewModel.loadChatInfo(chatId)
    }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chatAvatar.take(1).uppercase(),
                                color = colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = chatName.ifEmpty { "Чат" },
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = colors.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("chat_info/$chatId")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Информация",
                            tint = colors.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    scrolledContainerColor = colors.surface
                )
            )
        },
        bottomBar = {
            MessageInput(
                onSendMessage = { content ->
                    viewModel.sendMessage(chatId, content)
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
                isLoading && messages.isEmpty() -> LoadingIndicator()
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ошибка: $error", color = colors.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadMessages(chatId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
                else -> {
                    MessageList(
                        messages = messages,
                        currentUserId = TokenManager.getUserId()
                    )
                }
            }
        }
    }
}

@Composable
fun MessageList(
    messages: List<ChatMessage>,
    currentUserId: Int
) {
    val listState = rememberLazyListState()
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        state = listState,
        reverseLayout = false,
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        var lastDate: String? = null

        items(messages) { message ->
            val messageDate = formatMessageDate(message.createdAt)
            val showDate = lastDate != messageDate

            if (showDate) {
                DateSeparator(date = message.createdAt)
                lastDate = messageDate
            }

            val isOwn = message.userId == currentUserId

            MessageItem(
                message = message,
                isOwn = isOwn,
                showAvatar = !isOwn
            )
        }
    }
}

@Composable
fun DateSeparator(date: String) {
    val colors = MaterialTheme.colorScheme
    val formattedDate = formatDateHeader(date)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}
@Composable
fun MessageItem(
    message: ChatMessage,
    isOwn: Boolean,
    showAvatar: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val currentUserId = TokenManager.getUserId()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        // Блок сообщения
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)

        ) {
            // Имя отправителя (для чужих)
            if (!isOwn) {
                Text(
                    text = "${message.surname} ${message.name}".trim(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }

            // Сообщение с аватаром внутри
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
            ) {
                // Аватар внутри блока (для чужих) - слева
                if (showAvatar) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.2f))
                            .align(Alignment.Bottom),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.surname.take(1).uppercase(),
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Текст сообщения
                Surface(
                    shape = RoundedCornerShape(
                        topStart = if (isOwn) 16.dp else 4.dp,
                        topEnd = if (isOwn) 4.dp else 16.dp,
                        bottomStart = if (isOwn) 16.dp else 12.dp,
                        bottomEnd = if (isOwn) 12.dp else 16.dp
                    ),
                    color = if (isOwn) colors.primary else colors.surfaceVariant,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    ) {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isOwn) colors.onPrimary else colors.onSurface,
                            fontSize = 15.sp,
                            lineHeight = 20.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(message.createdAt),
                                fontSize = 11.sp,
                                color = if (isOwn) {
                                    colors.onPrimary.copy(alpha = 0.7f)
                                } else {
                                    colors.onSurfaceVariant.copy(alpha = 0.7f)
                                }
                            )

                            if (isOwn) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Отправлено",
                                    tint = colors.onPrimary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // Отступ справа для своих (симметрично аватару)
                if (isOwn) {
                    Spacer(modifier = Modifier.width(34.dp))
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    onAttachFile: () -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme
    val c_bgtxt = colors.onBackground
    val c_surf = colors.surface
    val c_surftxt = colors.onSurface
    val c_acc = colors.primary
    val c_accmin = colors.secondary

    // Контейнер в стиле основного футера
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(c_accmin), // Полу-акцентный фон (secondary)
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Кнопка прикрепления файла (акцентная круглая)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(c_acc),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onAttachFile,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Прикрепить файл",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Поле ввода (белое)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        color = c_bgtxt
                    ),
                    cursorBrush = SolidColor(c_acc),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (text.isEmpty()) {
                                Text(
                                    text = "Введите сообщение...",
                                    color = c_surftxt.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            // Кнопка отправки (акцентная круглая)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (text.isNotBlank()) c_acc else c_surftxt.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendMessage(text)
                            text = ""
                        }
                    },
                    enabled = text.isNotBlank(),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Отправить",
                        tint = if (text.isNotBlank()) Color.White else c_surftxt.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ========== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ==========
// В конце файла MessageScreen.kt добавляем/исправляем

// ========== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ==========

fun formatDateHeader(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString) ?: return dateString

        val today = Calendar.getInstance()
        val messageDate = Calendar.getInstance().apply {
            time = date
        }

        // Русские названия месяцев
        val monthNames = arrayOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        )

        when {
            today.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == messageDate.get(Calendar.DAY_OF_YEAR) -> "Сегодня"

            today.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) - messageDate.get(Calendar.DAY_OF_YEAR) == 1 -> "Вчера"

            else -> {
                val day = messageDate.get(Calendar.DAY_OF_MONTH)
                val month = monthNames[messageDate.get(Calendar.MONTH)]
                val year = messageDate.get(Calendar.YEAR)
                val currentYear = today.get(Calendar.YEAR)

                if (year == currentYear) {
                    "$day $month"
                } else {
                    "$day $month $year"
                }
            }
        }
    } catch (e: Exception) {
        dateString
    }
}

fun formatMessageDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString) ?: return dateString

        val calendar = Calendar.getInstance().apply {
            time = date
        }

        val monthNames = arrayOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        )

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = monthNames[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        if (year == currentYear) {
            "$day $month"
        } else {
            "$day $month $year"
        }
    } catch (e: Exception) {
        dateString
    }
}

fun formatTime(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString) ?: return dateString

        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}