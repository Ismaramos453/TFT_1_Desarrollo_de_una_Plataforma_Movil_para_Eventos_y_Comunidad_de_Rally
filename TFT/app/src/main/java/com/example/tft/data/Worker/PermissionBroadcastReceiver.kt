package com.example.tft.data.Worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.tft.ui.settings.SettingViewModel

class PermissionBroadcastReceiver(private val settingViewModel: SettingViewModel) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Verifica el estado del permiso de notificación
        val isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        // Si el permiso es revocado y las notificaciones estaban habilitadas
        if (!isNotificationPermissionGranted && settingViewModel.areNotificationsEnabled) {
            Log.d("PermissionReceiver", "Permiso de notificaciones revocado, desactivando notificaciones en la app")
            settingViewModel.toggleNotificationsEnabled()
        }
    }
}