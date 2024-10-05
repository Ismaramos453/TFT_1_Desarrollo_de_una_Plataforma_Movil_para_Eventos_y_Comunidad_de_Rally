package com.example.tft.ui.videos

import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.api.youtube.YouTubeApi
import com.example.tft.model.videos.Video
import kotlinx.coroutines.launch



class VideoViewModel : ViewModel() {

    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> get() = _videos

    private val _filteredVideos = MutableLiveData<List<Video>>()
    val filteredVideos: LiveData<List<Video>> get() = _filteredVideos

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiKey = "AIzaSyB0CEAh4Q3gEtZVAQu6FSDveY_-L_s_4GU"
    private var cache = mutableMapOf<String, List<Video>>()  // Caché para almacenar listas de videos por término de búsqueda

    init {
        searchRallyVideos()
    }

    private fun searchRallyVideos() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val searchTerms = listOf(
                    "WRC", "rally drivers", "rally cars",
                    "Sébastien Loeb", "Sébastien Ogier", "Thierry Neuville",
                    "Ott Tänak", "Elfyn Evans", "Kalle Rovanperä", "Dani Sordo"
                )
                val videoResults = mutableListOf<Video>()

                for (term in searchTerms) {
                    if (cache.containsKey(term)) {
                        videoResults.addAll(cache[term]!!)
                        continue
                    }

                    val response = YouTubeApi.service.searchVideos(
                        part = "snippet",
                        query = term,
                        type = "video",
                        apiKey = apiKey
                    )

                    val videos = response.items.map { result ->
                        Video(
                            title = result.snippet.title,
                            videoId = result.id.videoId,
                            description = result.snippet.description,
                            thumbnailUrl = result.snippet.thumbnails.default.url,
                            author = result.snippet.channelTitle
                        )
                    }

                    cache[term] = videos
                    videoResults.addAll(videos)
                }

                val uniqueVideos = videoResults.distinctBy { it.videoId }
                _videos.postValue(uniqueVideos)
                _filteredVideos.postValue(uniqueVideos)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _isLoading.postValue(false)
                e.printStackTrace()
            }
        }
    }

    fun filterVideos(query: String) {
        val filteredList = if (query.isEmpty()) {
            _videos.value ?: emptyList()
        } else {
            _videos.value?.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.author.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            } ?: emptyList()
        }
        _filteredVideos.postValue(filteredList)
    }

    fun sortVideosByTitle() {
        _filteredVideos.value = _filteredVideos.value?.sortedBy { it.title }
    }

    fun sortVideosByAuthor() {
        _filteredVideos.value = _filteredVideos.value?.sortedBy { it.author }
    }
}