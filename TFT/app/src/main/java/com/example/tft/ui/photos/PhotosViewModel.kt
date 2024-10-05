package com.example.tft.ui.photos


import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tft.data.FirestoreService
import com.example.tft.model.GalleryItem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

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
        FirestoreService.getGalleryItems { items ->
            _galleryItems.value = items
        }
    }
}