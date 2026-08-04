package com.example.myapplication.screen.Chat.Message

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.ChatParticipant
import com.example.myapplication.model.MediaItem  // ← Импорт из model
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.utils.TokenManager

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

    // Состояние для вкладок медиа
    var selectedMediaTab by remember { mutableStateOf(0) }
    val mediaTabs = listOf("Изображения", "Файлы", "Ссылки")

    LaunchedEffect(chatId) {
        viewModel.loadChatInfo(chatId)
        viewModel.loadMedia(chatId)
    }

    val colors = MaterialTheme.colorScheme
    val currentUserId = TokenManager.getUserId()

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Информация о чате",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    scrolledContainerColor = colors.surface
                )
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
                            onClick = { viewModel.loadChatInfo(chatId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
                chatInfo != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // Шапка с аватаром и названием
                        item {
                            ChatInfoHeader(
                                name = chatInfo!!.name,
                                isGroup = chatInfo!!.isGroup,
                                avatarText = chatInfo!!.name.take(1).uppercase(),
                                participantsCount = chatInfo!!.participants.size
                            )
                        }

                        // Участники (для группы)
                        if (chatInfo!!.isGroup) {
                            item {
                                ParticipantsSection(
                                    participants = chatInfo!!.participants,
                                    currentUserId = currentUserId.toString()
                                )
                            }
                        }

                        // Медиа блок с вкладками
                        item {
                            MediaSection(
                                selectedTab = selectedMediaTab,
                                onTabSelected = { selectedMediaTab = it },
                                tabs = mediaTabs,
                                images = viewModel.images.value,
                                files = viewModel.files.value,
                                links = viewModel.links.value
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInfoHeader(
    name: String,
    isGroup: Boolean,
    avatarText: String,
    participantsCount: Int
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Аватар
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatarText,
                color = colors.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Название
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )

        if (isGroup) {
            Text(
                text = "$participantsCount участников",
                fontSize = 14.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ParticipantsSection(
    participants: List<ChatParticipant>,
    currentUserId: String
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Участники (${participants.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colors.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                participants.forEachIndexed { index, participant ->
                    ParticipantItem(
                        participant = participant,
                        isCurrentUser = participant.id.toString() == currentUserId
                    )
                    if (index < participants.size - 1) {
                        Divider(
                            color = colors.onSurface.copy(alpha = 0.1f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantItem(
    participant: ChatParticipant,
    isCurrentUser: Boolean
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Аватар
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = participant.name.take(1).uppercase(),
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Имя
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${participant.surname} ${participant.name}".trim(),
                    fontSize = 15.sp,
                    color = colors.onSurface
                )
                if (isCurrentUser) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colors.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Вы",
                            fontSize = 11.sp,
                            color = colors.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            if (participant.isOnline) {
                Text(
                    text = "🟢 В сети",
                    fontSize = 12.sp,
                    color = Color.Green
                )
            }
        }

        // Статус онлайн
        if (participant.isOnline) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.Green)
            )
        }
    }
}
@Composable
fun MediaSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    images: List<MediaItem>,  // ← Используем MediaItem из model
    files: List<MediaItem>,   // ← Используем MediaItem из model
    links: List<MediaItem>    // ← Используем MediaItem из model
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Медиа",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Вкладки
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = colors.surface,
            contentColor = colors.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        // Контент вкладок
        when (selectedTab) {
            0 -> MediaImagesContent(images = images)
            1 -> MediaFilesContent(files = files)
            2 -> MediaLinksContent(links = links)
        }
    }
}

@Composable
fun MediaImagesContent(images: List<MediaItem>) {  // ← MediaItem из model
    val colors = MaterialTheme.colorScheme

    if (images.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет изображений",
                color = colors.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        return
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) { image ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceVariant)
                    .clickable {
                        // TODO: Открыть просмотр изображения
                    },
                contentAlignment = Alignment.Center
            ) {
                // Заглушка для изображения
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )
                // TODO: Загружать реальные изображения через Coil/Glide
            }
        }
    }
}

@Composable
fun MediaFilesContent(files: List<MediaItem>) {  // ← MediaItem из model
    val colors = MaterialTheme.colorScheme

    if (files.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет файлов",
                color = colors.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        files.forEach { file ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        // TODO: Скачать/открыть файл
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = file.name,
                        fontSize = 14.sp,
                        color = colors.onSurface
                    )
                    Text(
                        text = file.size,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                Text(
                    text = file.date,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }
            Divider(color = colors.onSurface.copy(alpha = 0.1f))
        }
    }
}

@Composable
fun MediaLinksContent(links: List<MediaItem>) {  // ← MediaItem из model
    val colors = MaterialTheme.colorScheme

    if (links.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет ссылок",
                color = colors.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        links.forEach { link ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        // TODO: Открыть ссылку
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = link.name,
                        fontSize = 14.sp,
                        color = colors.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = link.url ?: "",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Text(
                    text = link.date,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }
            Divider(color = colors.onSurface.copy(alpha = 0.1f))
        }
    }
}

