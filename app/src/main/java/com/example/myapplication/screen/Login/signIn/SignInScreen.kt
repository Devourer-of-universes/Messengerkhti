package com.example.myapplication.screen.Login.signIn

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.ui.theme.ThemeMode
import com.example.myapplication.ui.theme.bgMainDarkTheme
import com.example.myapplication.ui.theme.bgSecDarkTheme
import com.example.myapplication.ui.theme.txtMainGrey
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun SignInScreen(navController: NavController) {
    Log.d("proverka", "signinScreen открылся")
    val viewModel: SignInViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Цвета из темы
    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary

    // Валидация email
    LaunchedEffect(email) {
        emailError = email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Обработка состояния
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            SignInState.Success -> {
                Toast.makeText(context, "Вход выполнен", Toast.LENGTH_SHORT).show()
                navController.navigate("app") {
                    popUpTo("signin") { inclusive = true }
                }
            }
            is SignInState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(c_bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "Добро пожаловать!",
                color = c_bgtxt,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Войдите в свой аккаунт",
                color = c_surftxt,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Поле Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        text = "Электронная почта",
                        color = c_surftxt
                    )
                },
                isError = emailError,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    errorTextColor = Color.Red,
                    focusedTextColor = c_bgtxt,
                    unfocusedTextColor = c_bgtxt,
                    focusedBorderColor = c_acc,
                    unfocusedBorderColor = c_surftxt.copy(alpha = 0.5f),
                    focusedContainerColor = c_surf,
                    unfocusedContainerColor = c_surf,
                    focusedLabelColor = c_acc,
                    unfocusedLabelColor = c_surftxt,
                    cursorColor = c_acc
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле Пароль
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = "Пароль",
                        color = c_surftxt
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    errorTextColor = Color.Red,
                    focusedTextColor = c_bgtxt,
                    unfocusedTextColor = c_bgtxt,
                    focusedBorderColor = c_acc,
                    unfocusedBorderColor = c_surftxt.copy(alpha = 0.5f),
                    focusedContainerColor = c_surf,
                    unfocusedContainerColor = c_surf,
                    focusedLabelColor = c_acc,
                    unfocusedLabelColor = c_surftxt,
                    cursorColor = c_acc
                )
            )

            // Забыли пароль?
            TextButton(
                onClick = { /* TODO: восстановление пароля */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Забыли пароль?",
                    color = c_acc,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка авторизации
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank() && !emailError) {
                        viewModel.signIn(email = email, password = password)
                    } else {
                        Toast.makeText(
                            context,
                            if (emailError) "Неверный формат email" else "Заполните все поля",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = c_acc,
                    disabledContainerColor = c_accmin
                ),
                enabled = uiState != SignInState.Loading && email.isNotBlank() && password.isNotBlank() && !emailError
            ) {
                if (uiState == SignInState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = c_bgtxt,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Войти",
                        fontSize = 16.sp,
                        color = c_bgtxt,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Регистрация
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Нет аккаунта? ",
//                    color = c_surftxt,
//                    fontSize = 14.sp
//                )
//                TextButton(
//                    onClick = { navController.navigate("signup") },
//                    modifier = Modifier.height(40.dp)
//                ) {
//                    Text(
//                        text = "Зарегистрироваться",
//                        color = c_acc,
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
            }
        }
    }
