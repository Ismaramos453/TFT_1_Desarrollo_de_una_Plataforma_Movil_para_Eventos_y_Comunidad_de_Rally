package com.example.tft.ui.community

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.tft.model.foro.Question
import com.example.tft.templates_App.BackTopBar
import com.example.tft.templates_App.DefaultTopBar
import com.google.accompanist.pager.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import androidx.compose.material3.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.example.tft.model.foro.Votation
import com.example.tft.navigation.AppScreens

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavHostController) {
    val viewModel: CommunityViewModel = viewModel()
    val items by viewModel.items.observeAsState(initial = emptyList())
    var searchText by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Todas") }
    var commentsSortOption by remember { mutableStateOf("Todas") }
    val filterOption = remember { mutableStateOf("Todas") }
    val filterExpanded = remember { mutableStateOf(false) }

    LaunchedEffect(sortOption, commentsSortOption) {
        // Trigger recomposition when sort options change
    }

    val filteredItems = items.filter { item ->
        when (filterOption.value) {
            "Todas" -> true
            "Preguntas" -> item is Question
            "Votaciones" -> item is Votation
            else -> true
        } && itemMatchesSearchQuery(item, searchText)
    }.sortedWith { a, b ->
        // First sort by comments if commentsSortOption is active
        var commentResult = when (commentsSortOption) {
            "Más comentarios" -> (b as? Question)?.answers?.size?.compareTo((a as? Question)?.answers?.size ?: 0) ?: 0
            "Menos comentarios" -> (a as? Question)?.answers?.size?.compareTo((b as? Question)?.answers?.size ?: 0) ?: 0
            else -> 0
        }

        if (commentResult != 0) return@sortedWith commentResult

        // Then sort by date if necessary
        when (sortOption) {
            "Recientes" -> (b as? Question)?.timestamp?.compareTo((a as? Question)?.timestamp ?: Long.MAX_VALUE) ?: 0
            "Antiguas" -> (a as? Question)?.timestamp?.compareTo((b as? Question)?.timestamp ?: Long.MAX_VALUE) ?: 0
            else -> 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comunidad", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Group, contentDescription = "Back to previous screen", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { filterExpanded.value = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter options", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = filterExpanded.value,
                        onDismissRequest = { filterExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                filterOption.value = "Todas"
                                filterExpanded.value = false
                            },
                            text = { Text("Todas") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                filterOption.value = "Preguntas"
                                filterExpanded.value = false
                            },
                            text = { Text("Preguntas") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                filterOption.value = "Votaciones"
                                filterExpanded.value = false
                            },
                            text = { Text("Votaciones") }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.CreateQuestionScreen.route) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear Pregunta")
            }
        }

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar en la comunidad...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SortDropdownMenu("Recientes", listOf("Todas", "Recientes", "Antiguas"), sortOption) { selected ->
                        sortOption = selected
                    }
                    SortCommentsDropdownMenu(commentsSortOption) { selected ->
                        commentsSortOption = selected
                    }
                }
            }

            items(filteredItems) { item ->
                when (item) {
                    is Question -> QuestionCard(item, navController)
                    is Votation -> VotationCard(item, viewModel::voteOnVotation, navController)
                }
            }
        }
    }
}

@Composable
fun SortDropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelectOption: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .padding(4.dp)  // Adjusted padding for a more compact look
                .width(150.dp),  // Set a fixed width to make the button smaller
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,  // Usar color secundario para el botón
            ),
            shape = RoundedCornerShape(50)
        ) {
            // Update the text based on the selected option
            val displayText = when (selectedOption) {
                "Recientes" -> "Recientes"
                "Antiguas" -> "Menos recientes"
                "Todas" -> "Todas"
                else -> selectedOption  // Default to showing the selected option directly
            }
            Text(
                text = "$label: $displayText",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(150.dp)  // Match the width of the button
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSelectOption(option)
                        expanded = false
                    },
                    text = { Text(option, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }
    }
}

@Composable
fun SortCommentsDropdownMenu(
    currentSelection: String,
    updateSelection: (String) -> Unit
) {
    val options = listOf("Todas", "Más comentarios", "Menos comentarios")
    var expanded by remember { mutableStateOf(false) }
    var buttonSize by remember { mutableStateOf(IntSize.Zero) }

    Column {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .padding(4.dp)
                .width(150.dp)
                .onGloballyPositioned { coordinates ->
                    // Capture the button size
                    buttonSize = coordinates.size
                },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(50)
        ) {
            val displayText = when (currentSelection) {
                "Más comentarios" -> "Más comentarios"
                "Menos comentarios" -> "Menos comentarios"
                "Todas" -> "Todas"
                else -> currentSelection
            }
            Text(
                text = "Comentarios: $displayText",
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(
                x = 0.dp,
                y = 0.dp // Set y offset to 0 to remove any gap between the button and the dropdown
            ),
            modifier = Modifier.width(150.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        updateSelection(option)
                        expanded = false
                    },
                    text = { Text(option) }
                )
            }
        }
    }
}






private fun itemMatchesSearchQuery(item: Any, query: String): Boolean {
    return when (item) {
        is Question -> item.title.contains(query, ignoreCase = true) || item.content.contains(query, ignoreCase = true)
        is Votation -> item.title.contains(query, ignoreCase = true)
        else -> false
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuestionCard(question: Question, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(
                    AppScreens.QuestionsDetailScreen.route.replace("{questionId}", question.id))

            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = rememberImagePainter(question.userImage),
                contentDescription = "Imagen del Usuario",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pregunta por: ${question.userName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = question.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = question.content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "${question.answers.size} respuestas",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Fecha: ${Instant.ofEpochMilli(question.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}



@Composable
fun VotationCard(votation: Votation, onVote: (String, String) -> Unit, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(
                    AppScreens.VotationDetailScreen.route.replace("{votationId}", votation.id)
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
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

                // Barra de progreso para el porcentaje de votos
                LinearProgressIndicator(
                    progress = percentage / 100,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)  // Aumentar la altura de la barra
                        .clip(RoundedCornerShape(8.dp))  // Esquinas redondeadas para un estilo más moderno
                        .padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.primary  // Color de la barra de progreso
                )
                Text(
                    text = "${"%.1f".format(percentage)}% ($votesForOption votos)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}




