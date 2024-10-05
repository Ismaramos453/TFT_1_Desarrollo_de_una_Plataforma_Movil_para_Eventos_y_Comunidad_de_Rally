package com.example.tft.templates_App

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tft.R
import com.example.tft.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(navController: NavController, searchQuery: MutableState<String>) {
    val colors = MaterialTheme.colorScheme
    var isSearchExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = Modifier.height(68.dp),
        title = {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!isSearchExpanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .clickable { navController.navigate(AppScreens.HomeScreen.route) }
                                .padding(start = 16.dp),
                            tint = Color.White
                        )
                        Text(
                            text = "RallyWorld",
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 20.dp),
                            textAlign = TextAlign.Start
                        )
                        IconButton(onClick = { isSearchExpanded = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                        }
                    }
                } else {
                    TextField(
                        value = searchQuery.value,
                        onValueChange = { newValue -> searchQuery.value = newValue },
                        placeholder = { Text("Buscar eventos...") },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = Color.White,
                            textColor = Color.White,
                            placeholderColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        trailingIcon = {
                            IconButton(onClick = { isSearchExpanded = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar búsqueda", tint = Color.White)
                            }
                        },
                        keyboardActions = KeyboardActions(onSearch = {
                            isSearchExpanded = false
                            // Aquí se puede añadir una acción de búsqueda
                        })
                    )
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colors.primary)
    )
}
