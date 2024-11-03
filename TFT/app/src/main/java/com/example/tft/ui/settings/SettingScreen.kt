package com.example.tft.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.tft.templates_App.BackTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavHostController, settingViewModel: SettingViewModel) {
    val context = LocalContext.current

    // Observa los cambios en la preferencia de notificación
    val areNotificationsEnabled = settingViewModel.areNotificationsEnabled


    // Solicita permiso cuando sea necesario
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                settingViewModel.toggleNotificationsEnabled()
            } else {
                Toast.makeText(context, "Permiso para notificaciones denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = {
            BackTopBar(title = "Ajustes", navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Activar notificaciones de eventos")
                Switch(
                    checked = areNotificationsEnabled,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                settingViewModel.toggleNotificationsEnabled()
                            }
                        } else {
                            settingViewModel.toggleNotificationsEnabled()
                        }
                    }
                )
            }
        }
    }
}
