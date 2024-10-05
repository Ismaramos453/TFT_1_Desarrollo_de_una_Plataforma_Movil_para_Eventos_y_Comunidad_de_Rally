package com.example.tft.ui.car

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.tft.model.Car
import com.example.tft.templates_App.BackTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarScreen(navController: NavHostController, categoryId: String) {
    val carViewModel: CarViewModel = viewModel()
    val cars by carViewModel.cars.observeAsState(initial = emptyList())

    LaunchedEffect(key1 = categoryId) {
        carViewModel.loadCars(categoryId)
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Categoría: $categoryId", navController = navController)
        }
    ) { innerPadding ->
        if (categoryId.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cars) { car ->
                    CarItem(car)
                }
            }
        }
    }
}

@Composable
fun CarItem(car: Car) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))  // Esquinas redondeadas para la tarjeta
            .padding(8.dp),  // Espaciado alrededor de la tarjeta
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),  // Elevación para sombra
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)  // Color de fondo
    ) {
        Column {
            AsyncImage(
                model = car.image,
                contentDescription = "Imagen del coche",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop  // Ajustar imagen para cubrir el área designada
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Modelo: ${car.model}",
                    style = MaterialTheme.typography.titleMedium,  // Estilo para el título
                    color = MaterialTheme.colorScheme.onSurface  // Color del texto
                )
                Text(
                    text = "Potencia: ${car.power}",
                    style = MaterialTheme.typography.bodyMedium,  // Estilo para el cuerpo del texto
                    color = MaterialTheme.colorScheme.onSurfaceVariant  // Variante de color para texto
                )
                Text(
                    text = "Peso: ${car.weight}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Precio: ${car.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
