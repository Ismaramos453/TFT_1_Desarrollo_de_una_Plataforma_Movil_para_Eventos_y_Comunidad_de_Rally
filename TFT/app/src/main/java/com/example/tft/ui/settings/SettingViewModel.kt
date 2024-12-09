package com.example.tft.ui.settings
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.tft.data.Worker.NotificationUtils

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs: SharedPreferences = getApplication<Application>().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    var areNotificationsEnabled by mutableStateOf(prefs.getBoolean("notifications_enabled", false))
        private set

    fun toggleNotificationsEnabled() {
        val context = getApplication<Application>().applicationContext
        areNotificationsEnabled = !areNotificationsEnabled
        saveNotificationPreference(areNotificationsEnabled)
        if (areNotificationsEnabled) {
            NotificationUtils.scheduleEventNotifications(context)
        } else {
            NotificationUtils.cancelEventNotifications(context)
        }
    }

    private fun saveNotificationPreference(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }
}
