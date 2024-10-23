package com.example.tft.ui.filterEvent
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.Event.EventServices
import com.example.tft.model.RallyEvent
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch

class FilterEventViewModel : ViewModel() {

    private val _filteredEvents = MutableLiveData<List<RallyEvent>>()
    val filteredEvents: LiveData<List<RallyEvent>> = _filteredEvents

    fun filterEventsByLevel(level: String) {
        EventServices.loadEvents { events ->
            _filteredEvents.value = events.filter { it.level.equals(level, ignoreCase = true) }
        }
    }
}