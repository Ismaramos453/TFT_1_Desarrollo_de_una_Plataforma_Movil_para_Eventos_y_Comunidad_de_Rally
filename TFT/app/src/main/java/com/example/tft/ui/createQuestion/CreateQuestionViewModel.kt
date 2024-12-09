package com.example.tft.ui.createQuestion

import androidx.lifecycle.ViewModel

import com.example.tft.data.services.Question.QuestionServices
import com.example.tft.data.services.Votation.VotationServices

class CreateQuestionViewModel : ViewModel() {
    private val firestoreService = QuestionServices

    fun addQuestion(title: String, content: String, userId: String, callback: (Boolean) -> Unit) {
        QuestionServices.addQuestion(title, content, userId, callback)
    }

    fun addVotation(title: String, userId: String, options: List<String>, callback: (Boolean, String?) -> Unit) {
        VotationServices.addVotation(title, userId, options, callback)
    }
}



