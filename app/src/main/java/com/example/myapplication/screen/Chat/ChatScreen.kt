package com.example.myapplication.screen.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Chat
import com.example.myapplication.model.ChatFolder
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.ui.theme.txtMainGreyDark
import com.example.myapplication.ui.theme.txtMainGreyLight
import com.example.myapplication.utils.TokenManager
import com.example.myapplication.ui.theme.txtMainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chats by viewModel.chats.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var currentFolderId by remember { mutableStateOf<Int?>(null) }

    // Диалоги
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showCreateChatDialog by remember { mutableStateOf(false) }
    var showFolderMenuDialog by remember { mutableStateOf(false) }
    var showChatMenuDialog by remember { mutableStateOf(false) }
    var selectedFolderId by remember { mutableStateOf<Int?>(null) }
    var selectedChatId by remember { mutableStateOf<Int?>(null) }
    var selectedChatName by remember { mutableStateOf("") }

    val currentUserId = TokenManager.getUserId().toString()
    val colors = MaterialTheme.colorScheme

    // Загрузка данных
    LaunchedEffect(Unit) {
        viewModel.loadChats(currentUserId)
        viewModel.loadFolders()
    }

    // Фильтрация чатов
    val filteredChats = remember(chats, searchQuery, currentFolderId) {
        var filtered = if (currentFolderId == null) {
            chats
        } else {
            chats.filter { it.folderId == currentFolderId }
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.name?.contains(searchQuery, ignoreCase = true) == true ||
                        it.participants.any { p ->
                            "${p.surname} ${p.name}".contains(searchQuery, ignoreCase = true)
                        }
            }
        }
        filtered
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Поиск
            item {
                SearchBarOld(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 0.dp)
                )
            }

            // Папки (горизонтальный скролл с кнопкой "+")
            item {
                FoldersRowWithAdd(
                    folders = folders,
                    currentFolderId = currentFolderId,
                    onFolderClick = { folderId ->
                        currentFolderId = folderId
                    },
                    onFolderLongClick = { folderId ->
                        selectedFolderId = folderId
                        showFolderMenuDialog = true
                    },
                    onAddFolder = {
                        showCreateFolderDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // Список чатов
            when {
                isLoading && chats.isEmpty() -> {
                    item { LoadingIndicator() }
                }
                error != null -> {
                    item {
                        Text(
                            text = "Ошибка: $error",
                            color = colors.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                filteredChats.isEmpty() -> {
                    item {
                        EmptyStateOld(
                            message = if (searchQuery.isNotEmpty()) {
                                "Ничего не найдено"
                            } else {
                                "Нет чатов в этой папке"
                            }
                        )
                    }
                }
                else -> {
                    items(filteredChats) { chat ->
                        ChatItemOld(
                            chat = chat,
                            onClick = {
                                navController.navigate("chat/${chat.id}")
                            },
                            onLongClick = { chatId ->
                                selectedChatId = chatId
                                selectedChatName = chat.name!!
                                showChatMenuDialog = true
                            }
                        )
                    }
                }
            }

            // Отступ снизу
            item {
                Spacer(modifier = Modifier.height(80.dp))

            }
//            item {
//                Box(modifier = Modifier.fillMaxSize()){
//
//                }
//
//            }
        }
        FloatingActionButton(
            onClick = { showCreateChatDialog = true },
            containerColor = colors.primary,
            contentColor = colors.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "Создать чат",
                tint = Color.White
            )
        }
    }

    // Диалог создания папки
    if (showCreateFolderDialog) {
        CreateFolderDialogOld(
            onDismiss = { showCreateFolderDialog = false },
            onCreate = { folderName ->
                viewModel.createFolder(folderName)
                showCreateFolderDialog = false
            }
        )
    }

    // Диалог создания чата
    if (showCreateChatDialog) {
        CreateChatDialog(
            onDismiss = { showCreateChatDialog = false },
            onCreate = { chatName, userIds ->
                // TODO: Создать чат
                viewModel.createChat(chatName, userIds)
                showCreateChatDialog = false
            }
        )
    }

    // Меню для папки (долгое нажатие)
    if (showFolderMenuDialog && selectedFolderId != null) {
        val folder = folders.find { it.id == selectedFolderId }
        FolderMenuDialog(
            folderName = folder?.name ?: "",
            onRename = {
                // TODO: Переименовать папку
                showFolderMenuDialog = false
            },
            onDelete = {
                viewModel.deleteFolder(selectedFolderId!!)
                showFolderMenuDialog = false
                if (currentFolderId == selectedFolderId) {
                    currentFolderId = null
                }
                selectedFolderId = null
            },
            onDismiss = {
                showFolderMenuDialog = false
                selectedFolderId = null
            }
        )
    }

    // Меню для чата (долгое нажатие)
    if (showChatMenuDialog && selectedChatId != null) {
        ChatMenuDialog(
            chatName = selectedChatName,
            folders = folders,
            currentFolderId = currentFolderId,
            onMoveToFolder = { folderId ->
                viewModel.moveChatToFolder(selectedChatId!!, folderId)
                showChatMenuDialog = false
                selectedChatId = null
                selectedChatName = ""
            },
            onDelete = {
                // TODO: Удалить чат
                showChatMenuDialog = false
                selectedChatId = null
                selectedChatName = ""
            },
            onDismiss = {
                showChatMenuDialog = false
                selectedChatId = null
                selectedChatName = ""
            }
        )
    }
}

// ========== ПАПКИ С КНОПКОЙ "+" ==========

@Composable
fun FoldersRowWithAdd(
    folders: List<ChatFolder>,
    currentFolderId: Int?,
    onFolderClick: (Int?) -> Unit,
    onFolderLongClick: (Int) -> Unit,
    onAddFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Горизонтальный список папок
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            // "Все чаты"
            item {
                FolderChipOld(
                    name = "Все чаты",
                    isSelected = currentFolderId == null,
                    onClick = { onFolderClick(null) }
                )
            }

            // Папки
            items(folders) { folder ->
                FolderChipOld(
                    name = folder.name,
                    isSelected = currentFolderId == folder.id,
                    onClick = { onFolderClick(folder.id) },
                    onLongClick = { onFolderLongClick(folder.id) }
                )
            }
        }

        // Кнопка "+" для создания папки
        IconButton(
            onClick = onAddFolder,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Создать папку",
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FolderChipOld(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme

    val modifier = if (onLongClick != null) {
        Modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    } else {
        Modifier.clickable { onClick() }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) colors.primary else colors.surfaceVariant,
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
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
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

// ========== ДИАЛОГИ ==========

@Composable
fun FolderMenuDialog(
    folderName: String,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📁 $folderName",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onRename,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Переименовать")
                    }
                }

                Divider()

                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Удалить папку",
                            color = MaterialTheme.colorScheme.error
                        )
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

@Composable
fun ChatMenuDialog(
    chatName: String,
    folders: List<ChatFolder>,
    currentFolderId: Int?,
    onMoveToFolder: (Int?) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "💬 $chatName",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Переместить в папку:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // "Все чаты"
                TextButton(
                    onClick = { onMoveToFolder(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = if (currentFolderId == null)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Все чаты",
                            color = if (currentFolderId == null)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                        if (currentFolderId == null) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                folders.forEach { folder ->
                    TextButton(
                        onClick = { onMoveToFolder(folder.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = if (currentFolderId == folder.id)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = folder.name,
                                color = if (currentFolderId == folder.id)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                            if (currentFolderId == folder.id) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Удалить чат",
                            color = MaterialTheme.colorScheme.error
                        )
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

@Composable
fun CreateChatDialog(
    onDismiss: () -> Unit,
    onCreate: (String, List<Int>) -> Unit
) {
    var chatName by remember { mutableStateOf("") }
    var isGroup by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedUsers by remember { mutableStateOf<List<Int>>(emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isGroup) "👥 Создать группу" else "💬 Создать чат",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Тип чата
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isGroup,
                        onClick = { isGroup = false },
                        label = { Text("Личный") }
                    )
                    FilterChip(
                        selected = isGroup,
                        onClick = { isGroup = true },
                        label = { Text("Группа") }
                    )
                }

                // Название (для группы)
                if (isGroup) {
                    OutlinedTextField(
                        value = chatName,
                        onValueChange = { chatName = it },
                        label = { Text("Название группы") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Поиск пользователей
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Поиск пользователей") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true
                )

                // TODO: Список пользователей для выбора
                Text(
                    text = "Выбрано: ${selectedUsers.size} участников",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isGroup && chatName.isBlank()) {
                        // Показать ошибку
                    } else {
                        onCreate(chatName, selectedUsers)
                    }
                },
                enabled = if (isGroup) chatName.isNotBlank() else selectedUsers.isNotEmpty()
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

// ========== ВСПОМОГАТЕЛЬНЫЕ КОМПОНЕНТЫ ==========

@Composable
fun SearchBarOld(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(24.dp))
            .background(c_acc),
        contentAlignment = Alignment.Center
    ) {
        // Белая строка внутри
        Row(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 16.dp)
                .clip(shape = RoundedCornerShape(12.dp))
                .background(txtMainWhite),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск",
                tint = txtMainGreyDark,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // TextField внутри белого фона
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        text = "Поиск чата...",
                        color = txtMainGreyDark.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Transparent),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = txtMainGreyDark,
                    unfocusedTextColor = txtMainGreyDark,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = txtMainGreyDark,
                    focusedLabelColor = Color.Transparent,
                    unfocusedLabelColor = Color.Transparent,
                    focusedPlaceholderColor = txtMainGreyDark.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = txtMainGreyDark.copy(alpha = 0.6f)
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 14.sp,
                    color = txtMainGreyDark,
                    lineHeight = 20.sp
                )
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchChange("") },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Очистить",
                        tint = txtMainGreyDark.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatItemOld(
    chat: Chat,
    onClick: () -> Unit,
    onLongClick: (Int) -> Unit
) {
    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее). На нём уже все элементы
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет
    val c_accmin = MaterialTheme.colorScheme.secondary
    val currentUserId = TokenManager.getUserId()

    val chatName: String? = if (chat.isGroup) {
        chat.name
    } else {
        val otherUser = chat.participants.find { it.id != currentUserId }
        otherUser?.let {
            "${it.surname} ${it.name}".trim()
        } ?: chat.name?.ifEmpty { "Пользователь" }
    }

    val avatarText = if (chat.isGroup) {
        // Для группы - первая буква названия группы
        chat.name?.take(1)!!.uppercase()
    } else {
        // Для личного - первая буква имени собеседника
        val otherUser = chat.participants.find { it.id != currentUserId }
        val name = otherUser?.name ?: chat.name
        if (name!!.isNotEmpty()) name.take(1).uppercase() else "?"
        // Если имя есть - берем первую букву, иначе "?"
    }

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

    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(c_surf)
                .fillMaxWidth(0.9f)
                .height(75.dp)
                .padding(horizontal = 8.dp)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { onLongClick(chat.id) }
                )
                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(50.dp)
                    .background(c_accmin)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarText,
                    color = c_acc,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = chatName!!,
                    fontSize = 20.sp,
                    color = c_bgtxt,
                    maxLines = 1
                )
                Text(
                    text = lastMessageText,
                    fontSize = 14.sp,
                    color = c_surftxt,
                    maxLines = 1
                )
            }

            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(c_acc),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


    }
}

@Composable
fun EmptyStateOld(message: String) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "💬",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Создайте чат или добавьте участников",
            fontSize = 14.sp,
            color = colors.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun CreateFolderDialogOld(
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