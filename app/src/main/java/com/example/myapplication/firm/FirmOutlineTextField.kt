package com.example.myapplication.firm

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.txtMainWhite
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.draw.clip

@Composable
fun FirmOutlineTextField(
    /**
     * Название вводимого поля
     * */
    label: String,
    /**
     * Значение вводимого поля (callback)
     * */
    value: (String) -> Unit,
    /**
     * Текущее значение (для двухсторонней привязки)
     * */
    textValue: String = "",
    /**
     * Верхний отступ
     * */
    paddingTop: Dp = 0.dp,
    /**
     * Нижний отступ
     * */
    paddingBottom: Dp = 0.dp,
    /**
     * Пароль или нет
     * */
    password: Boolean = false,
    /**
     * Ошибка или нет
     * */
    error: Boolean = false,
    /**
     * Режим поиска (показывает иконку поиска и кнопку очистки)
     * */
    search: Boolean = false,
    /**
     * Подсказка (placeholder)
     * */
    placeholder: String = "",
    /**
     * Блокировка поля
     * */
    enabled: Boolean = true,
    /**
     * Тип клавиатуры (по умолчанию Text - поддерживает русский)
     * */
    keyboardType: KeyboardType = KeyboardType.Text,
    /**
     * Действие при нажатии Enter
     * */
    imeAction: ImeAction = ImeAction.Done,
    /**
     * Количество строк (для многострочного ввода)
     * */
    maxLines: Int = 1,
    /**
     * Обработчик действия (например, поиск по Enter)
     * */
    onImeAction: (() -> Unit)? = null,
    /**
     * Кастомная иконка слева
     * */
    leadingIcon: @Composable (() -> Unit)? = null,
    /**
     * Кастомная иконка справа
     * */
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier
) {
    // Внутреннее состояние для отображения/скрытия пароля
    var showPassword by remember { mutableStateOf(false) }

    val c_bg = MaterialTheme.colorScheme.background
    val c_bgtxt = MaterialTheme.colorScheme.onBackground
    val c_surf = MaterialTheme.colorScheme.surface
    val c_surftxt = MaterialTheme.colorScheme.onSurface
    val c_acc = MaterialTheme.colorScheme.primary
    val c_accmin = MaterialTheme.colorScheme.secondary

    // Определяем цвета для рамки
    val borderColor = if (error) {
        Color.Red
    } else {
        Color.Transparent
    }

    // Определяем visualTransformation для пароля
    val visualTransformation = if (password && !showPassword) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    // Определяем иконку для пароля
    val passwordTrailingIcon = if (password) {
        {
            IconButton(
                onClick = { showPassword = !showPassword }
            ) {
                Icon(
                    imageVector = if (showPassword)
                        Icons.Default.VisibilityOff
                    else
                        Icons.Default.Visibility,
                    contentDescription = if (showPassword) "Скрыть пароль" else "Показать пароль",
                    tint = c_bgtxt.copy(alpha = 0.7f)
                )
            }
        }
    } else if (search && textValue.isNotEmpty()) {
        // Иконка очистки для поиска
        {
            IconButton(
                onClick = {
                    value("")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Очистить",
                    tint = c_bgtxt.copy(alpha = 0.5f)
                )
            }
        }
    } else if (search) {
        // Иконка поиска
        {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск",
                tint = c_bgtxt.copy(alpha = 0.5f)
            )
        }
    } else {
        trailingIcon
    }

    // Определяем иконку слева для поиска
    val leadingIconContent = if (search) {
        {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск",
                tint = c_bgtxt.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    } else {
        leadingIcon
    }

    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(
            // Цвета для обычного состояния
            focusedTextColor = c_bgtxt,
            unfocusedTextColor = c_bgtxt,
            focusedContainerColor = c_bg,
            unfocusedContainerColor = c_bg,
            // Цвета для рамки
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            // Цвета для лейбла
            focusedLabelColor = if (error) Color.Red else c_acc,
            unfocusedLabelColor = c_bgtxt.copy(alpha = 0.7f),
            // Цвета для ошибки
            errorTextColor = Color.Red,
            errorBorderColor = Color.Red,
            errorLabelColor = Color.Red,
            // Цвет курсора
            cursorColor = c_acc,
            // Цвет для disabled состояния
            disabledTextColor = c_bgtxt.copy(alpha = 0.5f),
            disabledContainerColor = c_surf.copy(alpha = 0.5f),
            disabledLabelColor = c_bgtxt.copy(alpha = 0.5f),
            disabledBorderColor = Color.Transparent
        ),
        modifier = Modifier

//            .clip(shape = RoundedCornerShape(16.dp))
            .padding(top = paddingTop, bottom = paddingBottom),
        label = {
            Text(
                color = if (error) Color.Red else c_bgtxt,
                text = label
            )
        },
        value = textValue,
        onValueChange = {
            value(it)
        },
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    color = c_bgtxt.copy(alpha = 0.5f)
                )
            }
        },
        visualTransformation = visualTransformation,
        isError = error,
        enabled = enabled,
        singleLine = maxLines == 1,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
            autoCorrect = true // Включаем автокоррекцию для русского языка
        ),
        leadingIcon = leadingIconContent,
        trailingIcon = passwordTrailingIcon,
        shape = RoundedCornerShape(16.dp)// Используем стандартную форму
    )
}

// Функция-помощник для создания текстового поля без лишних параметров
@Composable
fun FirmSimpleTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    error: Boolean = false
) {
    FirmOutlineTextField(
        label = label,
        value = onValueChange,
        textValue = value,
        placeholder = placeholder,
        enabled = enabled,
        keyboardType = keyboardType,
        imeAction = imeAction,
        error = error,
        modifier = modifier
    )
}

// Функция-помощник для поля пароля
@Composable
fun FirmPasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Введите пароль",
    enabled: Boolean = true,
    error: Boolean = false
) {
    FirmOutlineTextField(
        label = label,
        value = onValueChange,
        textValue = value,
        placeholder = placeholder,
        enabled = enabled,
        password = true,
        error = error,
        modifier = modifier
    )
}

// Функция-помощник для поля поиска
@Composable
fun FirmSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Поиск...",
    enabled: Boolean = true,
    onSearch: (() -> Unit)? = null
) {
    FirmOutlineTextField(
        label = "Поиск",
        value = onValueChange,
        textValue = value,
        placeholder = placeholder,
        enabled = enabled,
        search = true,
        modifier = modifier,
        imeAction = ImeAction.Search,
        onImeAction = onSearch
    )
}