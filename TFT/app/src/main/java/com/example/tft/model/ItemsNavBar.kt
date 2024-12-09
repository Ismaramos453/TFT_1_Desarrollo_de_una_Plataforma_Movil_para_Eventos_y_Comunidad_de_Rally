package com.example.tft.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.AirlineSeatReclineNormal
import androidx.compose.material.icons.outlined.BakeryDining
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Dehaze
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.tft.navigation.AppScreens

sealed class ItemsNavBar(
    val iconBar: ImageVector,
    val title: String,
    val ruta: String
){
    object Item_NavBar1: ItemsNavBar(
        Icons.Outlined.Home,
        "Inicio",
        AppScreens.HomeScreen.route
    )
    object Item_NavBar2: ItemsNavBar(
        Icons.Outlined.AddAlert,
        "Alertas",
        AppScreens.NotificationScreen.route
    )
    object Item_NavBar3: ItemsNavBar(
        Icons.Outlined.BakeryDining,
        "WRC",
        AppScreens.PadockScreen.route
    )
    object Item_NavBar4: ItemsNavBar(
        Icons.Outlined.Celebration,
        "FanZone",
        AppScreens.CommunityScreen.route
    )
    object Item_NavBar5: ItemsNavBar(
        Icons.Outlined.Dehaze,
        "Otros",
        AppScreens.OtherScreen.route
    )
}
