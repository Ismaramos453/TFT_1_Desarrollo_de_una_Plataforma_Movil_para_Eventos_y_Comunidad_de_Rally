package com.example.tft.ui.eventDetails
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tft.data.AuthenticationServices
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.events.EventViewModel
import com.example.tft.ui.theme.ColorTextDark
import com.example.tft.ui.theme.PrimaryColor
import com.example.tft.ui.theme.TFTTheme

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(navController: NavHostController, eventId: String) {
    val context = LocalContext.current
    val eventDetailViewModel: EventDetailViewModel = viewModel()
    val event by eventDetailViewModel.event.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var isBookmarked by remember { mutableStateOf(false) }
    val eventViewModel: EventViewModel = viewModel()
    val user = AuthenticationServices.getCurrentUser()

    // Se cambia LaunchedEffect(Unit) por LaunchedEffect(eventId)
    LaunchedEffect(eventId) {
        eventDetailViewModel.loadEventById(eventId)
        user?.let {
            eventViewModel.loadSavedEvents(it.uid)
        }
    }

    LaunchedEffect(event) {
        event?.let {
            isBookmarked = eventViewModel.events.value?.any { savedEvent -> savedEvent.id == event!!.id } ?: false

        }
    }


    TFTTheme {
        Scaffold(
            topBar = {
                BackTopBar(title = "Detalles del evento", navController = navController)
            }
        ) { innerPadding ->
            event?.let { eventDetails ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    item {
                        eventDetails.image?.let { imageUrl ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Event Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = eventDetails.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = {
                                    isBookmarked = !isBookmarked
                                    if (isBookmarked) {
                                        user?.let {
                                            eventViewModel.addEvent(it.uid, eventDetails)  // Pass the complete event
                                            dialogMessage = "Evento guardado exitosamente"
                                            showDialog = true
                                        }
                                    } else {
                                        user?.let {
                                            eventViewModel.removeEvent(it.uid, eventDetails.id)
                                            dialogMessage = "Evento eliminado de tus guardados"
                                            showDialog = true
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = if (isBookmarked) "Evento guardado" else "Guardar evento",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(36.dp)  // Ajusta este valor según la necesidad de tamaño
                                )

                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${eventDetails.date}, ${eventDetails.time}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )


                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Ubicación",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = eventDetails.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Clase",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = eventDetails.type,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = eventDetails.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Justify
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Displaying Rally Route Images
                        Text(
                            text = "Rally Route Images",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )


                        Spacer(modifier = Modifier.height(8.dp))

                        eventDetails.images?.let { images ->
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)  // Space between images
                            ) {
                                items(images) { imageUrl ->
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Rally Route Image",
                                        modifier = Modifier
                                            .size(120.dp)  // Smaller size for side by side
                                            .clip(MaterialTheme.shapes.small)
                                            .clickable { selectedImage = imageUrl },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Comentarios",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Event not found",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        // Dialog to show enlarged image with zoom functionality
        selectedImage?.let { imageUrl ->
            Dialog(onDismissRequest = { selectedImage = null }) {
                ZoomableImageDialog(imageUrl = imageUrl, onDismiss = { selectedImage = null })
            }
        }

        if (showDialog) {
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else PrimaryColor
            val dialogTextColor = if (isSystemInDarkTheme) ColorTextDark else Color.White

            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Aceptar", color = dialogTextColor)
                    }
                },
                title = { Text(text = "Evento Añadido", color = dialogTextColor) },
                text = { Text(text = dialogMessage, color = dialogTextColor) },
                containerColor = dialogBackgroundColor,
                tonalElevation = 8.dp
            )
        }
    }
}


@Composable
fun ZoomableImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)  // Ensuring the black background fills the screen
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            ZoomableImage(imageUrl = imageUrl)
        }
    }
}

@Composable
fun ZoomableImage(imageUrl: String) {
    val context = LocalContext.current

    // State for tracking the current scale, rotation, and translation of the image
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotationState by remember { mutableStateOf(0f) }

    // Modifier to handle zooming and panning
    val modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)  // Black background to fill entire screen
        .graphicsLayer(
            scaleX = scale,
            scaleY = scale,
        )
        .transformable(
            state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                scale *= zoomChange
                offset += offsetChange
                rotationState += rotationChange
            }
        )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Zoomable Rally Route Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)  // Ensure the image scales correctly to fit the screen width
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Fit
        )
    }
}
