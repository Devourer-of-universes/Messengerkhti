package com.example.myapplication.screen.Chat.Message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.ChatParticipant
import com.example.myapplication.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoScreen(
    navController: NavController,
    chatId: String,
    viewModel: ChatInfoViewModel = hiltViewModel()
) {
    val chatInfo by viewModel.chatInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(chatId) {
        viewModel.loadChatInfo(chatId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Информация о чате") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
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
                        Button(onClick = { viewModel.loadChatInfo(chatId) }) {
                            Text("Повторить")
                        }
                    }
                }
                chatInfo != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            ChatInfoHeader(chatInfo = chatInfo!!)
                        }

                        item {
                            ChatInfoDetails(chatInfo = chatInfo!!)
                        }

                        if (chatInfo!!.participants.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Участники (${chatInfo!!.participants.size})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            items(chatInfo!!.participants) { participant ->
                                ParticipantItem(participant = participant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInfoHeader(chatInfo: com.example.myapplication.model.ChatInfo) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = chatInfo.name.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = chatInfo.name,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = if (chatInfo.isGroup) "Групповой чат" else "Личный чат",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ChatInfoDetails(chatInfo: com.example.myapplication.model.ChatInfo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailRow(
                label = "Тип",
                value = if (chatInfo.isGroup) "Групповой" else "Личный"
            )
            DetailRow(
                label = "Создан",
                value = chatInfo.createdAt.toString() // TODO: форматировать дату
            )
            if (chatInfo.messageCount > 0) {
                DetailRow(
                    label = "Сообщений",
                    value = chatInfo.messageCount.toString()
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ParticipantItem(participant: ChatParticipant) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = participant.name.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column {
                Text(
                    text = "${participant.surname} ${participant.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (participant.isOnline) {
                    Text(
                        text = "🟢 В сети",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}