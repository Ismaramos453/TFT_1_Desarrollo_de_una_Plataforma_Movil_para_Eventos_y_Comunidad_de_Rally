package com.example.tft.ui.photos
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.tft.templates_App.DefaultTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.tft.R
import com.example.tft.model.RallyEvent
import com.example.tft.navigation.AppScreens
import com.example.tft.ui.bottonBar.BottonBarScreen
import com.example.tft.ui.theme.BackGroundLight
import com.example.tft.ui.theme.BackGroundLightDark
import com.example.tft.ui.theme.PrimaryColorDark
import com.example.tft.ui.theme.TertiaryColor
import com.example.tft.ui.theme.TertiaryColorDark
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tft.model.GalleryItem
import com.example.tft.model.ImageItem
import com.example.tft.templates_App.BackTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(navController: NavHostController, viewModel: PhotosViewModel = viewModel()) {
    val galleryItems by viewModel.galleryItems.observeAsState(emptyList())

    // Inicializar el filtro de año en 2023 y la categoría en WRC por defecto
    var selectedYear by remember { mutableStateOf("2023") }
    var selectedCategory by remember { mutableStateOf("WRC") }

    val years = galleryItems.map { it.year.toString() }.distinct()
    val categories = galleryItems.map { it.category }.distinct()

    Scaffold(
        topBar = {
            BackTopBar(title = "Galería de Fotos", navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Acción para subir imagen */ },
                containerColor = MaterialTheme.colorScheme.secondary  // Color secundario para el botón flotante
            ) {
                Icon(Icons.Filled.Upload, contentDescription = "Subir imagen")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                Column {
                    Text(
                        text = "Filtrar imágenes por:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        YearDropdownMenu(selectedYear, years) { year ->
                            selectedYear = year
                        }
                        CategoryDropdownMenu(selectedCategory, categories) { category ->
                            selectedCategory = category
                        }
                    }
                }
            }
            // Filtra las imágenes por año y categoría
            val filteredItems = galleryItems.filter {
                (it.year.toString() == selectedYear || selectedYear == "All") &&
                        (it.category == selectedCategory || selectedCategory == "All")
            }
            items(filteredItems) { galleryItem ->
                GroupedPhotosCarousel(galleryItem)
            }
        }
    }
}

@Composable
fun YearDropdownMenu(
    selectedYear: String,
    years: List<String>,
    onYearSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,  // Usar color secundario para el botón
            ),
            modifier = Modifier
                .width(120.dp)  // Ajustar el ancho del botón para que sea más pequeño
        ) {
            Text(
                text = "Año: ${if (selectedYear == "All") "Todos" else selectedYear}",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onYearSelected("All")
                    expanded = false
                },
                text = { Text("Todos", style = MaterialTheme.typography.bodySmall) }
            )
            years.forEach { year ->
                DropdownMenuItem(
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    },
                    text = { Text(year, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }
    }
}

@Composable
fun CategoryDropdownMenu(
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,  // Usar color secundario para el botón
                contentColor = Color.Black  // Color del texto en negro
            ),
            modifier = Modifier
                .width(180.dp)  // Ajustar el ancho del botón para que sea más grande
        ) {
            Text(
                text = "Categoría: ${if (selectedCategory == "All") "Todas" else selectedCategory}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black  // Color de texto en negro
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onCategorySelected("All")
                    expanded = false
                },
                text = { Text("Todas", style = MaterialTheme.typography.bodySmall) }
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    text = { Text(category, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }
    }
}


@Composable
fun GroupedPhotosCarousel(galleryItem: GalleryItem) {
    // Agrupa las imágenes por ruta
    val groupedByRoute = galleryItem.images.groupBy { it.route }

    groupedByRoute.forEach { (route, images) ->
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = route,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            PhotosCarousel(images)
        }
    }
}

@Composable
fun PhotosCarousel(images: List<ImageItem>) {
    LazyRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(images) { image ->
            PhotoCard(image)
        }
    }
}

@Composable
fun PhotoCard(image: ImageItem) {
    var isDialogOpen by remember { mutableStateOf(false) }

    if (isDialogOpen) {
        Dialog(onDismissRequest = { isDialogOpen = false }) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Image(
                        painter = rememberImagePainter(image.image),
                        contentDescription = image.description,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { isDialogOpen = false },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(250.dp)
            .clickable { isDialogOpen = true },  // Abrir el diálogo al hacer clic
        shape = RoundedCornerShape(16.dp), // Bordes redondeados más grandes para mejor estética
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Image(
                painter = rememberImagePainter(image.image),
                contentDescription = image.description,
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = image.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ruta",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = image.route,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
