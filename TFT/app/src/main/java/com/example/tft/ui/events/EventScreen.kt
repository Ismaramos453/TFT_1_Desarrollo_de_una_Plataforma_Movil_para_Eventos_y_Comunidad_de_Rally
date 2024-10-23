package com.example.tft.ui.events

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tft.data.services.Authentication.AuthenticationServices
import com.example.tft.model.SavedEvent
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.theme.ColorTextDark
import com.example.tft.ui.theme.PrimaryColor

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(navController: NavHostController) {
    val context = LocalContext.current
    val eventViewModel: EventViewModel = viewModel()
    val events by eventViewModel.events.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var eventToDeleteId by remember { mutableStateOf<String?>(null) }
    val user = AuthenticationServices.getCurrentUser()
    var isEditMode by remember { mutableStateOf(false) } // Estado para controlar el modo de edición

    LaunchedEffect(Unit) {
        user?.let {
            eventViewModel.loadSavedEvents(it.uid)
        }
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Eventos Guardados", navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isEditMode = !isEditMode }, // Cambiar entre modo de edición
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.Edit,
                    contentDescription = if (isEditMode) "Salir de editar" else "Editar eventos"
                )
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (events.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventNote, contentDescription = "No hay eventos", Modifier.size(100.dp))
                        Text(
                            "No hay eventos guardados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(events) { event ->
                        EventItem(event, isEditMode, onDelete = {
                            eventToDeleteId = event.id
                            showDialog = true
                        }, navController)
                    }
                }
            }
        }
    }

    if (showDialog && eventToDeleteId != null) {
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else PrimaryColor
        val dialogTextColor = if (isSystemInDarkTheme) ColorTextDark else Color.White
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    user?.let {
                        eventViewModel.removeEvent(it.uid, eventToDeleteId!!)
                    }
                    showDialog = false
                    eventToDeleteId = null
                }) {
                    Text("Aceptar",color = dialogTextColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = dialogTextColor)
                }
            },
            title = { Text(text = "Confirmar eliminación", color = dialogTextColor) },
            containerColor = dialogBackgroundColor,
            text = { Text(text = "¿Estás seguro de que deseas eliminar este evento de tus favoritos?", color = dialogTextColor) }
        )
    }
}

@Composable
fun EventItem(event: SavedEvent, isEditMode: Boolean, onDelete: () -> Unit, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium)
            .clickable {
                navController.navigate("EventDetail_Screens/${event.id}")
            },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${event.date}, ${event.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)
                )
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)
                )
            }
            if (isEditMode) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar evento", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}