package com.example.myapplication.model

data class MediaResponse(
    val files: List<MediaFile>,
    val images: List<MediaFile>
)

data class MediaFile(
    val id: Int = 0,
    val name: String = "",
    val url: String = "",
    val size: String = "",
    val type: String = "",
    val created_at: String = ""
)


data class MediaItem(
    val id: String = "",
    val name: String = "",
    val url: String? = null,
    val size: String = "",
    val date: String = "",
    val type: String = "" // image, file, link
)