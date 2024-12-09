package com.example.tft.ui.votationDetail

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tft.model.foro.Votation
import com.example.tft.templates_App.BackTopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotationDetailScreen(navController: NavHostController, votationId: String, viewModel: VotationDetailViewModel) {
    val votation by viewModel.getVotationDetail(votationId).observeAsState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf("") }
    var editedOptions by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            BackTopBar(title = "Detalle de Votación", navController = navController)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            votation?.let { vot ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        VotationDetails(
                            votation = vot,
                            isOwner = viewModel.isUserOwner(vot),
                            onVote = { id, option -> viewModel.voteOnVotation(id, option) { /* Handle vote result */ } },
                            onEdit = {
                                editedTitle = vot.title
                                editedOptions = vot.options
                                showEditDialog = true
                            },
                            onDelete = { showDeleteConfirmDialog = true }
                        )

                        if (showDeleteConfirmDialog) {
                            val isSystemInDarkTheme = isSystemInDarkTheme()
                            val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                            val dialogTextColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.onSurface else Color.White

                            AlertDialog(
                                onDismissRequest = { showDeleteConfirmDialog = false },
                                title = { Text("Eliminar Votación", color = dialogTextColor) },
                                text = { Text("¿Estás seguro de querer eliminar esta votación? Esta acción es irreversible.", color = dialogTextColor) },
                                confirmButton = {
                                    TextButton(onClick = {
                                        viewModel.deleteVotation(votationId) { success ->
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
                                containerColor = dialogBackgroundColor
                            )
                        }


                        if (showEditDialog) {
                            var editedTitleState by remember { mutableStateOf(editedTitle) }
                            var editedOptionsState by remember { mutableStateOf(editedOptions) }
                            val isSystemInDarkTheme = isSystemInDarkTheme()
                            val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
                            val dialogTextColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.onSurface else Color.White

                            AlertDialog(
                                onDismissRequest = { showEditDialog = false },
                                title = { Text("Editar Votación", color = dialogTextColor) },
                                text = {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = editedTitleState,
                                            onValueChange = { editedTitleState = it },
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

                                        editedOptionsState.forEachIndexed { index, option ->
                                            OutlinedTextField(
                                                value = option,
                                                onValueChange = { newOption ->
                                                    val newOptions = editedOptionsState.toMutableList()
                                                    newOptions[index] = newOption
                                                    editedOptionsState = newOptions
                                                },
                                                label = { Text("Opción ${index + 1}", color = dialogTextColor) },
                                                modifier = Modifier.fillMaxWidth(),
                                                textStyle = LocalTextStyle.current.copy(color = dialogTextColor),
                                                colors = TextFieldDefaults.outlinedTextFieldColors(

                                                    focusedBorderColor = dialogTextColor,
                                                    unfocusedBorderColor = dialogTextColor,
                                                    cursorColor = dialogTextColor
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        Button(
                                            onClick = {
                                                editedOptionsState = editedOptionsState + ""  // Agregar una nueva opción vacía
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Agregar opción", color = dialogTextColor)
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val updatedVotation = vot.copy(title = editedTitleState, options = editedOptionsState)
                                        viewModel.updateVotation(votationId, updatedVotation) { success ->
                                            if (success) showEditDialog = false
                                        }
                                    }) {
                                        Text("Guardar", color = dialogTextColor)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showEditDialog = false }) {
                                        Text("Cancelar", color = dialogTextColor)
                                    }
                                },
                                containerColor = dialogBackgroundColor
                            )
                        }



                    }
                }
            } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVotationDialog(
    title: String,
    options: List<String>,
    onTitleChange: (String) -> Unit,
    onOptionsChange: (List<String>) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var editedTitle by remember { mutableStateOf(title) }
    var editedOptions by remember { mutableStateOf(options) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Votación") },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                TextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it; onTitleChange(it) },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Manejo dinámico de opciones
                editedOptions.forEachIndexed { index, option ->
                    TextField(
                        value = option,
                        onValueChange = { newOption ->
                            val newOptions = editedOptions.toMutableList()
                            newOptions[index] = newOption
                            editedOptions = newOptions
                            onOptionsChange(newOptions)
                        },
                        label = { Text("Opción ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        editedOptions = editedOptions + ""
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Agregar opción")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}



@Composable
fun ConfirmActionDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}
@Composable
fun VotationDetails(votation: Votation, isOwner: Boolean, onVote: (String, String) -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Votación por: ${votation.userName}", style = MaterialTheme.typography.bodyMedium)
        Text(votation.title, style = MaterialTheme.typography.titleMedium)
        // Calcular el total de votos
        val totalVotes = votation.votes.values.sum()

        votation.options.forEach { option ->
            val votesForOption = votation.votes[option] ?: 0
            val percentage = if (totalVotes > 0) (votesForOption / totalVotes.toFloat()) * 100 else 0f

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVote(votation.id, option) }
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = votation.userVote == option,
                    onClick = { onVote(votation.id, option) }
                )
                Text(option, style = MaterialTheme.typography.bodySmall)
            }

            LinearProgressIndicator(
                progress = percentage / 100,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${"%.1f".format(percentage)}% ($votesForOption votos)",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Mostrar iconos de acción si el usuario es el propietario
        if (isOwner) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar votación")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar votación")
                }
            }
        }
    }
}
