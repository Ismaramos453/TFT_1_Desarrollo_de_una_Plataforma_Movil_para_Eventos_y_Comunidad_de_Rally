package com.example.tft.ui.createQuestion

import androidx.lifecycle.ViewModel
import com.example.tft.data.FirestoreService
import com.example.tft.model.foro.Question

class CreateQuestionViewModel : ViewModel() {
    private val firestoreService = FirestoreService

    fun addQuestion(title: String, content: String, userId: String, callback: (Boolean) -> Unit) {
        firestoreService.addQuestion(title, content, userId, callback)
    }

    // Updated signature for addVotation
    fun addVotation(title: String, userId: String, options: List<String>, callback: (Boolean, String?) -> Unit) {
        firestoreService.addVotation(title, userId, options, callback)
    }
}



