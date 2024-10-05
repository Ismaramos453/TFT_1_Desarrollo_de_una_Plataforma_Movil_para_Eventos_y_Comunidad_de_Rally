package com.example.tft.model.user

import com.example.tft.model.SavedEvent
data class Users(
    val id: String? = null,
    var userId: String = "",
    var name: String = "",
    var image: String = "",
    var favoritePilots: MutableList<Int> = mutableListOf(),  // Lista para almacenar los IDs de los pilotos favoritos
    val eventsSaved: List<SavedEvent> = emptyList()
) {
    constructor() : this(null, "", "", "", mutableListOf(), emptyList())
}



