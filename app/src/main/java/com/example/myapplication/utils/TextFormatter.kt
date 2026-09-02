// utils/TextFormatter.kt
package com.example.myapplication.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

object TextFormatter {

    private val urlRegex = Regex("""(https?://[^\s]+)""")

    @Composable
    fun formatTextWithLinks(text: String): AnnotatedString {
        return buildAnnotatedString {
            var lastIndex = 0
            val matches = urlRegex.findAll(text)

            for (match in matches) {
                // Добавляем обычный текст до ссылки
                if (match.range.first > lastIndex) {
                    append(text.substring(lastIndex, match.range.first))
                }

                // Добавляем ссылку с аннотацией
                val url = match.value
                val start = length
                append(url)
                val end = length

                addStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium
                    ),
                    start = start,
                    end = end
                )

                // Добавляем аннотацию для клика
                addStringAnnotation(
                    tag = "URL",
                    annotation = url,
                    start = start,
                    end = end
                )

                lastIndex = match.range.last + 1
            }

            // Добавляем оставшийся текст
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }
    }
}