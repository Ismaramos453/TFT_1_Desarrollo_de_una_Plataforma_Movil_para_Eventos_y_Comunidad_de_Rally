package com.example.tft.model.wrc

data class TeamWrc(
    val image: String = "",
    val teamName: String = "",
    val car: String = "",
    val country: String = "",
    val pilots: List<String> = listOf()  // Ahora pilots es una lista de strings.
)


data class Pilot(
    val name: String,
    val number: Int
)
