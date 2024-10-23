package com.example.tft.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.Event.EventServices
import com.example.tft.data.services.News.NewsServices
import com.example.tft.model.News
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch


import com.example.tft.model.RallyEvent

import java.io.IOException
class HomeViewModel : ViewModel() {

    private val _events = MutableLiveData<List<RallyEvent>>()
    val events: LiveData<List<RallyEvent>> = _events

    private val _filteredEvents = MutableLiveData<List<RallyEvent>>()
    val filteredEvents: LiveData<List<RallyEvent>> = _filteredEvents

    private val _categories = listOf("All", "WRC Event", "Awards Ceremony", "Fan Meet-up", "Shutdown Event", "Gala Dinner")
    val categories: List<String> get() = _categories

    // Añadido para noticias
    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>> = _news


    init {
        loadRallyEvents()
        loadNews()  // Cargar noticias
    }

    private fun loadNews() {
        NewsServices.getNews { newsList ->
            _news.value = newsList
        }
    }

    fun loadRallyEvents() {
        EventServices.loadEvents { events ->
            _events.value = events
            filterEvents("All", "All", "")
        }
    }

    fun filterEvents(category: String, level: String, query: String) {
        _filteredEvents.value = _events.value?.filter { event ->
            (category == "All" || event.type.contains(category, ignoreCase = true)) &&
                    (level == "All" || event.level.contains(level, ignoreCase = true)) &&
                    (event.title.contains(query, ignoreCase = true) ||
                            event.type.contains(query, ignoreCase = true) ||
                            event.location.contains(query, ignoreCase = true))
        }
    }

    fun getEventById(eventId: String): RallyEvent? {
        return _events.value?.find { it.id == eventId }
    }
}
