package com.example.myapplication.screen.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.material3.TextButtonDefaults
import androidx.wear.compose.material3.TextButton
import com.example.myapplication.data.mapper.ChatMapper
import com.example.myapplication.model.Chat
import com.example.myapplication.model.ChatFolder
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.utils.TokenManager
import com.google.ai.client.generativeai.type.content

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chats by viewModel.chats.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showChatContextMenu by remember { mutableStateOf(false) }
    var selectedChatId by remember { mutableStateOf<Int?>(null) }
    var selectedFolderId by remember { mutableStateOf<Int?>(null) }

    // Выбранная папка (по умолчанию "Все чаты" - null)
    var currentFolderId by remember { mutableStateOf<Int?>(null) }

    val currentUserId = TokenManager.getUserId().toString()
    val colors = MaterialTheme.colorScheme

    // Загрузка данных
    LaunchedEffect(Unit) {
        viewModel.loadChats(currentUserId)
        viewModel.loadFolders()
    }

    // Фильтрация чатов по папке и поиску
    val filteredChats = remember(chats, currentFolderId, searchQuery) {
        var filtered = if (currentFolderId == null) {
            chats
        } else {
            chats.filter { chat ->
                chat.folderId == currentFolderId
            }
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.participants.any { p ->
                            "${p.surname} ${p.name}".contains(searchQuery, ignoreCase = true)
                        }
            }
        }
        filtered
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateFolderDialog = true },
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Создать папку"
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
                        Text("Ошибка: $error", color = colors.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.loadChats(currentUserId)
                                viewModel.loadFolders()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Поиск
                        SearchBar(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        // Папки (горизонтальный скролл)
                        FoldersRow(
                            folders = folders,
                            currentFolderId = currentFolderId,
                            onFolderClick = { folderId ->
                                currentFolderId = folderId
                            },
                            onEditFolder = { folderId ->
                                // TODO: Открыть редактирование папки
                            },
                            onDeleteFolder = { folderId ->
                                viewModel.deleteFolder(folderId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        // Список чатов
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (filteredChats.isEmpty()) {
                                item {
                                    EmptyState(
                                        message = if (searchQuery.isNotEmpty()) {
                                            "Ничего не найдено"
                                        } else if (currentFolderId != null) {
                                            "В этой папке нет чатов"
                                        } else {
                                            "Нет чатов"
                                        },
                                        modifier = Modifier.padding(top = 32.dp)
                                    )
                                }
                            } else {
                                items(filteredChats) { chat ->
                                    ChatItem(
                                        chat = chat,
                                        onClick = {
                                            navController.navigate("chat/${chat.id}")
                                        },
                                        onLongClick = { chatId ->
                                            selectedChatId = chatId
                                            showChatContextMenu = true
                                        }
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог создания папки
    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onCreate = { folderName ->
                viewModel.createFolder(folderName)
                showCreateFolderDialog = false
            }
        )
    }

    // Контекстное меню для перемещения чата
    if (showChatContextMenu && selectedChatId != null) {
        ChatContextMenu(
            chatId = selectedChatId!!,
            folders = folders,
            onMoveToFolder = { folderId ->
                viewModel.moveChatToFolder(selectedChatId!!, folderId)
                showChatContextMenu = false
                selectedChatId = null
            },
            onDismiss = {
                showChatContextMenu = false
                selectedChatId = null
            }
        )
    }
}

@Composable
fun FoldersRow(
    folders: List<ChatFolder>,
    currentFolderId: Int?,
    onFolderClick: (Int?) -> Unit,
    onEditFolder: (Int) -> Unit,
    onDeleteFolder: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // "Все чаты"
        item {
            FolderChip(
                name = "Все чаты",
                isSelected = currentFolderId == null,
                onClick = { onFolderClick(null) }
            )
        }

        // Папки
        items(folders) { folder ->
            FolderChip(
                name = folder.name,
                isSelected = currentFolderId == folder.id,
                onClick = { onFolderClick(folder.id) },
                onEdit = { onEditFolder(folder.id) },
                onDelete = { onDeleteFolder(folder.id) }
            )
        }
    }
}

@Composable
fun FolderChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) colors.primary else colors.surfaceVariant,
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = name,
                color = if (isSelected) colors.onPrimary else colors.onSurface,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )

            if (onEdit != null && !isSelected) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (onDelete != null && !isSelected) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Удалить",
                        tint = colors.error,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = {
            Text(
                text = "Поиск чатов...",
                color = colors.onSurfaceVariant,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск",
                tint = colors.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Очистить",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outline,
            cursorColor = colors.primary
        ),
        shape = RoundedCornerShape(24.dp),
        singleLine = true
    )
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💬",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = colors.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Создайте папку или добавьте чаты",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant
        )
    }
}
// screen/Chat/ChatScreen.kt
@Composable
fun ChatItem(
    chat: Chat,
    onClick: () -> Unit,
    onLongClick: (Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val currentUserId = TokenManager.getUserId()

    // Форматируем последнее сообщение
    val lastMessageText = remember(chat) {
        when {
            chat.lastMessage.isNotEmpty() -> {
                if (chat.lastMessageUserId == currentUserId) {
                    "Вы: ${chat.lastMessage}"
                } else {
                    chat.lastMessage
                }
            }
            else -> "Нет сообщений"
        }
    }

    // Имя чата
    val chatName = if (chat.isGroup) {
        chat.name
    } else {
        // Для личного чата - если имя пустое, ставим заглушку
        chat.name.ifEmpty { "Пользователь" }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick(chat.id) }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (chat.isGroup) "👥" else chatName.take(1).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = chatName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface,
                    maxLines = 1
                )

                Text(
                    text = lastMessageText,
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Бейдж
            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📁 Создание папки",
                color = colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Введите название папки",
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = folderName,
                    onValueChange = { folderName = it },
                    placeholder = {
                        Text(
                            text = "Название папки...",
                            color = colors.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        cursorColor = colors.primary
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (folderName.isNotBlank()) {
                        onCreate(folderName)
                    }
                },
                enabled = folderName.isNotBlank()
            ) {
                Text(
                    text = "Создать",
                    color = if (folderName.isNotBlank()) colors.primary else colors.onSurfaceVariant
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Отмена",
                    color = colors.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun ChatContextMenu(
    chatId: Int,
    folders: List<ChatFolder>,
    onMoveToFolder: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📂 Переместить чат",
                color = colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // "Все чаты"
                TextButton(
                    onClick = { onMoveToFolder(null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextButtonDefaults.textButtonColors(
                        contentColor = colors.onSurface
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Все чаты")
                    }
                }

                Divider()

                folders.forEach { folder ->
                    TextButton(
                        onClick = { onMoveToFolder(folder.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextButtonDefaults.textButtonColors(
                            contentColor = colors.onSurface
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = colors.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(folder.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}