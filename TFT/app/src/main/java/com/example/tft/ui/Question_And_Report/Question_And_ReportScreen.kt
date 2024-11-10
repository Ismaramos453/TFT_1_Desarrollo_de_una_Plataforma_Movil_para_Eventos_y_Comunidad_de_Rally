package com.example.tft.ui.Question_And_Report

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.tft.templates_App.BackTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tft.model.Questions_And_BugReport.SeverityLevel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.tft.ui.theme.ColorTextDark


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavHostController, viewModel: Question_And_ReportViewModel) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(SeverityLevel.LIGHT) }
    var selectedOption by remember { mutableStateOf("Hacer una pregunta") }
    val options = listOf("Hacer una pregunta", "Informar de un error")
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }  // Usar remember para MutableState<Boolean>
    val dialogMessage = remember { mutableStateOf("") }  // Usar remember para MutableState<String>

    Scaffold(
        topBar = {
            BackTopBar(title = "Preguntas y Reportes", navController = navController)
        },
        snackbarHost = { SnackbarHost(hostState = SnackbarHostState()) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            DropdownMenuComponent(
                selectedOption = selectedOption,
                options = options,
                onOptionSelected = { selectedOption = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (selectedOption == "Hacer una pregunta") {
                FaqForm(title, onTitleChange = { title = it },
                    content, onContentChange = { content = it })
            } else {
                ErrorReportForm(title, onTitleChange = { title = it },
                    content, onContentChange = { content = it },
                    severity, onSeveritySelected = { severity = it })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (selectedOption == "Hacer una pregunta") {
                        viewModel.sendFaqQuestion(title, content)
                        snackbarMessage = "Pregunta enviada con éxito."
                        dialogMessage.value = "Tu pregunta ha sido enviada correctamente."
                    } else if (selectedOption == "Informar de un error") {
                        viewModel.reportBug(title, content, severity)
                        snackbarMessage = "Error informado con éxito."
                        dialogMessage.value = "Tu informe de error ha sido enviado correctamente."
                    }
                    showSnackbar = true
                    showDialog.value = true  // Usar .value para cambiar el estado
                    title = ""
                    content = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar", color = MaterialTheme.colorScheme.onSecondary)
            }
            CustomAlertDialog(showDialog = showDialog, dialogMessage = dialogMessage.value)
        }
    }
}



@Composable
fun CustomAlertDialog(showDialog: MutableState<Boolean>, dialogMessage: String) {
    if (showDialog.value) {
        // Determine if the system is in dark theme
        val isSystemInDarkTheme = isSystemInDarkTheme()

        // Set colors based on the theme
        val dialogBackgroundColor = if (isSystemInDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary

        val dialogTextColor = if (isSystemInDarkTheme) ColorTextDark else Color.White
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(
                    text = "Evento Añadido",
                    color = dialogTextColor
                )
            },
            text = {
                Text(
                    text = dialogMessage,
                    color = dialogTextColor
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Aceptar", color = dialogTextColor)
                }
            },
            containerColor = dialogBackgroundColor,
            tonalElevation = 8.dp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqForm(title: String, onTitleChange: (String) -> Unit,
            content: String, onContentChange: (String) -> Unit) {
    TextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text("Título") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = content,
        onValueChange = onContentChange,
        label = { Text("Contenido") },
        modifier = Modifier.fillMaxWidth().height(200.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorReportForm(title: String, onTitleChange: (String) -> Unit,
                    content: String, onContentChange: (String) -> Unit,
                    severity: SeverityLevel, onSeveritySelected: (SeverityLevel) -> Unit) {
    TextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text("Título del error") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = content,
        onValueChange = onContentChange,
        label = { Text("Descripción del error") },
        modifier = Modifier.fillMaxWidth().height(200.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    SeveritySelector(severity, onSeveritySelected)
}

@Composable
fun DropdownMenuComponent(
    selectedOption: String,  // Este parámetro debe existir en la definición
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedOption, style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Desplegar")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = { Text(option) }
                )
            }
        }
    }
}

@Composable
fun SeveritySelector(currentSeverity: SeverityLevel, onSeveritySelected: (SeverityLevel) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        SeverityLevel.values().forEach { level ->
            OutlinedButton(
                onClick = { onSeveritySelected(level) },
                colors = ButtonDefaults.buttonColors(containerColor = if (currentSeverity == level) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            ) {
                Text(level.name)
            }
        }
    }
}
