package com.example.tft

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tft.data.Worker.NotificationUtils
import com.example.tft.data.services.Authentication.AuthenticationServices
import com.example.tft.navigation.AppNavigation
import com.example.tft.navigation.AppScreens
import com.example.tft.ui.bottonBar.BottonBarScreen
import com.example.tft.ui.settings.SettingViewModel


import com.example.tft.ui.theme.TFTTheme

@Composable
fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return currentRoute != AppScreens.LoginScreen.route && currentRoute != AppScreens.RegisterScreen.route
            && currentRoute != AppScreens.ForgotPasswordScreen.route
}

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TFTTheme {
                val navController = rememberNavController()
                var isLoading by remember { mutableStateOf(true) }
                var startDestination by remember { mutableStateOf(AppScreens.LoginScreen.route) }
                val settingViewModel: SettingViewModel by viewModels() // Asegura que estás usando el ViewModel adecuadamente

                // Verifica si el usuario existe o está autenticado
                AuthenticationServices.checkIfUserExists { userExists ->
                    startDestination = if (userExists) AppScreens.HomeScreen.route else AppScreens.LoginScreen.route
                    isLoading = false
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Scaffold(
                        bottomBar = {
                            if (shouldShowBottomBar(currentRoute)) {
                                BottonBarScreen(navController)
                            }
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            AppNavigation(navController, startDestination)
                        }
                    }

                    // Verificar estado de notificaciones al iniciar
                    LaunchedEffect(key1 = settingViewModel.areNotificationsEnabled) {
                        if (settingViewModel.areNotificationsEnabled) {
                            NotificationUtils.scheduleEventNotifications(applicationContext)


                        } else {
                            NotificationUtils.cancelEventNotifications(applicationContext)
                        }
                    }
                }
            }
        }
    }
}




