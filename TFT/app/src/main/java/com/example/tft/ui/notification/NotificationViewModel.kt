package com.example.tft.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.model.notification.NotificationService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableLiveData<List<Map<String, Any>>>()
    val notifications: LiveData<List<Map<String, Any>>> = _notifications

    fun loadNotifications() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        NotificationService.getNotifications(currentUserId) { fetchedNotifications ->
            _notifications.value = fetchedNotifications
        }
    }
}