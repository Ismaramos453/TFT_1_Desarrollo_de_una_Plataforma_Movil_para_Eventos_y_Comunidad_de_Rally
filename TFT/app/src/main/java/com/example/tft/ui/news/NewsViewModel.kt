package com.example.tft.ui.news
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.News.NewsServices
import com.example.tft.model.News
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>> = _news

    private val _searchQuery = MutableLiveData("")

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            NewsServices.getNews { newsList ->
                _news.value = newsList
            }
        }
    }
}
