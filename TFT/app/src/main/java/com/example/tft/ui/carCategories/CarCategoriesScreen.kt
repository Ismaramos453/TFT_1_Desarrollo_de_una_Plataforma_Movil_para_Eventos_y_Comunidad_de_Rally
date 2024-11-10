package com.example.tft.ui.carCategories

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.example.tft.R
import com.example.tft.model.RallyEvent
import com.example.tft.navigation.AppScreens
import com.example.tft.ui.theme.TFTTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tft.model.CarCategory
import com.example.tft.templates_App.BackTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarCategoriesScreen(navController: NavHostController) {
    val viewModel: CarCategoriesViewModel = viewModel()
    val categories by viewModel.categories.observeAsState(listOf())

    TFTTheme {
        Scaffold(
            topBar = {
                BackTopBar(title = "Categorías de coches", navController = navController)
            }
        ) { innerPadding ->
            if (categories.isEmpty()) {
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
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            navController = navController,
                            category = category,
                            imageResourceId = category.summary.image,  // Usa la URL de la imagen desde el campo `summary`
                            title = category.summary.title,
                            specs = listOf(
                                "Average Power: ${category.summary.averagePower}",
                                "Average Weight: ${category.summary.averageWeight}"
                            ),
                            iconResourceId = R.drawable.reunion
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun CategoryCard(
    navController: NavHostController,
    category: CarCategory,
    imageResourceId: String,  // Cambia el tipo a String para la URL
    title: String,
    specs: List<String>,
    iconResourceId: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {  navController.navigate(AppScreens.CarScreen.route.replace("{categoryId}", category.id))  },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Column {
            AsyncImage(
                model = imageResourceId,  // Directamente la URL
                contentDescription = "$title Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconResourceId),
                    contentDescription = "Go to $title Screen",
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
                    specs.forEach { spec ->
                        Text(
                            text = spec,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}