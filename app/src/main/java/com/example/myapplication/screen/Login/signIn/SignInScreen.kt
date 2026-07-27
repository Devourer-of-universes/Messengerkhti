package com.example.myapplication.screen.Login.signIn

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.firm.FirmPasswordTextField
import com.example.myapplication.firm.FirmSimpleTextField
import com.example.myapplication.ui.components.LoadingIndicator

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // Редирект если уже залогинены
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
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
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Заголовок
                    Text(
                        text = "Вход в систему",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Корпоративный мессенджер",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email / Логин
                    FirmSimpleTextField(
                        label = "Email или логин",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Введите email или логин"
                    )

                    // Пароль
                    FirmPasswordTextField(
                        label = "Пароль",
                        value = password,
                        onValueChange = { password = it }
                    )

                    // Ошибка
                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Кнопка входа
                    Button(
                        onClick = {
                            viewModel.login(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = email.isNotBlank() && password.isNotBlank()
                    ) {
                        Text("Войти", style = MaterialTheme.typography.titleMedium)
                    }

                    // Регистрация
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Нет аккаунта?")
                        TextButton(onClick = { navController.navigate("signup") }) {
                            Text("Зарегистрироваться")
                        }
                    }
                }
            }
        }
    }
}