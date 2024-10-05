package com.example.tft.ui.questionsDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.FirestoreService
import com.example.tft.model.foro.Question
import com.google.firebase.auth.FirebaseAuth

class QuestionsDetailViewModel : ViewModel() {
    private val firestoreService = FirestoreService

    // MutableLiveData que se expone como LiveData
    private val _questionLiveData = MutableLiveData<Question>()
    val questionLiveData: LiveData<Question> = _questionLiveData

    fun getQuestionDetail(questionId: String): LiveData<Question> {
        loadQuestionDetail(questionId)
        return questionLiveData
    }

    private fun loadQuestionDetail(questionId: String) {
        firestoreService.getQuestionById(questionId) { question ->
            _questionLiveData.postValue(question)
        }
    }

    // Asegúrate de llamar a loadQuestionDetail dentro de las funciones que modifican los datos
    fun updateQuestion(questionId: String, title: String, content: String, callback: (Boolean) -> Unit) {
        firestoreService.updateQuestion(questionId, title, content) { success ->
            if (success) {
                loadQuestionDetail(questionId)
            }
            callback(success)
        }
    }

    fun addAnswer(questionId: String, content: String, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestoreService.addAnswer(questionId, content, userId) { success ->
            if (success) {
                loadQuestionDetail(questionId)
            }
            callback(success)
        }
    }

    fun deleteQuestion(questionId: String, callback: (Boolean) -> Unit) {
        firestoreService.deleteQuestion(questionId, callback)
    }
}
