package com.example.tft.ui.login
import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tft.R
import com.example.tft.navigation.AppScreens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException



import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    Box(Modifier.fillMaxSize()) {
        BackgroundImage()
        LoginContent(navController, viewModel)
    }
}

@Composable
fun LoginContent(navController: NavController, viewModel: LoginViewModel) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val emailError by viewModel.emailError.observeAsState()
    val passwordError by viewModel.passwordError.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeaderImage()
                Text(
                    "RallyWorld",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(25.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTextField(
                        valueState = email,
                        label = "Correo electrónico",
                        modifier = Modifier.fillMaxWidth(),
                        error = emailError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(
                        valueState = password,
                        label = "Contraseña",
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        error = passwordError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "¿Olvidaste tu contraseña?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(245, 138, 38, 255),
                        modifier = Modifier.clickable {
                            navController.navigate(route = AppScreens.ForgotPasswordScreen.route)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoginButton(navController, email.value, password.value, viewModel)
                Spacer(modifier = Modifier.height(16.dp))
                GoogleSignInButton(viewModel) {
                    navController.navigate(route = AppScreens.HomeScreen.route)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("¿No tienes cuenta?", fontSize = 16.sp, color = Color.White)
                Text(
                    "Regístrate",
                    fontSize = 16.sp,
                    color = Color(245, 138, 38, 255),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate(route = AppScreens.RegisterScreen.route) }
                )
            }
        }
    }
}

@Composable
fun HeaderImage() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Header",
        modifier = Modifier.size(200.dp)
    )
}

@Composable
fun BackgroundImage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun LoginButton(
    navController: NavController,
    email: String,
    password: String,
    viewModel: LoginViewModel
) {
    Button(
        onClick = {
            viewModel.signInWithEmailAndPassword(email, password) {
                navController.navigate(route = AppScreens.HomeScreen.route)
            }
        },
        colors = ButtonDefaults.buttonColors(Color(245, 138, 38, 255)),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            text = "Iniciar sesión",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun GoogleSignInButton(viewModel: LoginViewModel, home: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken, home)
                }
            } catch (e: ApiException) {
                Log.w("RallyWord", "Google sign in failed", e)
            }
        }
    }

    Button(
        onClick = {
            val googleSignInClient = GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            )
            launcher.launch(googleSignInClient.signInIntent)
        },
        colors = ButtonDefaults.buttonColors(Color.White),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = com.google.android.gms.base.R.drawable.googleg_standard_color_18),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Login con Google",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    valueState: MutableState<String>,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    error: String? = null
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
            visualTransformation = visualTransformation,
            isError = error != null
        )
        if (error != null) {
            Text(text = error, color = Color.Red)
        }
    }
}
