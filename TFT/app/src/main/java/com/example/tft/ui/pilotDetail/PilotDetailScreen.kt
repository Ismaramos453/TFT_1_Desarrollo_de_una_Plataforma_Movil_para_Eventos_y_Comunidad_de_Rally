package com.example.tft.ui.pilotDetail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.tft.model.detail_stage.Race
import com.example.tft.templates_App.BackTopBar

import com.example.tft.ui.pilots.getPilotImageUrl
import com.example.tft.ui.theme.ColorTextDark
import com.example.tft.ui.theme.PrimaryColor
import com.google.firebase.auth.FirebaseAuth

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
    val userId = currentUser?.uid  // Asegúrate de que el usuario está logueado
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(pilotId) {
        viewModel.loadPilotDetail(pilotId)
        viewModel.loadSeasons()
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
        pilotDetail?.let { pilot ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                        Button(
                            onClick = {
                                if (userId != null) {
                                    viewModel.addPilotToFavorites(userId, pilotId)
                                    showDialog = true  // Mostrar el diálogo de confirmación
                                } else {

                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Añadir a favoritos")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                        Text(
                            text = "Team Colors: ${pilot.teamColors.primary}, ${pilot.teamColors.secondary}",
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dropdown to select season
                        Box {
                            OutlinedButton(onClick = { expanded = true }) {
                                Text(text = seasons.find { it.id == selectedSeasonId }?.name ?: "Select Season",
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
                            color = MaterialTheme.colorScheme.primary
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
                CircularProgressIndicator()
            }
        }


    }
    if (showDialog) {
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else PrimaryColor
        val dialogTextColor = if (isSystemInDarkTheme) ColorTextDark else Color.White
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text("Confirmación",color = dialogTextColor)
            },
            text = {
                Text("Piloto añadido a tus favoritos",color = dialogTextColor)
            },
            containerColor = dialogBackgroundColor,
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false  // Ocultar el diálogo cuando el usuario toca "Aceptar"
                    }
                ) {
                    Text("Aceptar",color = dialogTextColor)
                }
            }
        )
    }


}

@Composable
fun RaceCard(race: Race) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = race.stage.name,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Description: ${race.stage.description}",
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Position: ${race.position}",
                color = MaterialTheme.colorScheme.onSurface
            )
            race.points?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Points: $it",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Updated At: ${race.updatedAtTimestamp}",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun getPilotImageUrl(pilotId: Int): String {
    return "https://motorsportapi.p.rapidapi.com/api/motorsport/team/$pilotId/image"
}
