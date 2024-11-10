package com.example.tft.ui.other

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tft.navigation.AppScreens
import com.example.tft.templates_App.DefaultTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(navController: NavHostController) {

    val searchQuery = remember { mutableStateOf("") } // Convertido en MutableState
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Otras secciones", color = Color.White) }, // Color del título ajustado a blanco
                navigationIcon = {
                    // Puedes cambiar Icons.Filled.Menu por Icons.Filled.ArrowBack si es para navegación hacia atrás
                    IconButton(onClick = { /* Define la acción aquí, como navController.popBackStack() o abrir un drawer */ }) {
                        Icon(Icons.Filled.AutoAwesomeMosaic, contentDescription = "Otros", tint = Color.White)
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
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SectionItem(
                    text = "Pilotos Favoritos",
                    icon = Icons.Default.Star,
                    onClick = { navController.navigate(AppScreens.PilotFavoritesScreen.route) }
                )
                CustomDivider()
                SectionItem(
                    text = "Eventos guardados",
                    icon = Icons.Default.CheckCircleOutline,
                    onClick = { navController.navigate(AppScreens.EventScreen.route) }
                )
                CustomDivider()
                SectionItem(
                    text = "Mi calendario",
                    icon = Icons.Default.CalendarMonth,
                    onClick = { navController.navigate(AppScreens.CalendarScreen.route) }
                )
                CustomDivider()
                SectionItem(
                    text = "Galería",
                    icon = Icons.Default.AddAPhoto,
                    onClick = { navController.navigate(AppScreens.PhotosScreen.route) }
                )
                CustomDivider()
                CustomDivider()
                SectionItem(
                    text = "Vídeos",
                    icon = Icons.Default.Cast,
                    onClick = { navController.navigate(AppScreens.VideoScreen.route) }
                )
                CustomDivider()
                SectionItem(
                    text = "Mi Cuenta",
                    icon = Icons.Default.Person,
                    onClick = { navController.navigate(AppScreens.UserProfileScreen.route) }
                )
                CustomDivider()
                SectionItem(
                    text = "Ajustes",
                    icon = Icons.Default.Settings,
                    onClick = { navController.navigate(AppScreens.SettingScreen.route) }
                )
                CustomDivider()
                SectionItem(
                    text = "Preguntas y Reportes",
                    icon = Icons.Default.Help,
                    onClick = { navController.navigate(AppScreens.FaqScreen.route) }
                )
            }
        }
    }
}

@Composable
fun SectionItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = MaterialTheme.colorScheme.onSurface
    val iconTintColor = if (isDarkTheme) Color.White else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTintColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = textColor
        )
    }
}

@Composable
fun CustomDivider() {
    val isDarkTheme = isSystemInDarkTheme()
    // Define el color del divisor dependiendo del tema
    val dividerColor = if (isDarkTheme) Color.Gray else Color.LightGray  // Cambia estos colores según tu preferencia

    Divider(
        color = dividerColor,
        thickness = 1.dp  // Puedes ajustar el grosor si lo deseas
    )
}