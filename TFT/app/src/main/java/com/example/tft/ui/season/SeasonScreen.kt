package com.example.tft.ui.season

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tft.R
import com.example.tft.model.detail_stage.Season
import com.example.tft.navigation.AppScreens
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.SeasonDetail.SeasonDetailViewModel
import com.example.tft.ui.theme.PrimaryColor
import com.example.tft.ui.theme.SecondaryColor
import com.example.tft.ui.theme.TertiaryColor
import com.example.tft.ui.theme.TertiaryColorDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonScreen(navController: NavHostController, viewModel: SeasonViewModel = viewModel()) {
    val seasons by viewModel.seasons.observeAsState()
    val isDarkTheme = isSystemInDarkTheme()
    val cardBackgroundColor = if (isDarkTheme) TertiaryColorDark else TertiaryColor
    val textColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            BackTopBar(title = "Temporadas", navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Temporadas del WRC",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Explora las temporadas pasadas y presentes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                seasons?.let {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado más grande entre los elementos
                    ) {
                        items(it) { season ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = cardBackgroundColor, // Color de fondo de la carta
                                    contentColor = textColor // Color del contenido (texto)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                border = BorderStroke(1.dp, PrimaryColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo_wrc),
                                        contentDescription = "Logo WRC",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(bottom = 16.dp),
                                        colorFilter = ColorFilter.tint(textColor)
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    ) {
                                        Text(
                                            text = season.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        Text(
                                            text = "Año: ${season.year}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = textColor
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Button(
                                            onClick = { navController.navigate("${AppScreens.GlobalClassificationScreen.route}/${season.id}") },
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor) // Color de fondo del botón
                                        ) {
                                            Text(
                                                text = "Clasificación Global",
                                                color = textColor // Color del texto del botón
                                            )
                                        }
                                        Button(
                                            onClick = { navController.navigate("${AppScreens.SeasonDetailScreen.route}/${season.id}") },
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor) // Color de fondo del botón
                                        ) {
                                            Text(
                                                text = "Rallys",
                                                color = textColor // Color del texto del botón
                                            )
                                        }
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
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Cargando temporadas...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
