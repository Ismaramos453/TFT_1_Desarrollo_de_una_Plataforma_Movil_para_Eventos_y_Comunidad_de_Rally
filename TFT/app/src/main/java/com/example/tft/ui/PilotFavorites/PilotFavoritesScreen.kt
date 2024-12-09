package com.example.tft.ui.PilotFavorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tft.model.pilot.Team
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.tft.navigation.AppScreens
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.pilots.getPilotImageUrl
import com.example.tft.ui.theme.ColorTextDark
import com.example.tft.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilotFavoritesScreen(userId: String, navController: NavHostController) {
    val viewModel: PilotFavoritesViewModel = viewModel()
    val favoritePilots = viewModel.favoritePilotsDetails.collectAsState().value
    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.loadFavoritePilotsDetails(userId)
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Mis pilotos favoritos", navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isEditMode = !isEditMode },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.Edit,
                    contentDescription = if (isEditMode) "Salir de editar" else "Editar mi lista de pilotos favoritos"
                )
            }
        },
        content = { padding ->
            if (favoritePilots.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Group, contentDescription = "No hay eventos", Modifier.size(100.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tu lista de pilotos está vacía",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
            } else {
                LazyColumn(contentPadding = padding) {
                    items(favoritePilots) { pilot ->
                        PilotItem(pilot, navController, isEditMode) {
                            viewModel.removePilotFromFavorites(userId, pilot.id)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun PilotItem(pilot: Team, navController: NavHostController, isEditMode: Boolean, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else PrimaryColor
        val dialogTextColor = if (isSystemInDarkTheme) ColorTextDark else Color.White
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar",color = dialogTextColor) },
            text = { Text("¿Estás seguro de que deseas eliminar a este piloto de tus favoritos?",color = dialogTextColor) },
            containerColor = dialogBackgroundColor,
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDialog = false
                    }
                ) {
                    Text("Eliminar",color = dialogTextColor)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar",color = dialogTextColor)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {  navController.navigate(AppScreens.PilotDetailScreen.route.replace("{pilotId}", pilot.id.toString()))  },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(getPilotImageUrl(pilot.id))
                    .crossfade(true)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = "Pilot Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pilot.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Equipo: ${pilot.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "País: ${pilot.country.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (isEditMode) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}







