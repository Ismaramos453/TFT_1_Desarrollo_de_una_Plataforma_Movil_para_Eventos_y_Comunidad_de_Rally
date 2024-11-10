package com.example.tft.data.services.Questions_And_BugReport

import java.util.*

import com.example.tft.model.Questions_And_BugReport.BugReport
import com.example.tft.model.Questions_And_BugReport.QuestionForAdmin
import com.example.tft.model.Questions_And_BugReport.SeverityLevel
import com.google.firebase.firestore.FirebaseFirestore

object QuestionForAdminServices {
    private val db = FirebaseFirestore.getInstance()

    fun postQuestion(title: String, content: String, userId: String, userName: String, userEmail: String) {
        val question = QuestionForAdmin(
            title = title,
            content = content,
            authorId = userId,
            authorName = userName,
            authorEmail = userEmail,
            timestamp = Date(),
            type = "Question"
        )
        // Crear el documento y luego actualizarlo con su propio ID
        val documentReference = db.collection("questionsAdmin").document()
        question.id = documentReference.id // Asignar el ID antes de guardarlo
        documentReference.set(question)
    }

    fun postBugReport(title: String, content: String, userId: String, userName: String, userEmail: String, severity: SeverityLevel) {
        val report = BugReport(
            title = title,
            content = content,
            authorId = userId,
            authorName = userName,
            authorEmail = userEmail,
            timestamp = Date(),
            severity = severity,
            type = "Bug Report"
        )
        // Crear el documento y luego actualizarlo con su propio ID
        val documentReference = db.collection("bugReports").document()
        report.id = documentReference.id // Asignar el ID antes de guardarlo
        documentReference.set(report)
    }
}
