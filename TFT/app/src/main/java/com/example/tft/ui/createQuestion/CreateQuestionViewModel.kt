package com.example.tft.ui.createQuestion

import androidx.lifecycle.ViewModel

import com.example.tft.data.services.Question.QuestionServices
import com.example.tft.data.services.Votation.VotationServices
import com.example.tft.model.foro.Question

class CreateQuestionViewModel : ViewModel() {
    private val firestoreService = QuestionServices

    fun addQuestion(title: String, content: String, userId: String, callback: (Boolean) -> Unit) {
        QuestionServices.addQuestion(title, content, userId, callback)
    }

    // Updated signature for addVotation
    fun addVotation(title: String, userId: String, options: List<String>, callback: (Boolean, String?) -> Unit) {
        VotationServices.addVotation(title, userId, options, callback)
    }
}



