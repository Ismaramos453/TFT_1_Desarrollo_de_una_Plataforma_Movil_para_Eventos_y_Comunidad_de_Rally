package com.example.tft.ui.events

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tft.data.EventService
import com.example.tft.model.RallyEvent
import com.example.tft.model.SavedEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val _events = MutableLiveData<List<SavedEvent>>()
    val events: LiveData<List<SavedEvent>> = _events
    private val _eventTypes = MutableLiveData<Map<LocalDate, String>>()
    val eventTypes: LiveData<Map<LocalDate, String>> = _eventTypes

    // StateFlow para almacenar las fechas de los eventos
    private val _eventDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val eventDates: StateFlow<List<LocalDate>> = _eventDates.asStateFlow()





    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSavedEvents(userId: String) {
        viewModelScope.launch {
            val events = EventService.loadEvents(userId)
            _events.postValue(events)
            updateEventDates(events)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateEventDates(events: List<SavedEvent>) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val eventTypesMap = mutableMapOf<LocalDate, String>()
        val dates = events.mapNotNull { event ->
            val date = LocalDate.parse(event.date, formatter)
            eventTypesMap[date] = event.type // Asignamos el tipo de evento (WRC, ERC, etc.)
            date
        }
        _eventDates.value = dates
        _eventTypes.postValue(eventTypesMap)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun addEvent(userId: String, event: RallyEvent) {
        viewModelScope.launch {
            val success = EventService.saveEvent(getApplication(), userId, event)
            if (success) {
                loadSavedEvents(userId)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeEvent(userId: String, eventId: String) {
        viewModelScope.launch {
            EventService.deleteEvent(userId, eventId)
            loadSavedEvents(userId)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsByDate(date: LocalDate): List<SavedEvent> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return _events.value?.filter { LocalDate.parse(it.date, formatter) == date } ?: emptyList()
    }



}




