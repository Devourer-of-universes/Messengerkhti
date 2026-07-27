package com.example.myapplication.screen.Profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    navController: NavController,
    viewModel: OldProfileScreenViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Личная информация") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleEditing() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
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
                        Text("Ошибка: $error")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadUser() }) {
                            Text("Повторить")
                        }
                    }
                }
                user != null -> {
                    if (isEditing) {
                        // Режим редактирования
                        EditProfileContent(
                            user = user!!,
                            onSave = { updatedUser ->
                                viewModel.updateUser(updatedUser)
                            },
                            onCancel = { viewModel.toggleEditing() }
                        )
                    } else {
                        // Режим просмотра
                        ProfileInfoContent(user = user!!)
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Пользователь не найден")
                        Button(onClick = { viewModel.loadUser() }) {
                            Text("Обновить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoContent(user: com.example.myapplication.model.User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Личная информация
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "👤 Личная информация",
                    fontWeight = FontWeight.Bold
                )
                InfoRow("Фамилия", user.surname)
                InfoRow("Имя", user.name)
                InfoRow("Отчество", user.patronymic.ifEmpty { "—" })
            }
        }

        // Контакты
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📞 Контакты",
                    fontWeight = FontWeight.Bold
                )
                InfoRow("Email", user.email)
                InfoRow("Телефон", user.telNum)
            }
        }

        // Работа
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💼 Работа",
                    fontWeight = FontWeight.Bold
                )
                InfoRow("Должность", user.postName.ifEmpty { "—" })
                InfoRow("Отдел", user.departmentName.ifEmpty { "—" })
                InfoRow("Логин", user.username)
                InfoRow("Роль", user.roleName)
            }
        }

        // Системная информация
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚙️ Системная информация",
                    fontWeight = FontWeight.Bold
                )
                InfoRow("Статус", if (user.status == "active") "🟢 Активен" else "🔴 Неактивен")
            }
        }
    }
}

@Composable
fun EditProfileContent(
    user: com.example.myapplication.model.User,
    onSave: (com.example.myapplication.model.User) -> Unit,
    onCancel: () -> Unit
) {
    var surname by remember { mutableStateOf(user.surname) }
    var name by remember { mutableStateOf(user.name) }
    var patronymic by remember { mutableStateOf(user.patronymic) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.telNum) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "✏️ Редактирование",
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Фамилия") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = patronymic,
                    onValueChange = { patronymic = it },
                    label = { Text("Отчество") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Отмена")
                    }
                    Button(
                        onClick = {
                            val updatedUser = user.copy(
                                surname = surname,
                                name = name,
                                patronymic = patronymic,
                                email = email,
                                telNum = phone
                            )
                            onSave(updatedUser)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сохранить")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}