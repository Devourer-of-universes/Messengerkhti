// utils/DownloadUtils.kt
package com.example.myapplication.utils

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

object DownloadUtils {

    fun downloadImage(context: Context, imageUrl: String, fileName: String, onComplete: () -> Unit) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            // Проверяем разрешения для Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - не требуются разрешения для загрузки в Downloads
            } else {
                // Для Android 9 и ниже проверяем разрешение
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(context, "Нет разрешения на сохранение", Toast.LENGTH_SHORT).show()
                    onComplete()
                    return
                }
            }

            val uri = Uri.parse(imageUrl)
            val request = DownloadManager.Request(uri).apply {
                setTitle(fileName)
                setDescription("Загрузка изображения")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "messenger_images/$fileName"
                )
                allowScanningByMediaScanner()
            }

            downloadManager.enqueue(request)
            Toast.makeText(context, "Загрузка начата", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            onComplete()
        }
    }

    fun downloadFile(context: Context, fileUrl: String, fileName: String) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val uri = Uri.parse(fileUrl)
            val request = DownloadManager.Request(uri).apply {
                setTitle(fileName)
                setDescription("Загрузка файла")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "messenger_files/$fileName"
                )
                allowScanningByMediaScanner()
            }

            downloadManager.enqueue(request)
            Toast.makeText(context, "Загрузка начата", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}