package com.example.tft.ui.EditProfile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tft.templates_App.BackTopBar
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import com.example.tft.R
import com.example.tft.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, viewModel: EditProfileViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.observeAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            name = profile.name ?: ""
            email = profile.email ?: ""
            if (imageUri == null) {
                imageUri = profile.image?.let { Uri.parse(it) }
            }
        }
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Editar Perfil", navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen de perfil del usuario
                Spacer(modifier = Modifier.height(32.dp))
                imageUri?.let { uri ->
                    Image(
                        painter = rememberImagePainter(uri),
                        contentDescription = "Imagen de perfil actual",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                } ?: run {
                    // Placeholder si no hay imagen seleccionada
                    Image(
                        painter = painterResource(id = R.drawable.default_profile_image), 
                        contentDescription = "Imagen de perfil por defecto",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Botón para seleccionar una nueva foto de perfil
                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Seleccionar Foto de Perfil", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = { navController.navigate(AppScreens.ChangePasswordScreen.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Cambiar Contraseña", color = MaterialTheme.colorScheme.onSecondary)
                }
                Spacer(modifier = Modifier.height(32.dp))
                // Campos de texto para editar el nombre y correo
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón para guardar cambios
                Button(
                    onClick = {
                        viewModel.updateUserProfileWithImage(name, email, imageUri) {
                            navController.popBackStack()
                            navController.navigate(AppScreens.UserProfileScreen.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Guardar Cambios", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
