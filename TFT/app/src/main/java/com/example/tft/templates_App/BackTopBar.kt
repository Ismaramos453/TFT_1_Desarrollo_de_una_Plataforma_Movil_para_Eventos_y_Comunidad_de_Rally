package com.example.tft.templates_App

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, navController: NavController){
    val colors = MaterialTheme.colorScheme
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(start = 20.dp),
                color = Color.White
            )
        },
        navigationIcon = {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "backIcon",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(start = 10.dp),
                tint = Color.White
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colors.primary)
        )
}