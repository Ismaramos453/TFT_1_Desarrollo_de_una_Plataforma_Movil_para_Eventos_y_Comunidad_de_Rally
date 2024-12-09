package com.example.tft.model.foro

data class Question(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val answers: MutableList<Answer> = mutableListOf()
)

data class Answer(
    val userId: String = "",      // ID del usuario que responde
    val userName: String = "",    // Nombre del usuario que responde
    val content: String = "",     // Contenido de la respuesta
    val timestamp: Long = System.currentTimeMillis() // Timestamp de la respuesta
) {
    // Constructor sin argumentos
    constructor() : this("", "", "", System.currentTimeMillis())
}

data class Votation(
    var id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val title: String = "",
    val options: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    var votes: Map<String, Int> = emptyMap(),
    var userVote: String? = null
)


