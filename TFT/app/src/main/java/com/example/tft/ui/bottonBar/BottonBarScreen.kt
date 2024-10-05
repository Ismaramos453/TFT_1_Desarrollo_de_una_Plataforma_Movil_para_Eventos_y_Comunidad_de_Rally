package com.example.tft.ui.bottonBar

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.tft.model.ItemsNavBar
import com.example.tft.model.ItemsNavBar.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tft.ui.theme.PrimaryColor
import com.example.tft.ui.theme.SecondaryColor
import com.example.tft.ui.theme.TertiaryColor


@Composable
fun BottonBarScreen(navHostController: NavHostController) {
    val menuItems = listOf(
        ItemsNavBar.Item_NavBar1,
        ItemsNavBar.Item_NavBar2,
        ItemsNavBar.Item_NavBar3,
        ItemsNavBar.Item_NavBar4,
        ItemsNavBar.Item_NavBar5
    )

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val orangeColor = Color(0xFFFFA500) // Definir el color naranja

    BottomAppBar(
        containerColor = PrimaryColor // Color primario
    ) {
        NavigationBar(
            containerColor = PrimaryColor // Asegúrate de que el NavigationBar tenga el mismo color de fondo
        ) {
            menuItems.forEach { item ->
                val isSelected = currentRoute == item.ruta
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        navHostController.navigate(item.ruta) {
                            // Evita múltiples copias de la misma ruta en la pila
                            launchSingleTop = true
                            // Evita volver a la misma instancia de la pantalla
                            restoreState = true
                            // Limpiar el back stack para evitar volver a la pantalla de login
                            popUpTo(navHostController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.iconBar,
                            contentDescription = item.title,
                            tint = if (isSelected) orangeColor else TertiaryColor.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text(
                            item.title,
                            color = if (isSelected) orangeColor else TertiaryColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = orangeColor,
                        unselectedIconColor = TertiaryColor.copy(alpha = 0.6f),
                        selectedTextColor = orangeColor,
                        unselectedTextColor = TertiaryColor.copy(alpha = 0.6f),
                        indicatorColor = PrimaryColor // Hacer que el indicador tenga el mismo color que el fondo
                    )
                )
            }
        }
    }
}
