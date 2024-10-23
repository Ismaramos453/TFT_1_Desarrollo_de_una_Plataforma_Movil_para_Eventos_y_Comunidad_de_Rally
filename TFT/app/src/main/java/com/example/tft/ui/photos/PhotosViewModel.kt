package com.example.tft.ui.photos


import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tft.data.services.Gallery.GalleryServices
import com.example.tft.model.GalleryItem


class PhotosViewModel(application: Application) : AndroidViewModel(application) {
    private val _galleryItems = MutableLiveData<List<GalleryItem>>()
    val galleryItems: LiveData<List<GalleryItem>> = _galleryItems

    val likes = mutableStateMapOf<String, Boolean>()

    fun toggleLike(photoId: String) {
        val currentLikeStatus = likes[photoId] ?: false
        likes[photoId] = !currentLikeStatus
    }

    init {
        loadGalleryItems()
    }

    private fun loadGalleryItems() {
        GalleryServices.getGalleryItems { items ->
            _galleryItems.value = items
        }
    }
}