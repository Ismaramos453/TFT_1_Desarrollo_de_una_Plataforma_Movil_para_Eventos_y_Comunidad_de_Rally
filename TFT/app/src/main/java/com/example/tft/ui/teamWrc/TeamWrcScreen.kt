package com.example.tft.ui.teamWrc

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.tft.model.wrc.TeamWrc
import com.example.tft.templates_App.BackTopBar
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamWrcScreen(navController: NavHostController, viewModel: TeamWrcViewModel = viewModel()) {
    val teams by viewModel.teams.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            BackTopBar(title = "Equipos WRC", navController = navController)
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(teams) { team ->
                TeamCard(team)
            }
        }
    }
}

@Composable
fun TeamCard(team: TeamWrc) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box {
                if (team.image.isNotBlank()) {
                    Image(
                        painter = rememberImagePainter(
                            data = team.image,
                            builder = {
                                crossfade(true)
                            }
                        ),
                        contentDescription = "Team Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image Available", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }

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
                    text = team.teamName.ifEmpty { "No team name available" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text("Car: ${team.car}", style = MaterialTheme.typography.bodyMedium)
                Text("Country: ${team.country}", style = MaterialTheme.typography.bodyMedium)
                Text("Pilots:")
                val halfSize = (team.pilots.size + 1) / 2
                val firstColumn = team.pilots.take(halfSize)
                val secondColumn = team.pilots.drop(halfSize)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        firstColumn.forEach { pilot ->
                            PilotItem(pilot)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        secondColumn.forEach { pilot ->
                            PilotItem(pilot)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PilotItem(pilot: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Pilot",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.width(8.dp))
        Text(pilot, style = MaterialTheme.typography.bodySmall)
    }
}

