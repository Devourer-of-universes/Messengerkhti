package com.example.myapplication.screen.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.ui.theme.txtMainWhite
import com.example.myapplication.firm.FirmOutlineTextField
import com.example.myapplication.screen.Profile.OldProfileScreenViewModel
import com.example.myapplication.utils.TokenManager
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.dropShadow
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonDefaults
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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
    val showSaveDialog by viewModel.showSaveDialog.collectAsState()

    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Личные данные",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = txtMainWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = txtMainWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.primary
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
                            onClick = { viewModel.loadUser() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
                user != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        // Аватар
                        item {
                            AvatarSection(
                                user = user!!,
                                isEditing = isEditing,
                                onAvatarClick = { /* TODO: Смена аватара */ }
                            )
                        }

                        // Личная информация
                        item {
                            PersonalInfoSection(
                                user = user!!,
                                isEditing = isEditing,
                                onValueChange = { field, value ->
                                    viewModel.updateField(field, value)
                                }
                            )
                        }

                        // Контакты
                        item {
                            ContactsSection(
                                user = user!!,
                                isEditing = isEditing,
                                onValueChange = { field, value ->
                                    viewModel.updateField(field, value)
                                }
                            )
                        }

                        // Рабочая информация
                        item {
                            WorkInfoSection(
                                user = user!!,
                                colors = colors
                            )
                        }

                        // Смена пароля
                        item {
                            PasswordSection(
                                onChangePassword = {
                                    viewModel.showChangePasswordDialog()
                                },
                                colors = colors
                            )
                        }

                        // Кнопки действий
                        item {
                            ActionButtons(
                                isEditing = isEditing,
                                onEditToggle = {
                                    if (isEditing) {
                                        viewModel.saveChanges()
                                    } else {
                                        viewModel.toggleEditing()
                                    }
                                },
                                onCancel = {
                                    viewModel.cancelEditing()
                                },
                                colors = colors
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    // Диалог успешного сохранения
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeSaveDialog() },
            title = {
                Text(
                    text = "✅ Изменения сохранены",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Внесённые изменения сохранены и отправлены на подтверждение.",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Как только подтверждение будет получено, изменения вступят в силу.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.closeSaveDialog() }
                ) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Диалог смены пароля
    if (viewModel.showPasswordDialog.collectAsState().value) {
        ChangePasswordDialog(
            onDismiss = { viewModel.closePasswordDialog() },
            onChangePassword = { currentPassword, newPassword ->
                viewModel.changePassword(currentPassword, newPassword)
            },
            isLoading = isLoading
        )
    }
}

@Composable
fun AvatarSection(
    user: com.example.myapplication.model.User,
    isEditing: Boolean,
    onAvatarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(
                    elevation = 8.dp,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .clickable { onAvatarClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.take(1).uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(
                onClick = onAvatarClick,
                colors = TextButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Загрузить фото", fontSize = 13.sp)
            }
            TextButton(
                onClick = { /* TODO: Удалить аватар */ },
                colors = TextButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Удалить", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun PersonalInfoSection(
    user: com.example.myapplication.model.User,
    isEditing: Boolean,
    onValueChange: (String, String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Личная информация",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isEditing) {
                FirmOutlineTextField(
                    label = "Фамилия",
                    value = { onValueChange("surname", it) },
                    textValue = user.surname,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FirmOutlineTextField(
                    label = "Имя",
                    value = { onValueChange("name", it) },
                    textValue = user.name,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FirmOutlineTextField(
                    label = "Отчество",
                    value = { onValueChange("patronymic", it) },
                    textValue = user.patronymic,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FirmOutlineTextField(
                    label = "Дата рождения",
                    value = { onValueChange("birthday", it) },
                    textValue = user.birthday ?: "",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                InfoRow("Фамилия", user.surname)
                InfoRow("Имя", user.name)
                InfoRow("Отчество", user.patronymic.ifEmpty { "—" })
                InfoRow("Дата рождения", user.birthday ?: "—")
            }
        }
    }
}

@Composable
fun ContactsSection(
    user: com.example.myapplication.model.User,
    isEditing: Boolean,
    onValueChange: (String, String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Контакты",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isEditing) {
                FirmOutlineTextField(
                    label = "Телефон",
                    value = { onValueChange("phone", it) },
                    textValue = user.telNum,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FirmOutlineTextField(
                    label = "Email",
                    value = { onValueChange("email", it) },
                    textValue = user.email,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                InfoRow("Телефон", user.telNum)
                InfoRow("Email", user.email)
            }
        }
    }
}

@Composable
fun WorkInfoSection(
    user: com.example.myapplication.model.User,
    colors: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Рабочая информация",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoRow("Должность", user.postName.ifEmpty { "—" })
            InfoRow("Отдел", user.departmentName.ifEmpty { "—" })
            InfoRow("Логин", user.username)
            InfoRow("Роль", if (user.isSuperAdmin) "Супер-администратор" else user.roleName)
            InfoRow("Статус", if (user.status == "active") "🟢 Активен" else "🔴 Неактивен")
            InfoRow("Дата регистрации", formatDate(user.createdAt))
            if (user.startDate != null) {
                InfoRow("Начало работы", user.startDate)
            }
            if (user.birthday != null) {
                InfoRow("Дата рождения", user.birthday)
            }
        }
    }
}

@Composable
fun PasswordSection(
    onChangePassword: () -> Unit,
    colors: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onChangePassword() },
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Смена пароля",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onBackground
                )
                Text(
                    text = "Изменить текущий пароль",
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionButtons(
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    onCancel: () -> Unit,
    colors: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isEditing) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.surfaceVariant
                )
            ) {
                Text("Отмена", color = colors.onSurface)
            }
            Button(
                onClick = onEditToggle,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                )
            ) {
                Text("Сохранить", color = txtMainWhite)
            }
        } else {
            Button(
                onClick = onEditToggle,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Редактировать")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = colors.onSurfaceVariant,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = colors.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onChangePassword: (String, String) -> Unit,
    isLoading: Boolean
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🔑 Смена пароля",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FirmOutlineTextField(
                    label = "Текущий пароль",
                    value = { currentPassword = it },
                    textValue = currentPassword,
                    password = true,
                    placeholder = "Введите текущий пароль",
                    modifier = Modifier
                )

                FirmOutlineTextField(
                    label = "Новый пароль",
                    value = { newPassword = it },
                    textValue = newPassword,
                    password = true,
                    placeholder = "Введите новый пароль",
                    modifier = Modifier
                )

                FirmOutlineTextField(
                    label = "Подтверждение",
                    value = { confirmPassword = it },
                    textValue = confirmPassword,
                    password = true,
                    placeholder = "Подтвердите пароль",
                    modifier = Modifier
                )

                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.length < 6) {
                        passwordError = "Пароль должен содержать минимум 6 символов"
                        return@Button
                    }
                    if (newPassword != confirmPassword) {
                        passwordError = "Пароли не совпадают"
                        return@Button
                    }
                    passwordError = null
                    onChangePassword(currentPassword, newPassword)
                },
                enabled = !isLoading &&
                        currentPassword.isNotEmpty() &&
                        newPassword.isNotEmpty() &&
                        confirmPassword.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Сменить пароль")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Отмена")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}