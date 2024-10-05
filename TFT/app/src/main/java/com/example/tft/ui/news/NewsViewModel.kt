package com.example.tft.ui.news
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.FirestoreService
import com.example.tft.model.News
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>> = _news

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            FirestoreService.getNews { newsList ->
                _news.value = newsList
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterNews()
    }

    private fun filterNews() {
        viewModelScope.launch {
            FirestoreService.getNews { newsList ->
                if (_searchQuery.value!!.isNotEmpty()) {
                    _news.value = newsList.filter { it.title.contains(_searchQuery.value!!, true) }
                } else {
                    _news.value = newsList
                }
            }
        }
    }
}
