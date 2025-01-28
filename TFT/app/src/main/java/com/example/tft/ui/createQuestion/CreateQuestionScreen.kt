package com.example.tft.ui.createQuestion

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tft.templates_App.BackTopBar
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tft.navigation.AppScreens
import com.example.tft.ui.theme.ColorText
import com.example.tft.ui.theme.ColorTextDark
import com.example.tft.ui.theme.TertiaryColorDark


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionScreen(navController: NavHostController, viewModel: CreateQuestionViewModel = viewModel()) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var questionType by remember { mutableStateOf("Pregunta Normal") }
    val questionOptions = listOf("Pregunta Normal", "Votación")
    val context = LocalContext.current
    var successState by remember { mutableStateOf<Boolean?>(null) }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }

    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) ColorTextDark else ColorText
    val containerColor = if (isDarkTheme) TertiaryColorDark else Color.White

    Scaffold(
        topBar = {
            BackTopBar(title = "Crear pregunta", navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título", color = textColor) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = textColor,
                    containerColor = containerColor,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = textColor
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            DropdownMenuComponent(questionType, questionOptions) {
                questionType = it
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (questionType == "Pregunta Normal") {
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido", color = textColor) },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = textColor,
                        containerColor = containerColor,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = textColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                TextField(
                    value = option1,
                    onValueChange = { option1 = it },
                    label = { Text("Opción 1", color = textColor) },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = textColor,
                        containerColor = containerColor,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = option2,
                    onValueChange = { option2 = it },
                    label = { Text("Opción 2", color = textColor) },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = textColor,
                        containerColor = containerColor,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        if (questionType == "Votación" && option1.isNotEmpty() && option2.isNotEmpty()) {
                            viewModel.addVotation(title, userId, listOf(option1, option2)) { success, votationId ->
                                successState = success
                                if (success && votationId != null) {
                                    Log.d("CreateQuestionScreen", "Votation created with ID: $votationId")
                                    navController.navigate(AppScreens.CommunityScreen.route) {
                                        popUpTo(AppScreens.CommunityScreen.route) { inclusive = true }
                                    }
                                }
                            }
                        } else if (questionType == "Pregunta Normal" && content.isNotEmpty()) {
                            viewModel.addQuestion(title, content, userId) { success ->
                                successState = success
                                if (success) {
                                    navController.navigate(AppScreens.CommunityScreen.route) {
                                        popUpTo(AppScreens.CommunityScreen.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }

    successState?.let { success ->
        HandleResponse(context, success) {
            navController.navigate(AppScreens.CommunityScreen.route) {
                popUpTo(AppScreens.CommunityScreen.route) { inclusive = true }
            }
        }
    }
}

@Composable
fun HandleResponse(context: Context, success: Boolean, onDone: () -> Unit) {
    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show()
            onDone()
        } else {
            Toast.makeText(context, "Error al realizar la operación", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun DropdownMenuComponent(
    currentType: String,
    options: List<String>,
    onTypeChange: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) ColorTextDark else ColorText
    val backgroundColor = if (isDarkTheme) TertiaryColorDark else Color.White

    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier.width(180.dp)
        ) {
            Text(
                text = "Tipo: $currentType",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(backgroundColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onTypeChange(option)
                        expanded = false
                    },
                    text = { Text(option, style = MaterialTheme.typography.bodySmall, color = textColor) }
                )
            }
        }
    }
}
