package com.example.myapplication.screen.Chat.Message

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myapplication.DataMessanger
import com.example.myapplication.R
import com.example.myapplication.model.Message
import com.example.myapplication.ui.theme.txtMainWhite
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    navController: NavController,
    channelId: String,
) {
    val viewModel: MessageViewModel = hiltViewModel()
    val messages = viewModel.messages.collectAsState()
    val isMuted = remember { mutableStateOf(false) }
    val showChatInfo = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.listenForMessages(channelId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ContentMessage(
            modifier = Modifier.fillMaxSize(),
            messages,
            viewModel,
            channelId,
            navController as NavHostController,
            isMuted = isMuted,
            showChatInfo = showChatInfo
        )
    }

    // Центральное диалоговое окно с информацией о чате
    if (showChatInfo.value) {
        ChatInfoDialog(
            channelId = channelId,
            chatName = DataMessanger.chatName,
            isMuted = isMuted.value,
            onDismiss = { showChatInfo.value = false },
            onMuteToggle = { isMuted.value = !isMuted.value }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoDialog(
    channelId: String,
    chatName: String,
    isMuted: Boolean,
    onDismiss: () -> Unit,
    onMuteToggle: () -> Unit
) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary

    // Состояние для выбранной вкладки
    var selectedTab by remember { mutableStateOf(ChatInfoTab.SEARCH) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .shadow(24.dp, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = c_surf,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Заголовок с аватаром и именем
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(c_acc)
                        .padding(top = 24.dp, bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(White.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chatName.take(1).uppercase(),
                                fontSize = 28.sp,
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = chatName,
                            fontSize = 18.sp,
                            color = White,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Кнопка закрытия
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Закрыть",
                            tint = White
                        )
                    }
                }

                // Строка с иконками-категориями
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ChatInfoTabItem(
                        icon = Icons.Default.Search,
                        text = "Поиск",
                        isSelected = selectedTab == ChatInfoTab.SEARCH,
                        onClick = { selectedTab = ChatInfoTab.SEARCH }
                    )
                    ChatInfoTabItem(
                        icon = Icons.Default.People,
                        text = "Участники",
                        isSelected = selectedTab == ChatInfoTab.MEMBERS,
                        onClick = { selectedTab = ChatInfoTab.MEMBERS }
                    )
                    ChatInfoTabItem(
                        icon = Icons.Default.Image,
                        text = "Медиа",
                        isSelected = selectedTab == ChatInfoTab.MEDIA,
                        onClick = { selectedTab = ChatInfoTab.MEDIA }
                    )
                    ChatInfoTabItem(
                        icon = Icons.Default.Link,
                        text = "Ссылки",
                        isSelected = selectedTab == ChatInfoTab.LINKS,
                        onClick = { selectedTab = ChatInfoTab.LINKS }
                    )
                }

                HorizontalDivider(
                    color = c_surftxt.copy(alpha = 0.2f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Контент выбранной вкладки
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        ChatInfoTab.SEARCH -> {
                            item { SearchInChatContent() }
                        }
                        ChatInfoTab.MEMBERS -> {
                            item { MembersListContent(channelId) }
                        }
                        ChatInfoTab.MEDIA -> {
                            items(3) { index ->
                                MediaItemContent(index)
                            }
                        }
                        ChatInfoTab.LINKS -> {
                            items(2) { index ->
                                LinkItemContent(index)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Кнопка отключения уведомлений
                        SettingItem(
                            icon = if (isMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                            title = if (isMuted) "Включить уведомления" else "Отключить уведомления",
                            onClick = onMuteToggle,
                            iconColor = c_acc
                        )

                        HorizontalDivider(
                            color = c_surftxt.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Кнопка очистки истории
                        SettingItem(
                            icon = Icons.Outlined.Delete,
                            title = "Очистить историю",
                            onClick = { /* TODO */ },
                            iconColor = Color.Red,
                            titleColor = Color.Red
                        )

                        // Кнопка выхода из чата
                        SettingItem(
                            icon = Icons.Outlined.ExitToApp,
                            title = "Выйти из чата",
                            onClick = { /* TODO */ },
                            iconColor = Color.Red,
                            titleColor = Color.Red
                        )
                    }
                }
            }
        }
    }
}

enum class ChatInfoTab {
    SEARCH, MEMBERS, MEDIA, LINKS
}

@Composable
fun ChatInfoTabItem(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val c_acc = MaterialTheme.colorScheme.primary
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val color = if (isSelected) c_acc else c_surftxt.copy(alpha = 0.6f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = color,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun SearchInChatContent() {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary

    var searchQuery by remember { mutableStateOf("") }

    Column {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Поиск сообщений...", color = c_surftxt) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = c_bg,
                focusedContainerColor = c_bg,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = c_acc
            ),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = c_surftxt)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Пример результатов поиска
        Text(
            text = "Результаты поиска",
            fontSize = 14.sp,
            color = c_surftxt,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // TODO: Реальные результаты поиска
        SearchResultItem(
            message = "Привет всем!",
            sender = "Иван",
            time = "10:30"
        )
        SearchResultItem(
            message = "Как дела?",
            sender = "Петр",
            time = "09:15"
        )
    }
}

@Composable
fun SearchResultItem(message: String, sender: String, time: String) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surftxt = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 8.dp)
    ) {
        Row {
            Text(
                text = sender,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = c_bgtxt
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = time,
                fontSize = 12.sp,
                color = c_surftxt.copy(alpha = 0.6f)
            )
        }
        Text(
            text = message,
            fontSize = 14.sp,
            color = c_surftxt
        )
    }
}

@Composable
fun MembersListContent(channelId: String) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary

    // TODO: Загрузить реальных участников из Firebase
    val members = listOf(
        Pair("Иван", true),  // true - текущий пользователь
        Pair("Петр", false),
        Pair("Мария", false),
        Pair("Анна", false)
    )

    Column {
        Text(
            text = "Участники: ${members.size}",
            fontSize = 14.sp,
            color = c_surftxt,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        members.forEach { (name, isCurrentUser) ->
            MemberItem(
                name = name,
                isCurrentUser = isCurrentUser,
                onItemClick = { /* TODO: Открыть профиль участника */ }
            )
        }
    }
}

@Composable
fun MemberItem(name: String, isCurrentUser: Boolean, onItemClick: () -> Unit) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(c_acc.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = c_acc
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 16.sp,
                color = c_bgtxt
            )
            if (isCurrentUser) {
                Text(
                    text = "Вы",
                    fontSize = 12.sp,
                    color = c_surftxt.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun MediaItemContent(index: Int) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surftxt = MaterialTheme.colorScheme.onSurface

    val mediaItems = listOf(
        Triple("photo_1.jpg", "10:30", "Фото"),
        Triple("photo_2.jpg", "09:15", "Фото"),
        Triple("video_1.mp4", "вчера", "Видео")
    )

    if (index < mediaItems.size) {
        val (name, time, type) = mediaItems[index]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(c_surftxt.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (type == "Видео") Icons.Default.EmojiEmotions else Icons.Default.Image,
                    contentDescription = null,
                    tint = c_surftxt
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    color = c_bgtxt
                )
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = c_surftxt.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun LinkItemContent(index: Int) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary

    val links = listOf(
        Triple("https://t.me/example", "Telegram", "10:30"),
        Triple("https://github.com/project", "GitHub", "09:15")
    )

    if (index < links.size) {
        val (url, title, time) = links[index]
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = c_acc
            )
            Text(
                text = url,
                fontSize = 12.sp,
                color = c_surftxt.copy(alpha = 0.6f)
            )
            Text(
                text = time,
                fontSize = 10.sp,
                color = c_surftxt.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    iconColor: Color,
    titleColor: Color? = null
) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val finalTitleColor = titleColor ?: c_bgtxt

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = finalTitleColor
        )
    }
}

@Composable
fun ContentMessage(
    modifier: Modifier = Modifier,
    messages: State<List<Message>>,
    viewModel: MessageViewModel,
    channelId: String,
    navController: NavHostController,
    isMuted: androidx.compose.runtime.MutableState<Boolean>,
    showChatInfo: androidx.compose.runtime.MutableState<Boolean>
) {
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.background(c_bg)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = c_bg),
        ) {
            ChatMessages(
                messages = messages.value,
                onSendMessage = { message ->
                    viewModel.sendMessage(channelID = channelId, message)
                },
                navController = navController,
                chatName = DataMessanger.chatName,
                onChatInfoClick = { showChatInfo.value = true }
            )
        }
    }
}

@Composable
fun ChatMessages(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    navController: NavHostController,
    chatName: String,
    onChatInfoClick: () -> Unit
) {
    val msg = remember { mutableStateOf("") }
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize()) {
        // Верхняя панель
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(c_acc),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                contentColor = txtMainWhite,
                containerColor = Color.Transparent,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    hoveredElevation = 0.dp,
                    focusedElevation = 0.dp
                ),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp),
                onClick = {
                    navController.popBackStack()
                    DataMessanger.chatName = ""
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                    tint = c_bgtxt
                )
            }

            Text(
                text = chatName,
                fontSize = 20.sp,
                color = c_bgtxt,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable(
                        onClick = onChatInfoClick,
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .padding(horizontal = 40.dp, vertical = 12.dp)
            )
        }

        // Список сообщений
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 80.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                ItemMessageOnChat(message = message)
            }
        }

        // Нижняя панель ввода
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(c_acc)
                    .size(40.dp),
                onClick = { /* attach file logic */ },
            ) {
                Icon(
                    tint = c_bgtxt,
                    imageVector = Icons.Filled.AttachFile,
                    contentDescription = "attachFile"
                )
            }

            TextField(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = c_surf,
                    focusedContainerColor = c_surf,
                    unfocusedTextColor = c_bgtxt,
                    focusedTextColor = c_bgtxt,
                    unfocusedPlaceholderColor = c_surftxt,
                    focusedPlaceholderColor = c_surftxt,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = c_acc,
                    focusedLabelColor = c_acc,
                    unfocusedLabelColor = c_surftxt
                ),
                value = msg.value,
                onValueChange = { msg.value = it },
                placeholder = {
                    Text(
                        text = "Введите сообщение",
                        color = c_surftxt
                    )
                },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (msg.value.isNotBlank()) {
                            onSendMessage(msg.value)
                            msg.value = ""
                            hideKeyboardController?.hide()
                        }
                    }
                )
            )

            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(c_acc)
                    .size(40.dp),
                onClick = {
                    if (msg.value.isNotBlank()) {
                        onSendMessage(msg.value)
                        msg.value = ""
                        hideKeyboardController?.hide()
                    }
                },
            ) {
                Icon(
                    tint = c_bgtxt,
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "sendMessage"
                )
            }
        }
    }
}

@Composable
fun ItemMessageOnChat(message: Message) {
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary

    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val messageColor = if (isCurrentUser) c_acc else c_surf

    val configuration = LocalConfiguration.current
    val maxWidth = configuration.screenWidthDp.dp * 0.7f

    val date = Date(message.createdAT)
    val sdf = SimpleDateFormat("HH:mm")
    val formattedDate = sdf.format(date)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

        Row(
            modifier = Modifier
                .align(alignment)
                .clip(RoundedCornerShape(16.dp))
                .background(messageColor),
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isCurrentUser) {
                Image(
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 8.dp, top = 8.dp)
                        .size(36.dp)
                        .clip(CircleShape),
                    painter = painterResource(id = R.drawable.messenger_icon_round),
                    contentDescription = "avatar"
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                if (!isCurrentUser) {
                    Text(
                        text = message.senderName,
                        color = c_acc,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.message,
                    color = if (isCurrentUser) White else c_bgtxt,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )

                Text(
                    text = formattedDate,
                    color = if (isCurrentUser) White.copy(alpha = 0.6f) else c_surftxt.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}