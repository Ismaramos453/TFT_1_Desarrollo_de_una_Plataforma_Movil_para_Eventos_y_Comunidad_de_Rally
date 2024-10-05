package com.example.tft.model

data class RallyEvent(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val image: String? = null,
    val images: List<String> = emptyList(), // Lista de imágenes de los tramos del rally
    val description: String = "",
    val level: String = "",
    val coordinates: Pair<Double, Double>? = null // Coordenadas para el mapa, si es necesario
)


data class SavedEvent(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val type: String = "",  // Aquí agregamos el tipo de evento (ERC, WRC, etc.)
    val description: String = "",
    val image: String = ""
) {
    // Constructor sin argumentos necesario para Firebase
    constructor() : this("", "", "", "", "", "", "", "")
}

