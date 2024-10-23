package com.example.tft.ui.allEventsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Event.EventServices
import com.example.tft.model.RallyEvent

class AllEventsViewModel : ViewModel() {
    private val _events = MutableLiveData<List<RallyEvent>>()
    val events: LiveData<List<RallyEvent>> get() = _events

    init {
        loadEvents()
    }

    private fun loadEvents() {
        // Aquí debes cargar los eventos desde tu fuente de datos, como una base de datos o un servicio web
        EventServices.loadEvents { eventList ->
            _events.value = eventList
        }
    }
}
