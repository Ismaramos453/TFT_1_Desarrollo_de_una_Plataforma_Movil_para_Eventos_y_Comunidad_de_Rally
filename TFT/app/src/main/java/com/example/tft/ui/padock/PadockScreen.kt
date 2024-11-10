package com.example.tft.ui.padock
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.tft.templates_App.DefaultTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tft.R
import com.example.tft.navigation.AppScreens
import com.example.tft.ui.theme.TFTTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PadockScreen(navController: NavHostController) {
    val searchQuery = remember { mutableStateOf("") }  // Usar val aquí para mantener el estado mutable
    TFTTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Sección del mundial de Rally", color = Color.White) }, // Color del título ajustado a blanco
                    navigationIcon = {
                        // Puedes cambiar Icons.Filled.Menu por Icons.Filled.ArrowBack si es para navegación hacia atrás
                        IconButton(onClick = { /* Define la acción aquí, como navController.popBackStack() o abrir un drawer */ }) {
                            Icon(Icons.Filled.DirectionsCar, contentDescription = "WRC", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
        ) { innerPadding ->
            PadockContent(navController, innerPadding)
        }
    }
}

@Composable
fun PadockContent(navController: NavHostController, innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { SeasonCard(navController, R.drawable.wrc, "Clasification Info", "Explore details about WRC", Icons.Default.EmojiEvents) }
        item { PilotCard(navController, R.drawable.pilotos, "Pilots Info", "Explore details about pilots", Icons.Default.Person) }
        item { CarCard(navController, R.drawable.wrc_cars1, "Sección de coches", "Explora todos los coches de las grandes categorías", Icons.Default.DirectionsCar) }
        item { TeamCard(navController, R.drawable.equipos, "Teams Info", "Explore details about teams", Icons.Default.Group) }
    }
}

@Composable
fun SeasonCard(navController: NavHostController, imageId: Int, title: String, description: String, icon: ImageVector) {
    InfoCard(navController, imageId, title, description, icon, AppScreens.SeasonScreen.route)
}

@Composable
fun PilotCard(navController: NavHostController, imageId: Int, title: String, description: String, icon: ImageVector) {
    InfoCard(navController, imageId, title, description, icon, AppScreens.PilotScreen.route)
}

@Composable
fun CarCard(navController: NavHostController, imageId: Int, title: String, description: String, icon: ImageVector) {
    InfoCard(navController, imageId, title, description, icon, AppScreens.CarCategoriesScreen.route)
}

@Composable
fun TeamCard(navController: NavHostController, imageId: Int, title: String, description: String, icon: ImageVector) {
    InfoCard(navController, imageId, title, description, icon, AppScreens.TeamWrcScreen.route)
}


@Composable
fun InfoCard(
    navController: NavHostController,
    @DrawableRes imageId: Int,
    title: String,
    description: String,
    icon: ImageVector,
    route: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { navController.navigate(route) },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Icon for $title",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
