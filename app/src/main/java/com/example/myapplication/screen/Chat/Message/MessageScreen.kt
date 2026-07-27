package com.example.myapplication.screen.Chat.Message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.ui.components.LoadingIndicator

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

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Чат") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("chat_info/$chatId")
                    }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Info,
                            contentDescription = "Информация"
                        )
                    }
                }
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
        ) {
            when {
                isLoading && messages.isEmpty() -> LoadingIndicator()
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ошибка: $error")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadMessages(chatId) }) {
                            Text("Повторить")
                        }
                    }
                }
                else -> MessageList(messages = messages)
            }
        }
    }
}

@Composable
fun MessageList(messages: List<ChatMessage>) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        reverseLayout = false,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(messages) { message ->
            MessageItem(
                message = message,
                isOwn = message.userId == 0 // TODO: Сравнить с ID текущего пользователя
            )
        }
    }
}

@Composable
fun MessageItem(
    message: ChatMessage,
    isOwn: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isOwn)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
            ) {
                if (!isOwn) {
                    Text(
                        text = message.surname,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOwn)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message.createdAt.take(5), // Время
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOwn)
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Введите сообщение...") },
            singleLine = true
        )

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onSendMessage(text)
                    text = ""
                }
            },
            enabled = text.isNotBlank()
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send,
                contentDescription = "Отправить"
            )
        }
    }
}