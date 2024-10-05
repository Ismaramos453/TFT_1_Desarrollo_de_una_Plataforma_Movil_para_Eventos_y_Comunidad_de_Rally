package com.example.tft.ui.settings

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavHostController, settingViewModel: SettingViewModel = viewModel()) {
    Scaffold(
        topBar = {
            BackTopBar(title = "Ajustes", navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
                ) {
                    Text("Activar ubicación")
                    val isLocationEnabled by settingViewModel::isLocationEnabled
                    Switch(
                        checked = isLocationEnabled,
                        onCheckedChange = { settingViewModel.toggleLocationEnabled() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
                ) {
                    Text("Activar notificaciones")
                    val areNotificationsEnabled by settingViewModel::areNotificationsEnabled
                    Switch(
                        checked = areNotificationsEnabled,
                        onCheckedChange = { settingViewModel.toggleNotificationsEnabled() }
                    )
                }
            }
        }
    }
}