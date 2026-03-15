package com.example.myapplication.firm

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.txtMainWhite

@Composable
fun FirmOutlineTextField(
    /**
     * Название вводимого поля
     * */
    label: String,
    /**
     * Значение вводимого поля
     * */
    value: (String) -> Unit,
    /**
     * Верхний отступ (равен 0)
     * */
    paddingTop: Dp = 0.dp,
    /**
     * Нижний отступ (равен 0)
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
    search: Boolean,
) {

    var text by remember { mutableStateOf("") }
    val c_bg = MaterialTheme.colorScheme.background     //- это основной фон
    val c_bgtxt = MaterialTheme.colorScheme.onBackground     //- это самый яркий текст, белый/чёрный
    val c_surf = MaterialTheme.colorScheme.surface     //- это дополнительный фон (белый/серо-синий посветлее). На нём уже все элементы
    val c_surftxt = MaterialTheme.colorScheme.onSurface     //- это серый текст
    val c_acc = MaterialTheme.colorScheme.primary     //- это акцентный цвет
    val c_accmin = MaterialTheme.colorScheme.secondary     //- это акцент с прозрачностью 0.5
    val bgGreyBlack = MaterialTheme.colorScheme.primary

    if (password) {
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                errorTextColor = Color.Red,
                focusedContainerColor = c_surf,
                unfocusedContainerColor = c_surf,
                focusedTextColor = c_bgtxt,
                unfocusedTextColor = c_bgtxt,
                focusedBorderColor = Color.Transparent, // Убираем рамку, если нужен стиль "капсулы"
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(top = paddingTop, bottom = paddingBottom),
            label = {
                Text(
                    color = txtMainWhite,
                    text = label
                )
            },
            value = text,
            onValueChange = {
                text = it
                value(it)
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = error
        )
    } else {
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = txtMainWhite,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                unfocusedTextColor = c_bgtxt
            ),
            modifier = Modifier
                .padding(top = paddingTop, bottom = paddingBottom),
            label = {
                Text(
                    color = txtMainWhite,
                    text = label
                )
            },
            value = text,
            onValueChange = {
                text = it
                value(it)
            },
        )
    }


}