package com.example.myapplication.utils

object Constants {
    // Для локальной разработки - укажите IP вашего компьютера
//    const val BASE_URL = "http://192.168.8.88:3000/" // ЗАМЕНИТЕ НА ВАШ IP

    // Для эмулятора
     const val BASE_URL = "http://10.0.2.2:3000/"

    // Для реального устройства с USB
    // const val BASE_URL = "http://192.168.1.100:3000/"
    val WEB_SOCKET_URL: String
        get() = BASE_URL.replace("http://", "ws://").replace("https://", "wss://")
}