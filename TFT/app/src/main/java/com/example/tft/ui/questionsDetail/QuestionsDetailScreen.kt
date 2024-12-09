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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.ZoneId
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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

    // Determinar si está en modo oscuro y asignar el color de texto
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Detalle de pregunta",
                navController = navController,
            )
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
                    modifier = Modifier.size(24.dp),
                    tint = textColor
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
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Por: ${it.userName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fecha: ${Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.7f),
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
                                        Icon(Icons.Filled.Edit, contentDescription = "Editar pregunta", tint = textColor)
                                    }
                                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar pregunta", tint = textColor)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Respuestas:", style = MaterialTheme.typography.titleMedium, color = textColor)
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
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = answer.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Fecha: ${Instant.ofEpochMilli(answer.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textColor.copy(alpha = 0.7f),
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            } ?: run {
                CircularProgressIndicator(color = textColor)
            }

            // Diálogo para agregar respuesta
            if (showDialog) {
                val dialogBackgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                val dialogTextColor = if (isDarkTheme) Color.White else Color.White

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

            // Diálogo de confirmación de eliminación
            if (showDeleteConfirmDialog) {
                val dialogBackgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                val dialogTextColor = if (isDarkTheme) Color.White else Color.White

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

            // Diálogo para editar pregunta
            if (showEditDialog) {
                val dialogBackgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                val dialogTextColor = if (isDarkTheme) Color.White else Color.White

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
