package com.example.tft.ui.SeasonDetail
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tft.R
import com.example.tft.navigation.AppScreens
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.theme.SecondaryColor
import com.example.tft.ui.theme.TertiaryColor
import com.example.tft.ui.theme.TertiaryColorDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonDetailScreen(navController: NavController, seasonId: String, viewModel: SeasonDetailViewModel = viewModel()) {
    val substages by viewModel.substages.observeAsState()
    val isDarkTheme = isSystemInDarkTheme()
    val cardBackgroundColor = if (isDarkTheme) TertiaryColorDark else TertiaryColor
    val textColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(seasonId) {
        viewModel.fetchSubstages(seasonId)
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Detalles de la Temporada", navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
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
                    text = "Carreras de la Temporada",
                    style = MaterialTheme.typography.headlineMedium,
                    color = textColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                substages?.let {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(it.stages) { stage ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = cardBackgroundColor,
                                    contentColor = textColor
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    when (stage.slug) {
                                        "rally-de-monte-carlo", "rallye-monte-carlo", "rallye-monte-carlo-2013" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.montecarlo),
                                                contentDescription = "Rally de Monte-Carlo",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 16.dp)
                                            )
                                        }
                                        "rally-sweden", "rallye-sweden", "rally-sweden-2013" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.suecia),
                                                contentDescription = "Rally Sweden",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 16.dp)
                                            )
                                        }
                                        "rally-kenya" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.kenya),
                                                contentDescription = "Rally Kenya",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-croatia" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.croacia),
                                                contentDescription = "Rally Croatia",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-de-portugal", "rallye-portugal" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.portugal),
                                                contentDescription = "Rally Portugal",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-italia" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.italia),
                                                contentDescription = "Rally Italia",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-poland" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.polonia),
                                                contentDescription = "Rally Polonia",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-latvia" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.letonia),
                                                contentDescription = "Rally Letonia",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-finland" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.finlandia),
                                                contentDescription = "Rally Finland",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-greece" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.grecia),
                                                contentDescription = "Rally Greece",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-chile" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.chile),
                                                contentDescription = "Rally Chile",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-central-europe" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.europa),
                                                contentDescription = "Rally Central Europe",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-japan" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.japon),
                                                contentDescription = "Rally Japan",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-mexico", "rally-guanajuato-mexico" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.mexico),
                                                contentDescription = "Rally Mexico",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-estonia" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.estonia),
                                                contentDescription = "Rally Estonia",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-belgium" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.belgica),
                                                contentDescription = "Rally Belgium",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-new-zealand" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.nuevazelanda),
                                                contentDescription = "Rally New Zealand",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-de-espana" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.espana),
                                                contentDescription = "Rally Spain",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "arctic-rally-finland", "rally-finland-2013" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.articfinland),
                                                contentDescription = "Arctic-rally-finland",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-great-britain" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.granbritain),
                                                contentDescription = "Rally-great-britain",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-monza" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.monza),
                                                contentDescription = "Rally-monza",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-argentina" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.argentina),
                                                contentDescription = "Rally-argentina",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-turkey" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.turquia),
                                                contentDescription = "Rally-turkey",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rallye-deutschland" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.deutschland),
                                                contentDescription = "Rallye-deutschland",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rallye-de-france" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.francia),
                                                contentDescription = "Rallye-de-france",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-australia", "rally-australia-2013" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.australia),
                                                contentDescription = "Rally-australia",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "rally-china" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.china),
                                                contentDescription = "Rally-China",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "wales-rally", "wales-rally-2013" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.wales),
                                                contentDescription = "Wales-rally",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                        "acropolis-rally" -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.grecia),
                                                contentDescription = "Acropolis-rally",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        StatusIndicator(isFinished = stage.status.type == "finished")
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = stage.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor
                                            )
                                            Text(
                                                text = "Descripción: ${stage.description}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = textColor
                                            )
                                            Text(
                                                text = "Estado: ${stage.status.description}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = textColor
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = { navController.navigate("${AppScreens.StageDaysDetailScreen.route}/${stage.id}") },
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor) // Color de fondo del botón
                                        ) {
                                            Text(
                                                text = "Ver detalles",
                                                color = textColor // Color del texto del botón
                                            )
                                        }
                                        Button(
                                            onClick = { navController.navigate("${AppScreens.StageDetailScreen.route}/${stage.id}") },
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor) // Color de fondo del botón
                                        ) {
                                            Text(
                                                text = "Tiempos",
                                                color = textColor
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
                            text = "Cargando carreras...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(isFinished: Boolean) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (isFinished) Color.Red else Color.Green)
    )
}
