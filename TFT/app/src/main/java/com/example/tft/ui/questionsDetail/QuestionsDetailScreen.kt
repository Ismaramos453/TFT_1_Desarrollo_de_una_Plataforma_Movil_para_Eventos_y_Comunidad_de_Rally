package com.example.tft.ui.questionsDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tft.R
import com.example.tft.templates_App.BackTopBar
import com.example.tft.ui.other.CustomDivider
import com.example.tft.ui.theme.PrimaryColor
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.ZoneId
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction

import androidx.lifecycle.viewmodel.compose.viewModel
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionsDetailScreen(navController: NavHostController, questionId: String) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var answerContent by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val viewModel: QuestionsDetailViewModel = viewModel()
    val question by viewModel.getQuestionDetail(questionId).observeAsState()

    Scaffold(
        topBar = {
            BackTopBar(title = "Detalle de pregunta", navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.enviar),
                    contentDescription = "Añadir respuesta",
                    modifier = Modifier.size(24.dp) // Ajusta este valor según tus necesidades
                )
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            question?.let {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = it.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Por: ${it.userName}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fecha: ${Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.End)
                            )

                            if (it.userId == currentUserId) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        title = it.title
                                        content = it.content
                                        showEditDialog = true
                                    }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Editar pregunta")
                                    }
                                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar pregunta")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Respuestas:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    it.answers.forEach { answer ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = answer.userName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = answer.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Fecha: ${Instant.ofEpochMilli(answer.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            } ?: run {
                CircularProgressIndicator()
            }

            if (showDialog) {
                val isSystemInDarkTheme = isSystemInDarkTheme()
                val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                val dialogTextColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.onSurface else Color.White

                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            if (answerContent.isNotEmpty()) {
                                viewModel.addAnswer(questionId, answerContent) { success ->
                                    if (success) {
                                        answerContent = ""
                                        showDialog = false
                                    }
                                }
                            }
                        }) {
                            Text("Aceptar", color = dialogTextColor)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar", color = dialogTextColor)
                        }
                    },
                    title = { Text(text = "Añadir respuesta", color = dialogTextColor) },
                    text = {
                        OutlinedTextField(
                            value = answerContent,
                            onValueChange = { answerContent = it },
                            label = { Text("Escribe tu respuesta", color = dialogTextColor) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(color = dialogTextColor),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = dialogTextColor,
                                unfocusedBorderColor = dialogTextColor,
                                cursorColor = dialogTextColor
                            )
                        )
                    },
                    containerColor = dialogBackgroundColor
                )
            }

            if (showDeleteConfirmDialog) {
                val isSystemInDarkTheme = isSystemInDarkTheme()
                val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                val dialogTextColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.onSurface else Color.White

                AlertDialog(
                    onDismissRequest = { showDeleteConfirmDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteQuestion(questionId) { success ->
                                if (success) navController.popBackStack()
                            }
                            showDeleteConfirmDialog = false
                        }) {
                            Text("Eliminar", color = dialogTextColor)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmDialog = false }) {
                            Text("Cancelar", color = dialogTextColor)
                        }
                    },
                    title = { Text(text = "Confirmar eliminación", color = dialogTextColor) },
                    text = { Text("¿Estás seguro de que quieres eliminar esta pregunta? Esta acción es irreversible.", color = dialogTextColor) },
                    containerColor = dialogBackgroundColor
                )
            }

            if (showEditDialog) {
                val isSystemInDarkTheme = isSystemInDarkTheme()
                val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                val dialogTextColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.onSurface else Color.White

                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.updateQuestion(questionId, title, content) {
                                showEditDialog = false
                            }
                        }) {
                            Text("Actualizar", color = dialogTextColor)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancelar", color = dialogTextColor)
                        }
                    },
                    title = { Text(text = "Editar pregunta", color = dialogTextColor) },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Título", color = dialogTextColor) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(color = dialogTextColor),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = dialogTextColor,
                                    unfocusedBorderColor = dialogTextColor,
                                    cursorColor = dialogTextColor
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = content,
                                onValueChange = { content = it },
                                label = { Text("Contenido", color = dialogTextColor) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(color = dialogTextColor),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = dialogTextColor,
                                    unfocusedBorderColor = dialogTextColor,
                                    cursorColor = dialogTextColor
                                )
                            )
                        }
                    },
                    containerColor = dialogBackgroundColor
                )
            }
        }
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