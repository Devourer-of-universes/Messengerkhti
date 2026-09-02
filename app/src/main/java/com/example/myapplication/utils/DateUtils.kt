// utils/DateUtils.kt
package com.example.myapplication.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val monthNames = arrayOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )

    /**
     * Парсит ISO дату с Z (UTC)
     */
    private fun parseUTCDate(dateString: String): Date? {
        return try {
            // Формат: 2026-08-04T07:22:33.961Z
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(dateString)
        } catch (e: Exception) {
            try {
                // Без миллисекунд: 2026-08-04T07:22:33Z
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                formatter.parse(dateString)
            } catch (e2: Exception) {
                null
            }
        }
    }

    /**
     * Форматирует дату для заголовка сообщения (Сегодня, Вчера или день недели)
     */
    fun formatDateHeader(dateString: String): String {
        return try {
            val date = parseUTCDate(dateString) ?: return dateString

            val today = Calendar.getInstance()
            val messageDate = Calendar.getInstance().apply {
                time = date
                // Преобразуем UTC в локальное время
                timeZone = TimeZone.getDefault()
            }

            val todayDay = today.get(Calendar.DAY_OF_YEAR)
            val todayYear = today.get(Calendar.YEAR)
            val msgDay = messageDate.get(Calendar.DAY_OF_YEAR)
            val msgYear = messageDate.get(Calendar.YEAR)

            when {
                todayYear == msgYear && todayDay == msgDay -> "Сегодня"
                todayYear == msgYear && todayDay - msgDay == 1 -> "Вчера"
                else -> {
                    val day = messageDate.get(Calendar.DAY_OF_MONTH)
                    val month = monthNames[messageDate.get(Calendar.MONTH)]
                    val year = messageDate.get(Calendar.YEAR)

                    if (year == todayYear) {
                        "$day $month"
                    } else {
                        "$day $month $year"
                    }
                }
            }
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Форматирует дату для отображения в сообщении
     */
    fun formatMessageDate(dateString: String): String {
        return try {
            val date = parseUTCDate(dateString) ?: return dateString
            val calendar = Calendar.getInstance().apply {
                time = date
                timeZone = TimeZone.getDefault()
            }

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = monthNames[calendar.get(Calendar.MONTH)]
            val year = calendar.get(Calendar.YEAR)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            if (year == currentYear) {
                "$day $month"
            } else {
                "$day $month $year"
            }
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Форматирует время HH:mm (локальное)
     * Пример: 07:22 UTC -> 10:22 MSK (UTC+3)
     */
    fun formatTime(dateString: String): String {
        return try {
            val date = parseUTCDate(dateString) ?: return dateString

            // Преобразуем в локальное время
            val calendar = Calendar.getInstance().apply {
                time = date
                timeZone = TimeZone.getDefault()
            }

            val hour = calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
            val minute = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')

            "$hour:$minute"
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Форматирует полную дату и время для просмотра изображения
     * Пример: 4 августа 2026, 10:22
     */
    fun formatFullDateTime(dateString: String): String {
        return try {
            val date = parseUTCDate(dateString) ?: return dateString

            val calendar = Calendar.getInstance().apply {
                time = date
                timeZone = TimeZone.getDefault()
            }

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = monthNames[calendar.get(Calendar.MONTH)]
            val year = calendar.get(Calendar.YEAR)
            val hour = calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
            val minute = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')

            "$day $month $year, $hour:$minute"
        } catch (e: Exception) {
            dateString
        }
    }
}