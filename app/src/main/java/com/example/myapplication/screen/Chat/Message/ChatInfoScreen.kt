package com.example.myapplication.screen.Chat.Message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.model.ChatParticipant
import com.example.myapplication.model.MediaItem
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.ui.theme.txtMainWhite
import com.example.myapplication.utils.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoScreen(
    navController: NavController,
    chatId: String,
    viewModel: ChatInfoViewModel = hiltViewModel()
) {
    val chatInfo by viewModel.chatInfo.collectAsState()
    val images by viewModel.images.collectAsState()
    val files by viewModel.files.collectAsState()
    val links by viewModel.links.collectAsState()
    val gifs by viewModel.gifs.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Медиа", "Файлы", "Ссылки")

    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary
    LaunchedEffect(chatId) {
        viewModel.loadChatInfo(chatId)
        viewModel.loadMedia(chatId)
    }

    val colors = MaterialTheme.colorScheme
    val currentUserId = TokenManager.getUserId()
    val onImageClick: (String, String) -> Unit = { imageUrl, fileName ->
        val encodedUrl = java.net.URLEncoder.encode(imageUrl, "UTF-8")
        val encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8")
        navController.navigate("image_viewer/$encodedUrl/$encodedFileName")
    }
    Scaffold(
        containerColor = c_acc,
        topBar = {
            TopAppBar(
                title = {
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = txtMainWhite,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = colors.surface
                )
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
//                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // Шапка с аватаром
                        item {
                            ChatInfoHeader(
                                chatInfo = chatInfo!!,
                                colors = colors
                            )
                        }

                        // Действия с чатом
                        item {
                            ChatActionsRow(colors = colors)
                        }

                        // Участники (только для группы, если меньше 5)
                        if (chatInfo!!.isGroup && chatInfo!!.participants.size <= 5) {
                            item {
                                ParticipantsSection(
                                    participants = chatInfo!!.participants,
                                    currentUserId = currentUserId.toString(),
                                    colors = colors
                                )
                            }
                        }

                        // Медиа секция с вкладками
                        item {
                            MediaSection(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                                tabs = tabs,
                                images = images,
                                gifs = gifs,
                                videos = videos,
                                files = files,
                                links = links,
                                participants = if (chatInfo!!.isGroup && chatInfo!!.participants.size > 5) {
                                    chatInfo!!.participants
                                } else {
                                    emptyList()
                                },
                                currentUserId = currentUserId.toString(),
                                colors = colors,
                                onImageClick = onImageClick,
                                modifier = Modifier.fillMaxSize()
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
    chatInfo: com.example.myapplication.model.ChatInfo,
    colors: ColorScheme
) {
    val currentUserId = TokenManager.getUserId().toString()

    val (displayName, avatarLetter) = remember(chatInfo) {
        if (chatInfo.isGroup) {
            val name = chatInfo.name.ifEmpty { "Группа" }
            name to name.take(1).uppercase()
        } else {
            val otherUser = chatInfo.participants.find { it.id != currentUserId.toInt() }
            if (otherUser != null) {
                val name = "${otherUser.surname} ${otherUser.name}".trim()
                if (name.isNotEmpty()) {
                    name to otherUser.name.take(1).uppercase()
                } else {
                    "Пользователь" to "?"
                }
            } else {
                val name = chatInfo.name.ifEmpty { "Чат" }
                name to name.take(1).uppercase()
            }
        }
    }
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                .background(color = c_bg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatarLetter,
                color = c_accmin,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = displayName,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = txtMainWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (chatInfo.isGroup) {
            Text(
                text = "${chatInfo.participants.size} участников",
                fontSize = 14.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatActionsRow(colors: ColorScheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ChatActionButton(
            icon = Icons.Default.Notifications,
            label = "Уведомления",
            onClick = { /* TODO */ },
            colors = colors
        )
        ChatActionButton(
            icon = Icons.Default.Search,
            label = "Поиск",
            onClick = { /* TODO */ },
            colors = colors
        )
        ChatActionButton(
            icon = Icons.Default.PersonAdd,
            label = "Добавить",
            onClick = { /* TODO */ },
            colors = colors
        )
        ChatActionButton(
            icon = Icons.Default.MoreHoriz,
            label = "Ещё",
            onClick = { /* TODO */ },
            colors = colors
        )
    }
}

@Composable
fun ChatActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    colors: ColorScheme
) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .size(48.dp).border(color = txtMainWhite, width = 2.dp, shape = CircleShape)
                ,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = txtMainWhite,
                modifier = Modifier.clip(CircleShape).size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = txtMainWhite,
            maxLines = 1
        )
    }
}

@Composable
fun ParticipantsSection(
    participants: List<ChatParticipant>,
    currentUserId: String,
    colors: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Участники (${participants.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colors.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            participants.forEachIndexed { index, participant ->
                ParticipantItem(
                    participant = participant,
                    isCurrentUser = participant.id == currentUserId.toInt(),
                    colors = colors
                )
                if (index < participants.size - 1) {
                    Divider(
                        color = colors.onSurface.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ParticipantItem(
    participant: ChatParticipant,
    isCurrentUser: Boolean,
    colors: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Профиль участника */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = participant.name.take(1).uppercase(),
                color = colors.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${participant.surname} ${participant.name}".trim(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onBackground,
                    maxLines = 1
                )
                if (isCurrentUser) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colors.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Вы",
                            fontSize = 10.sp,
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
                    color = Color(0xFF34A853)
                )
            }
        }

        if (participant.isOnline) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF34A853))
            )
        }
    }
}

@Composable
fun MediaSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    images: List<MediaItem>,
    gifs: List<MediaItem>,
    videos: List<MediaItem>,
    files: List<MediaItem>,
    links: List<MediaItem>,
    participants: List<ChatParticipant>,
    currentUserId: String,
    colors: ColorScheme,
    onImageClick: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding( vertical = 8.dp)
            .clip(RoundedCornerShape(topEnd = 48.dp, topStart = 48.dp))
            .background(color = c_surf)
    ) {
        // Если участников больше 5, показываем их в медиасекции
        if (participants.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Участники (${participants.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackground
                    )
                    TextButton(
                        onClick = { /* TODO: Показать всех */ }
                    ) {
                        Text("Показать всех", fontSize = 13.sp, color = colors.primary)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    participants.take(5).forEachIndexed { index, participant ->
                        ParticipantItem(
                            participant = participant,
                            isCurrentUser = participant.id == currentUserId.toInt(),
                            colors = colors
                        )
                        if (index < participants.size - 1 && index < 4) {
                            Divider(
                                color = colors.onSurface.copy(alpha = 0.08f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    if (participants.size > 5) {
                        Divider(
                            color = colors.onSurface.copy(alpha = 0.08f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* TODO: Показать всех */ }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Показать всех (${participants.size})",
                                fontSize = 14.sp,
                                color = colors.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Вкладки медиа
//        Text(
//            text = "Медиа",
//            fontSize = 16.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = colors.onBackground,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )

        // Вкладки
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surfaceVariant
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = colors.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .padding(horizontal = 4.dp),
                        height = 2.dp,
                        color = colors.primary,
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    val count = when (index) {
                        0 -> images.size + gifs.size + videos.size
                        1 -> files.size
                        2 -> links.size
                        else -> 0
                    }
                    Tab(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Text(
                                text = if (count > 0) "$title $count" else title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal,
                                color = if (selectedTab == index) colors.primary else colors.onSurface
                            )
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Контент вкладок
        when (selectedTab) {
            0 -> MediaGridContent(
                images = images,
                gifs = gifs,
                videos = videos,
                colors = colors,
                onImageClick = onImageClick
            )
            1 -> MediaFilesList(files = files, colors = colors)
            2 -> MediaLinksList(links = links, colors = colors)
        }
    }
}

@Composable
fun MediaGridContent(
    images: List<MediaItem>,
    gifs: List<MediaItem>,
    videos: List<MediaItem>,
    colors: ColorScheme,
    onImageClick: (String, String) -> Unit = { _, _ -> }
) {
    val allMedia = images + gifs + videos

    if (allMedia.isEmpty()) {
        EmptyMediaState(text = "Нет медиа", colors = colors)
        return
    }

    val baseUrl = "http://10.0.2.2:3000"
    val context = LocalContext.current

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(allMedia) { item ->
            val fullImageUrl = if (item.url?.startsWith("/uploads/") == true) {
                baseUrl + item.url
            } else {
                item.url ?: ""
            }
            val fileName = item.url?.substringAfterLast("/") ?: "image"

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceVariant)
                    .clickable {
                        if (item.type != "video") {
                            onImageClick(fullImageUrl, fileName)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Реальное изображение через Coil
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(fullImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(android.R.drawable.ic_menu_gallery)
                )

                // Бейдж для GIF и видео
                when {
                    item.type == "gif" -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "GIF",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    item.type == "video" -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "🎬",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaFilesList(
    files: List<MediaItem>,
    colors: ColorScheme
) {
    if (files.isEmpty()) {
        EmptyMediaState(text = "Нет файлов", colors = colors)
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        files.forEach { file ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: Открыть файл */ }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.InsertDriveFile,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = file.name,
                        fontSize = 14.sp,
                        color = colors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = file.size,
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = file.date,
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Divider(
                color = colors.onSurface.copy(alpha = 0.08f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun MediaLinksList(
    links: List<MediaItem>,
    colors: ColorScheme
) {
    if (links.isEmpty()) {
        EmptyMediaState(text = "Нет ссылок", colors = colors)
        return
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        links.forEach { link ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: Открыть ссылку */ }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = link.name.takeIf { it.isNotEmpty() } ?: "Ссылка",
                        fontSize = 14.sp,
                        color = colors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = link.date,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Divider(
                color = colors.onSurface.copy(alpha = 0.08f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun EmptyMediaState(text: String, colors: ColorScheme) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = colors.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}