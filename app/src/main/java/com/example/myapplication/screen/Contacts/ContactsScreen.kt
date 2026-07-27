package com.example.myapplication.screen.Contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.myapplication.model.User
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.utils.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ContactsScreenViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchText by remember { mutableStateOf("") }

    val currentUserId = TokenManager.getUserId()

    LaunchedEffect(Unit) {
        viewModel.loadContacts(currentUserId.toString())
        viewModel.loadUsers()
    }

    val filteredUsers = if (searchText.isBlank()) {
        contacts
    } else {
        contacts.filter { user ->
            "${user.surname} ${user.name}".contains(searchText, ignoreCase = true) ||
                    user.username.contains(searchText, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Контакты") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Добавить контакт */ }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Добавить контакт")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Поиск
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Поиск контактов...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true
            )

            // Список контактов
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isLoading && contacts.isEmpty() -> LoadingIndicator()
                    error != null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Ошибка: $error")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                viewModel.loadContacts(currentUserId.toString())
                            }) {
                                Text("Повторить")
                            }
                        }
                    }
                    filteredUsers.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (searchText.isBlank()) "Нет контактов" else "Ничего не найдено",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (searchText.isBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Добавьте коллег в контакты",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(filteredUsers) { user ->
                                ContactItem(
                                    user = user,
                                    onClick = {
                                        // Открыть чат с этим пользователем
                                        navController.navigate("chat/${user.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(
    user: User,
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
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = colors.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${user.surname} ${user.name}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                if (user.postName.isNotEmpty()) {
                    Text(
                        text = user.postName,
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                if (user.departmentName.isNotEmpty()) {
                    Text(
                        text = user.departmentName,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Статус
            Surface(
                shape = CircleShape,
                color = if (user.status == "active")
                    Color.Green.copy(alpha = 0.2f)
                else
                    Color.Gray.copy(alpha = 0.2f),
                modifier = Modifier.size(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (user.status == "active") Color.Green else Color.Gray
                        )
                        .align(Alignment.Center as Alignment.Vertical)
                )
            }
        }
    }
}