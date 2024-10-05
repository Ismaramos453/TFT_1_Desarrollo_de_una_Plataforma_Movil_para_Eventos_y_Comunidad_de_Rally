package com.example.tft.ui.route

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
import com.example.tft.templates_App.DefaultTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            BackTopBar(title = "Routes", navController = navController)
        }
    ) { innerPadding ->
        // Contenido de la pantalla Home
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)

        ) {
            // Tu contenido aquí
            Text("Bienvenido a Routes")
        }
    }
}
