package com.example.myapplication.screen.Login.signUp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.RegisterRequest
import com.example.myapplication.ui.components.LoadingIndicator

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    var surname by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRegistered by viewModel.isRegistered.collectAsState()

    LaunchedEffect(isRegistered) {
        if (isRegistered) {
            navController.navigate("app") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Регистрация",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    // Фамилия
                    OutlinedTextField(
                        value = surname,
                        onValueChange = { surname = it },
                        label = { Text("Фамилия *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Имя
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Имя *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Отчество
                    OutlinedTextField(
                        value = patronymic,
                        onValueChange = { patronymic = it },
                        label = { Text("Отчество") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Логин
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Логин *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Телефон
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Телефон *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Дата рождения
                    OutlinedTextField(
                        value = birthday,
                        onValueChange = { birthday = it },
                        label = { Text("Дата рождения * (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Пароль
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (showPassword) {
                            PasswordVisualTransformation()
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(onClick = { showPassword = !showPassword }) {
                                Text(if (showPassword) "Скрыть" else "Показать")
                            }
                        }
                    )

                    // Подтверждение пароля
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Подтвердите пароль *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    // Ошибка
                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Кнопка регистрации
                    Button(
                        onClick = {
                            if (password == confirmPassword) {
                                val request = RegisterRequest(
                                    username = username,
                                    surname = surname,
                                    name = name,
                                    patronymic = patronymic,
                                    email = email,
                                    telNum = phone,
                                    birthday = birthday,
                                    password = password
                                )
                                viewModel.register(request)
                            } else {
                                viewModel.setError("Пароли не совпадают")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = surname.isNotBlank() &&
                                name.isNotBlank() &&
                                username.isNotBlank() &&
                                email.isNotBlank() &&
                                phone.isNotBlank() &&
                                birthday.isNotBlank() &&
                                password.isNotBlank() &&
                                confirmPassword.isNotBlank()
                    ) {
                        Text("Зарегистрироваться", style = MaterialTheme.typography.titleMedium)
                    }

                    // Вход
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Уже есть аккаунт?")
                        TextButton(onClick = { navController.navigate("login") }) {
                            Text("Войти")
                        }
                    }
                }
            }
        }
    }
}