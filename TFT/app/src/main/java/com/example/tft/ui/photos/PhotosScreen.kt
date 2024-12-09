package com.example.tft.ui.photos
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.tft.model.GalleryItem
import com.example.tft.model.ImageItem
import com.example.tft.templates_App.BackTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(navController: NavHostController, viewModel: PhotosViewModel = viewModel()) {
    val galleryItems by viewModel.galleryItems.observeAsState(emptyList())
    var selectedYear by remember { mutableStateOf("2023") }
    var selectedCategory by remember { mutableStateOf("WRC") }
    val years = galleryItems.map { it.year.toString() }.distinct()
    val categories = galleryItems.map { it.category }.distinct()

    Scaffold(
        topBar = {
            BackTopBar(title = "Galería de Fotos", navController = navController)
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
                containerColor = MaterialTheme.colorScheme.secondary,
            ),
            modifier = Modifier
                .width(120.dp)
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
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .width(180.dp)
        ) {
            Text(
                text = "Categoría: ${if (selectedCategory == "All") "Todas" else selectedCategory}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
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
            .clickable { isDialogOpen = true },
        shape = RoundedCornerShape(16.dp),
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
