package com.example.tft.ui.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachEmail
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tft.model.notification.NotificationService
import com.example.tft.templates_App.BackTopBar
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.ZoneId
import androidx.lifecycle.viewmodel.compose.viewModel
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavHostController, viewModel: NotificationViewModel = viewModel()) {
    val notifications by viewModel.notifications.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /* Acción para la navegación o menú */ }) {
                        Icon(Icons.Filled.AttachEmail, contentDescription = "Otros", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                notifications.forEach { notification ->
                    val notificationId = notification["id"].toString() // Asegúrate de tener el ID de la notificación

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                val message = notification["message"].toString()
                                val isResponseMessage = message.contains("un usuario te ha respondido", ignoreCase = true)

                                Text(
                                    text = message,
                                    style = if (isResponseMessage) {
                                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    } else {
                                        MaterialTheme.typography.bodyLarge
                                    },
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                notification["timestamp"]?.let { timestamp ->
                                    val formattedDate = Instant.ofEpochMilli(timestamp as Long)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    Text(
                                        text = "Fecha: $formattedDate",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Black
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
