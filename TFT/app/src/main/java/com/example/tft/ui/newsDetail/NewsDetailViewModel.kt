package com.example.tft.ui.newsDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tft.data.services.News.NewsServices
import com.example.tft.model.News

class NewsDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _newsDetail = MutableLiveData<News>()
    val newsDetail: LiveData<News> = _newsDetail

    fun loadNewsDetail(newsId: String) {
        NewsServices.getNews { newsList ->
            _newsDetail.value = newsList.find { it.id == newsId }
        }
    }
}

