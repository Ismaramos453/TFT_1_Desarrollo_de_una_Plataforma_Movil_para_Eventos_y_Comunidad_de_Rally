package com.example.tft.ui.eventDetails
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
import java.io.IOException


class EventDetailViewModel : ViewModel() {

    private val _event = MutableLiveData<RallyEvent?>()
    val event: LiveData<RallyEvent?> = _event

    fun loadEventById(eventId: String) {
        EventServices.getEventById(eventId) { event ->
            _event.value = event
        }
    }
}
