package com.example.tft.ui.forgotpassword


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tft.ui.login.BackgroundImage
import com.example.tft.ui.login.CustomTextField
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tft.templates_App.BackTopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: ForgotPasswordViewModel = viewModel(ForgotPasswordViewModel::class.java)) {
    Scaffold(
        topBar = {
            BackTopBar(title = "Restablecer Contraseña", navController = navController)
        }
    ) { innerPadding ->
        ForgotPasswordContent(navController, viewModel, innerPadding)
    }
}

@Composable
fun ForgotPasswordContent(navController: NavController, viewModel: ForgotPasswordViewModel, contentPadding: PaddingValues) {
    val email = remember { mutableStateOf("") }
    val emailError by viewModel.emailError.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        BackgroundImage()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 36.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Restablecer Contraseña", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            CustomTextField(
                valueState = email,
                label = "Correo electrónico",
                modifier = Modifier.fillMaxWidth(),
                error = emailError
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.sendPasswordResetEmail(email.value) {
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(245, 138, 38, 255)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Enviar correo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}


