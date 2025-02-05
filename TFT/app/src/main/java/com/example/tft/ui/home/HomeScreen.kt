package com.example.tft.ui.home
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.tft.templates_App.DefaultTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.tft.model.News
import com.example.tft.model.RallyEvent
import com.example.tft.navigation.AppScreens
import java.lang.Math.abs


import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, homeViewModel: HomeViewModel = viewModel()) {
    val rallyEvents by homeViewModel.filteredEvents.observeAsState(emptyList())
    val categories = homeViewModel.categories
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedLevel by remember { mutableStateOf("All") }
    val searchQuery = remember { mutableStateOf("") }

    // Obtener la fecha actual
    val currentDate = LocalDate.now()
    // Calcular el evento cuya fecha esté más cerca de la fecha actual
    val closestEvent = rallyEvents.minByOrNull { event ->
        try {
            val eventDate = LocalDate.parse(event.date)
            abs(ChronoUnit.DAYS.between(currentDate, eventDate))
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }

    // Separamos los eventos futuros y pasados (esto puede seguir usándose para otros listados)
    val recentEvents = rallyEvents.filter {
        try {
            LocalDate.parse(it.date).isAfter(currentDate)
        } catch (e: Exception) {
            false
        }
    }
    val allOtherEvents = rallyEvents.filter {
        try {
            !LocalDate.parse(it.date).isAfter(currentDate)
        } catch (e: Exception) {
            true
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(navController = navController, searchQuery = searchQuery)
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (searchQuery.value.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Mostrar el evento destacado (el más cercano a hoy) si existe y tiene imagen
                    if (closestEvent != null && closestEvent.image != null) {
                        item {
                            FeaturedEventImage(closestEvent, navController)
                        }
                    }

                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Noticias",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            NewsCarousel(homeViewModel.news.value?.take(5) ?: listOf(), navController)
                            Text(
                                text = "Ver todas las noticias",
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clickable { navController.navigate("Home_Screens/News_Screen") },
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    item {
                        if (recentEvents.isNotEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Próximos eventos",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                EventsCarousel(recentEvents.take(5), navController)
                                Text(
                                    text = "Ver todos los eventos",
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .clickable { navController.navigate("Home_Screens/AllEvents_Screen") },
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    item {
                        if (allOtherEvents.isNotEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Eventos pasados",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                EventsCarousel(allOtherEvents, navController)
                            }
                        }
                    }
                }
            } else {
                // Si hay búsqueda, filtrar eventos y noticias
                val filteredEvents = rallyEvents.filter { event ->
                    event.title.contains(searchQuery.value, ignoreCase = true) ||
                            event.description.contains(searchQuery.value, ignoreCase = true)
                }
                val filteredNews = homeViewModel.news.value?.filter { news ->
                    news.title.contains(searchQuery.value, ignoreCase = true)
                } ?: emptyList()

                Column {
                    if (filteredEvents.isNotEmpty()) {
                        Text("Eventos Relacionados", style = MaterialTheme.typography.titleMedium)
                        EventsCarousel(filteredEvents, navController)
                    }
                    if (filteredNews.isNotEmpty()) {
                        Text("Noticias Relacionadas", style = MaterialTheme.typography.titleMedium)
                        NewsCarousel(filteredNews, navController)
                    }
                    if (filteredEvents.isEmpty() && filteredNews.isEmpty()) {
                        Text("No se encontraron resultados para \"${searchQuery.value}\"", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedEventImage(event: RallyEvent, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable { navController.navigate(AppScreens.EventDetailScreen.route + "/${event.id}") },
        shape = RoundedCornerShape(0.dp)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(event.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen destacada del evento",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f,
                            endY = Float.POSITIVE_INFINITY
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
@Composable
fun IconCategoryButton(label: String, iconResourceId: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(65.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconResourceId),
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
@Composable
fun PageIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        for (i in 0 until totalPages) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (i == currentPage) activeColor else inactiveColor)
            )
            if (i != totalPages - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
fun EventsCarousel(events: List<RallyEvent>, navController: NavHostController) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val totalPages = events.size
    val currentPage by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex)
        }
    }

    Column {
        LazyRow(
            state = listState,
            modifier = Modifier
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(events) { event ->
                RallyEventCard(event, navController)
            }
        }
        PageIndicator(
            totalPages = totalPages,
            currentPage = currentPage,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun NewsCarousel(news: List<News>, navController: NavHostController) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val totalPages = news.size
    val currentPage by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex)
        }
    }

    Column {
        LazyRow(
            state = listState,
            modifier = Modifier
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(news) { newsItem ->
                NewsCard(newsItem, navController)
            }
        }
        PageIndicator(
            totalPages = totalPages,
            currentPage = currentPage,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}


@Composable
fun RallyEventCard(event: RallyEvent, navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(240.dp)
            .padding(end = 8.dp)
            .clickable {
                navController.navigate(AppScreens.EventDetailScreen.route + "/${event.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Box {
            if (event.image != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(event.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen del evento",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No image", color = MaterialTheme.colorScheme.onPrimary)
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
                text = event.title.ifEmpty { "No title available" },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }

        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = event.type.ifEmpty { "No type available" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${event.date.ifEmpty { "No date" }}, ${event.time.ifEmpty { "No time" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = event.location.ifEmpty { "No location available" },
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun NewsCard(news: News, navController: NavController){
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .padding(end = 8.dp)
            .clickable {   navController.navigate(AppScreens.NewsDetailScreen.route.replace("{newsId}", news.id)) },
        shape = RoundedCornerShape(8.dp),
    ) {
        Box {
            Image(
                painter = rememberImagePainter(news.image),
                contentDescription = "Imagen de Noticia",
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
                text = news.title,
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