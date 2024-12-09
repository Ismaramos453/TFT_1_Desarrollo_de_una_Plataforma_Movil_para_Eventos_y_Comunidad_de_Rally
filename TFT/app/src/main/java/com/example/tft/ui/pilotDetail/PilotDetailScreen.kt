package com.example.tft.ui.pilotDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.tft.model.detail_stage.Race
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.pilots.getPilotImageUrl
import com.example.tft.ui.theme.ColorTextDark
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilotDetailScreen(
    pilotId: Int,
    navController: NavHostController,
    viewModel: PilotDetailViewModel = viewModel()
) {
    val pilotDetail by viewModel.pilotDetail.observeAsState()
    val seasons by viewModel.seasons.observeAsState(emptyList())
    val pilotRaces by viewModel.pilotRaces.observeAsState(emptyList())

    var selectedSeasonId by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val favoritePilots by viewModel.favoritePilots.collectAsState()
    val isFavorite = favoritePilots.contains(pilotId)

    LaunchedEffect(pilotId) {
        viewModel.loadPilotDetail(pilotId)
        viewModel.loadSeasons()
    }

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadFavoritePilots(userId)
        }
    }

    LaunchedEffect(selectedSeasonId) {
        if (selectedSeasonId != 0) {
            viewModel.loadPilotRaces(pilotId, selectedSeasonId)
        }
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Detalles del Piloto", navController = navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            pilotDetail?.let { pilot ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Imagen del piloto
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(getPilotImageUrl(pilot.id))
                                    .crossfade(true)
                                    .transformations(CircleCropTransformation())
                                    .build(),
                                contentDescription = "Pilot Image",
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Botón de favoritos
                            Button(
                                onClick = {
                                    if (userId != null) {
                                        if (isFavorite) {
                                            viewModel.removePilotFromFavorites(userId, pilotId)
                                            dialogMessage = "Piloto eliminado de tus favoritos"
                                            showDialog = true
                                        } else {
                                            viewModel.addPilotToFavorites(userId, pilotId)
                                            dialogMessage = "Piloto añadido a tus favoritos"
                                            showDialog = true
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(if (isFavorite) "Eliminar de favoritos" else "Añadir a favoritos")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Detalles del piloto
                            Text(
                                text = pilot.fullName,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Country: ${pilot.country.name}",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Birthplace: ${pilot.playerTeamInfo.birthplace}",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Selector de temporada
                            Box {
                                OutlinedButton(onClick = { expanded = true }) {
                                    Text(
                                        text = seasons.find { it.id == selectedSeasonId }?.name
                                            ?: "Select Season",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    seasons.forEach { season ->
                                        DropdownMenuItem(
                                            text = { Text(season.name) },
                                            onClick = {
                                                selectedSeasonId = season.id
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Results",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start),
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(pilotRaces) { race ->
                        RaceCard(race = race)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Confirmación",
                    color = MaterialTheme.colorScheme.tertiary
                )
            },
            text = {
                Text(
                    text = dialogMessage,
                    color = MaterialTheme.colorScheme.tertiary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Aceptar")
                }
            },
            containerColor = MaterialTheme.colorScheme.primary // Fondo dinámico
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RaceCard(race: Race) {
    // Fondo dinámico para la tarjeta
    val cardBackgroundColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val textColor = if (isSystemInDarkTheme()) {
       ColorTextDark
    } else {
        MaterialTheme.colorScheme.primary
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título de la etapa
            Text(
                text = race.stage.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción de la etapa
            Text(
                text = race.stage.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Información adicional con íconos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextWithIcon(
                    icon = Icons.Default.DirectionsCar,
                    text = "Posición: ${race.position}",
                    iconTint = MaterialTheme.colorScheme.primary
                )
                race.points?.let {
                    TextWithIcon(
                        icon = Icons.Default.EmojiEvents,
                        text = "Puntos: $it",
                        iconTint = MaterialTheme.colorScheme.onSurface
                    )
                }
                TextWithIcon(
                    icon = Icons.Default.CalendarToday,
                    text = formatDate(race.updatedAtTimestamp),
                    iconTint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun TextWithIcon(icon: ImageVector, text: String, iconTint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(timestamp: Long): String {
    return Instant.ofEpochSecond(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}
