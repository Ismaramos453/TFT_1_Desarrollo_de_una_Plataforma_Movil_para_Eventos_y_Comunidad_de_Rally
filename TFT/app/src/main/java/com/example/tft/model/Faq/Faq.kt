package com.example.tft.model.Faq

import java.util.Date


enum class SeverityLevel {
    GRAVE, MEDIUM, LIGHT
}

data class FaqQuestion(
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Date
)

data class BugReport(
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Date,
    val severity: SeverityLevel
)
