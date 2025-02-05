package com.example.tft.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tft.navigation.AppScreens
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.login.BackgroundImage
import com.example.tft.ui.login.HeaderImage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    Scaffold(
        topBar = {
            BackTopBar(title = "Registro de usuario", navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BackgroundImage()
            RegisterContent(navController)
        }
    }
}

@Composable
fun RegisterContent(navController: NavController, viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatPassword = remember { mutableStateOf("") }
    val image = ""
    val emailError by viewModel.emailError.observeAsState()
    val passwordError by viewModel.passwordError.observeAsState()
    val usernameError by viewModel.usernameError.observeAsState()
    val passwordMismatchError by viewModel.passwordMismatchError.observeAsState()
    val registrationState by viewModel.registrationState.observeAsState(RegisterViewModel.RegistrationState.Idle)

    val passwordVisibility = remember { mutableStateOf(false) }
    val repeatPasswordVisibility = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            HeaderImage()
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                text = "RallyWorld",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            CustomTextField(
                valueState = username,
                label = "Nombre de usuario",
                modifier = Modifier.fillMaxWidth(),
                error = usernameError
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            CustomTextField(
                valueState = email,
                label = "Correo electrónico",
                modifier = Modifier.fillMaxWidth(),
                error = emailError
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            CustomTextField(
                valueState = password,
                label = "Contraseña",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                error = passwordError,
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                        Icon(
                            imageVector = if (passwordVisibility.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisibility.value) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            CustomTextField(
                valueState = repeatPassword,
                label = "Repetir Contraseña",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (repeatPasswordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { repeatPasswordVisibility.value = !repeatPasswordVisibility.value }) {
                        Icon(
                            imageVector = if (repeatPasswordVisibility.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (repeatPasswordVisibility.value) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            passwordMismatchError?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            RegisterButton(navController, viewModel, email.value, password.value, repeatPassword.value, username.value, image)  // Muestra el botón en el centro
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            if (registrationState is RegisterViewModel.RegistrationState.Failure) {
                Text(
                    text = (registrationState as RegisterViewModel.RegistrationState.Failure).errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    valueState: MutableState<String>,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    error: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = valueState.value,
            onValueChange = { valueState.value = it },
            label = { Text(label, color = Color.White) },
            modifier = modifier,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            keyboardActions = keyboardActions,
            isError = error != null,
            trailingIcon = trailingIcon
        )
        if (error != null) {
            Text(text = error, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RegisterButton(navController: NavController, viewModel: RegisterViewModel, email: String, password: String, repeatPassword: String, name: String, image: String) {
    val registrationState by viewModel.registrationState.observeAsState(RegisterViewModel.RegistrationState.Idle)

    Button(
        onClick = { viewModel.register(email, password, repeatPassword, name, image) },
        colors = ButtonDefaults.buttonColors(Color(245, 138, 38, 255)),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            text = "Registrarse",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }

    when (registrationState) {
        is RegisterViewModel.RegistrationState.Loading -> {
            CircularProgressIndicator()
        }
        is RegisterViewModel.RegistrationState.Success -> {
            LaunchedEffect(Unit) {
                navController.navigate(route = AppScreens.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
        is RegisterViewModel.RegistrationState.Failure -> {
            val errorMessage = (registrationState as RegisterViewModel.RegistrationState.Failure).errorMessage
            Text(text = errorMessage, color = Color.Red)
        }
        is RegisterViewModel.RegistrationState.Idle -> {
        }
    }
}

