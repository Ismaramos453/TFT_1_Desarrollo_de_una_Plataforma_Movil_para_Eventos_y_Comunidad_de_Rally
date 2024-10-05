package com.example.tft.ui.userProfile


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.tft.R
import com.example.tft.navigation.AppScreens
import com.example.tft.templates_App.BackTopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(navController: NavHostController, viewModel: UserProfileViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.observeAsState()
    val cacheBuster = remember { System.currentTimeMillis().toString() }

    // URL de la imagen predeterminada
    val defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/tft-ismael.appspot.com/o/profile_images%2Fperfil.png?alt=media&token=228f72ea-b277-47d7-b2df-b7c0876cdbb1"

    Scaffold(
        topBar = {
            BackTopBar(title = "Detalles de la Cuenta", navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            userProfile?.let { profile ->
                if (profile.name.isEmpty()) {
                    Text("No hay datos de usuario disponibles.", Modifier.align(Alignment.Center))
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val profileImageUrl = if (profile.image.isNotEmpty()) {
                            "${profile.image}?$cacheBuster"
                        } else {
                            // Usa la URL de imagen predeterminada si no hay imagen de perfil
                            "$defaultProfileImageUrl?$cacheBuster"
                        }

                        Image(
                            painter = rememberImagePainter(profileImageUrl),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(200.dp) // Tamaño del círculo
                                .clip(CircleShape) // Corta la imagen en forma de círculo
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) // Borde alrededor del círculo
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Nombre: ${profile.name}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Correo: ${profile.userId}")
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { navController.navigate(AppScreens.EditProfileScreen.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Editar Perfil", color = MaterialTheme.colorScheme.onSecondary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.signOutUser(navController) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            } ?: run {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}
