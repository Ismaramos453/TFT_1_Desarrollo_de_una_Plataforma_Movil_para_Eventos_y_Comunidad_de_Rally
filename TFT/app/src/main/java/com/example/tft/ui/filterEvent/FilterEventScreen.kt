package com.example.tft.ui.filterEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.home.RallyEventCard
import androidx.lifecycle.viewmodel.compose.viewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterEventScreen(navController: NavHostController, level: String, filterEventViewModel: FilterEventViewModel = viewModel()) {
    val context = LocalContext.current
    val rallyEvents by filterEventViewModel.filteredEvents.observeAsState(emptyList())

    LaunchedEffect(level) {
        filterEventViewModel.filterEventsByLevel(level)
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Eventos $level", navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rallyEvents) { event ->
                    RallyEventCard(event, navController)
                }
            }
        }
    }
}