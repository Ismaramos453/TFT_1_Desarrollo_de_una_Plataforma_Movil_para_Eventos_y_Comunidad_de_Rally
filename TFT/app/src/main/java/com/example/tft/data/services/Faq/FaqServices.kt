package com.example.tft.data.services.Faq

import java.util.*

import com.example.tft.model.Faq.BugReport
import com.example.tft.model.Faq.FaqQuestion
import com.example.tft.model.Faq.SeverityLevel
import com.google.firebase.firestore.FirebaseFirestore

object FaqServices {
    private val db = FirebaseFirestore.getInstance()

    fun postFaqQuestion(title: String, content: String, userId: String, userName: String, userEmail: String) {
        val question = FaqQuestion(
            title = title,
            content = content,
            authorId = userId,
            authorName = userName,
            authorEmail = userEmail,
            timestamp = Date()
        )
        db.collection("faqs").add(question)
    }

    fun postBugReport(title: String, content: String, userId: String, userName: String, userEmail: String, severity: SeverityLevel) {
        val report = BugReport(
            title = title,
            content = content,
            authorId = userId,
            authorName = userName,
            authorEmail = userEmail,
            timestamp = Date(),
            severity = severity
        )
        db.collection("bugReports").add(report)
    }
}
