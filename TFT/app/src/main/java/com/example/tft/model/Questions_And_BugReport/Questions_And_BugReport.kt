package com.example.tft.model.Questions_And_BugReport

import java.util.Date


enum class SeverityLevel {
    GRAVE, MEDIUM, LIGHT
}

data class QuestionForAdmin(
    var id: String = "",
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Date,
    val type: String = "Question"
)

data class BugReport(
    var id: String = "",
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Date,
    val severity: SeverityLevel,
    val type: String = "Bug Report"
)

