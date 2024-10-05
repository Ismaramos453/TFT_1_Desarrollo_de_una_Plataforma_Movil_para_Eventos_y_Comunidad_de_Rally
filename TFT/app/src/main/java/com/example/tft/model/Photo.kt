package com.example.tft.model

data class ImageItem(
    val description: String = "",
    val image: String = "",
    val route: String = ""
)

data class GalleryItem(
    val id: String = "",
    val category: String = "",
    val year: Int = 0,
    val images: List<ImageItem> = emptyList()
)