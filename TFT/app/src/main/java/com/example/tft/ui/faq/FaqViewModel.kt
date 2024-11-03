package com.example.tft.ui.faq



import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Authentication.AuthenticationServices
import com.example.tft.data.services.Faq.FaqServices
import com.example.tft.model.Faq.SeverityLevel
import com.google.firebase.auth.FirebaseAuth

class FaqViewModel : ViewModel() {
    fun sendFaqQuestion(title: String, content: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Using the currentUser directly to get the user ID and email
            val userId = currentUser.uid
            val userEmail = currentUser.email ?: "No Email Provided"
            val userName = currentUser.displayName ?: "Anonymous"

            FaqServices.postFaqQuestion(title, content, userId, userName, userEmail)
        } else {
            Log.e("FaqViewModel", "No user logged in.")
        }
    }

    fun reportBug(title: String, content: String, severity: SeverityLevel) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Again, extracting user details directly from the FirebaseUser
            val userId = currentUser.uid
            val userEmail = currentUser.email ?: "No Email Provided"
            val userName = currentUser.displayName ?: "Anonymous"

            FaqServices.postBugReport(title, content, userId, userName, userEmail, severity)
        } else {
            Log.e("FaqViewModel", "No user logged in.")
        }
    }
}

