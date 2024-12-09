package com.example.tft.ui.Question_And_Report

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Questions_And_BugReport.QuestionForAdminServices
import com.example.tft.model.Questions_And_BugReport.SeverityLevel
import com.google.firebase.auth.FirebaseAuth

class Question_And_ReportViewModel : ViewModel() {
    fun sendFaqQuestion(title: String, content: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userEmail = currentUser.email ?: "No Email Provided"
            val userName = currentUser.displayName ?: "Anonymous"

            QuestionForAdminServices.postQuestion(title, content, userId, userName, userEmail)
        } else {
            Log.e("FaqViewModel", "No user logged in.")
        }
    }

    fun reportBug(title: String, content: String, severity: SeverityLevel) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userEmail = currentUser.email ?: "No Email Provided"
            val userName = currentUser.displayName ?: "Anonymous"

            QuestionForAdminServices.postBugReport(title, content, userId, userName, userEmail, severity)
        } else {
            Log.e("FaqViewModel", "No user logged in.")
        }
    }
}

