package com.example.tft.ui.globalClassification

import androidx.compose.foundation.Image
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
import com.example.tft.templates_App.DefaultTopBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
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
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.tft.data.api.rememberImageLoader
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.pilots.getPilotImageUrl
import com.example.tft.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalClassificationScreen(navController: NavHostController, seasonId: String, viewModel: GlobalClassificationViewModel = viewModel()) {
    val standings by viewModel.standings.observeAsState()

    LaunchedEffect(seasonId) {
        viewModel.fetchStandings(seasonId)
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Clasificación Global", navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            standings?.let { standingsList ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(standingsList) { standing ->
                        val (containerColor, contentColor, icon) = when (standing.position) {
                            1 -> Triple(Color(0xFFD5BE00), Color.Black, Icons.Default.EmojiEvents)
                            2 -> Triple(Color(0xFFC0C0C0), Color.Black, Icons.Default.Star)
                            3 -> Triple(Color(0xFFCD7F32), Color.Black, Icons.Default.StarHalf)
                            else -> Triple(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface, null)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TeamImage(teamId = standing.team.id) // Usar la función TeamImage
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = standing.team.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(text = "Puntos: ${standing.points}", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "Posición: ${standing.position}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                icon?.let {
                                    Icon(imageVector = it, contentDescription = null, tint = contentColor, modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
                    Text(text = "Cargando clasificación...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}


@Composable
fun TeamImage(teamId: Int) {
    val imageLoader = rememberImageLoader()

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(getPilotImageUrl(teamId))
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = "Imagen del piloto",
        imageLoader = imageLoader, // Utiliza el ImageLoader configurado
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
    )
}