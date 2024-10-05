package com.example.tft.ui.newsDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.FirestoreService
import com.example.tft.model.News

class NewsDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _newsDetail = MutableLiveData<News>()
    val newsDetail: LiveData<News> = _newsDetail

    fun loadNewsDetail(newsId: String) {
        FirestoreService.getNews { newsList ->
            _newsDetail.value = newsList.find { it.id == newsId }
        }
    }
}

