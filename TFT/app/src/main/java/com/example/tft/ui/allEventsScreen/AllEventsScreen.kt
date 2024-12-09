package com.example.tft.ui.allEventsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.tft.model.RallyEvent
import com.example.tft.templates_App.BackTopBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tft.R
import com.example.tft.navigation.AppScreens
import com.example.tft.ui.home.IconCategoryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllEventsScreen(navController: NavHostController, viewModel: AllEventsViewModel = viewModel()) {
    val events by viewModel.events.observeAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    // Filtra los eventos
    val filteredEvents = events.filter { event ->
        event.title.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Todos los eventos", navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // LazyColumn para permitir desplazamiento
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Barra de búsqueda superior
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar eventos...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }

                item {
                    // Botones de categorías
                    EventCategoryButtons(navController)
                }

                if (filteredEvents.isEmpty()) {
                    item {
                        Text(
                            "No hay eventos disponibles.",
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Lista de eventos
                    items(filteredEvents) { event ->
                        RallyEventCard(event, navController)
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventCategoryButtons(navController: NavHostController) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconCategoryButton("Mundial", R.drawable.mundo) {
            navController.navigate(AppScreens.FilterEventScreen.route + "/Mundial")
        }
        IconCategoryButton("Nacional", R.drawable.banderas) {
            navController.navigate(AppScreens.FilterEventScreen.route + "/Nacional")
        }
        IconCategoryButton("Provincial", R.drawable.mapa) {
            navController.navigate(AppScreens.FilterEventScreen.route + "/Provincial")
        }
        IconCategoryButton("Local", R.drawable.ubicacion) {
            navController.navigate(AppScreens.FilterEventScreen.route + "/Local")
        }
    }
}

@Composable
fun RallyEventCard(event: RallyEvent, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { navController.navigate(AppScreens.EventDetailScreen.route + "/${event.id}") }

        ,

        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Image(
                painter = rememberImagePainter(event.image),
                contentDescription = "Imagen del evento",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )
            Text(
                text = event.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}
