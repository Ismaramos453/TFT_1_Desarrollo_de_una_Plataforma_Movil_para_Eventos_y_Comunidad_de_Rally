package com.example.tft.ui.eventDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Event.EventServices
import com.example.tft.model.RallyEvent


class EventDetailViewModel : ViewModel() {

    private val _event = MutableLiveData<RallyEvent?>()
    val event: LiveData<RallyEvent?> = _event

    fun loadEventById(eventId: String) {
        EventServices.getEventById(eventId) { event ->
            _event.value = event
        }
    }
}
