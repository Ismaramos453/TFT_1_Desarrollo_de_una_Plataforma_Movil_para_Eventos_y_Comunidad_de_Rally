package com.example.tft.ui.settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingViewModel : ViewModel() {
    private var _isLocationEnabled by mutableStateOf(false)
    val isLocationEnabled: Boolean
        get() = _isLocationEnabled

    private var _areNotificationsEnabled by mutableStateOf(false)
    val areNotificationsEnabled: Boolean
        get() = _areNotificationsEnabled

    fun toggleLocationEnabled() {
        _isLocationEnabled = !_isLocationEnabled
    }

    fun toggleNotificationsEnabled() {
        _areNotificationsEnabled = !_areNotificationsEnabled
    }
}