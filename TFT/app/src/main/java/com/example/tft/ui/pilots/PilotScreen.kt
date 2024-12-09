package com.example.tft.ui.pilots

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import coil.compose.AsyncImage
import com.example.tft.model.pilot.Team
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.tft.data.api.rememberImageLoader
import com.example.tft.navigation.AppScreens
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.theme.TFTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilotScreen(viewModel: PilotViewModel = viewModel(), navController: NavHostController) {
    val rallyPilots by viewModel.rallyPilots.observeAsState()
    val countries by viewModel.countries.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCountry by remember { mutableStateOf("Todos") }
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (viewModel.rallyPilots.value.isNullOrEmpty()) {
            viewModel.loadRallyPilots()
        }
    }

    TFTTheme {
        Scaffold(
            topBar = {
                BackTopBar(title = "Pilotos", navController = navController)
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                viewModel.searchPilots(it.text)
                            },
                            label = { Text("Buscar Pilotos") },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box {
                            OutlinedButton(
                                onClick = { expanded = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedCountry == "Todos") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(text = selectedCountry)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                countries.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text(country) },
                                        onClick = {
                                            selectedCountry = country
                                            viewModel.filterByCountry(country)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                items(rallyPilots ?: emptyList()) { pilot ->
                    PilotCard(pilot = pilot, navController = navController)
                }
            }
        }
    }
}

@Composable
fun PilotCard(pilot: Team, navController: NavHostController) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val imageLoader = rememberImageLoader()

    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "Imagen del piloto",
                imageLoader = imageLoader,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = pilot.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                Text(
                    text = "Equipo: ${pilot.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Text(
                    text = "País: ${pilot.country.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }
        }
    }
}

fun getPilotImageUrl(pilotId: Int): String {
    return "https://motorsportapi.p.rapidapi.com/api/motorsport/team/$pilotId/image"
}

