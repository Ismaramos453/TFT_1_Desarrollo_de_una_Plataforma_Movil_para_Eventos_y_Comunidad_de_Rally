package com.example.tft.ui.photos


import android.app.Application
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

    init {
        loadGalleryItems()
    }

    private fun loadGalleryItems() {
        GalleryServices.getGalleryItems { items ->
            _galleryItems.value = items
        }
    }
}