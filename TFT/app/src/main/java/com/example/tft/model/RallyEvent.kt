package com.example.tft.model

data class RallyEvent(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val image: Any? = null, // Cambiamos a Any para aceptar String o List
    val images: List<String> = emptyList(), // Lista de imágenes de los tramos del rally
    val description: String = "",
    val level: String = "",
    val coordinates: Pair<Double, Double>? = null // Coordenadas para el mapa, si es necesario
) {
    fun getImageAsString(): String? {
        // Si el campo 'image' es un String, devuélvelo directamente, si es una lista, toma el primer elemento.
        return when (image) {
            is String -> image
            is List<*> -> (image as? List<String>)?.firstOrNull() // Toma la primera imagen si es una lista
            else -> null
        }
    }
}



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

