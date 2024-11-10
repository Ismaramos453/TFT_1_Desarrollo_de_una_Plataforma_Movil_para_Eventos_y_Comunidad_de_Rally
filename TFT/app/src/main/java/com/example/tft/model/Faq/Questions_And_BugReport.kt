package com.example.tft.model.Faq

import java.util.Date


enum class SeverityLevel {
    GRAVE, MEDIUM, LIGHT
}

data class QuestionForAdmin(
    var id: String = "", // Agregar este campo
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Date,
    val type: String = "Question"
)

data class BugReport(
    var id: String = "", // Agregar este campo
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Date,
    val severity: SeverityLevel,
    val type: String = "Bug Report"
)

