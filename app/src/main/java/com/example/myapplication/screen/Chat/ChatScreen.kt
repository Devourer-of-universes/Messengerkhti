package com.example.myapplication.screen.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
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
import com.example.myapplication.model.Chat
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.utils.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chats by viewModel.chats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val currentUserId = TokenManager.getUserId().toString()

    LaunchedEffect(Unit) {
        viewModel.loadChats(currentUserId)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Открыть создание чата */ }
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = "Создать чат"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && chats.isEmpty() -> LoadingIndicator()
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Ошибка: $error")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadChats(currentUserId) }) {
                            Text("Повторить")
                        }
                    }
                }
                chats.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Нет чатов", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Начните общение с коллегами", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(chats) { chat ->
                            ChatItem(
                                chat = chat,
                                onClick = {
                                    navController.navigate("chat/${chat.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    chat: Chat,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = colors.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (chat.isGroup) {
                        Text(
                            text = "👥",
                            fontSize = 24.sp
                        )
                    } else {
                        val participant = chat.participants.firstOrNull()
                        Text(
                            text = participant?.name?.take(1)?.uppercase() ?: "?",
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = chat.name.ifEmpty {
                            chat.participants.joinToString(", ") { "${it.surname} ${it.name}" }
                        },
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    if (chat.unreadCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = colors.primary,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = chat.unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = chat.lastMessage?.content ?: "Нет сообщений",
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}