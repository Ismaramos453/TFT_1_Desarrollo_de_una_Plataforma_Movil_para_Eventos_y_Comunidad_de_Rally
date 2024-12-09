package com.example.tft.ui.questionsDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Answer.AnswerServices
import com.example.tft.data.services.Question.QuestionServices
import com.example.tft.model.foro.Question
import com.example.tft.data.services.notification.NotificationService
import com.google.firebase.auth.FirebaseAuth

class QuestionsDetailViewModel : ViewModel() {
    private val firestoreService = QuestionServices
    private val _questionLiveData = MutableLiveData<Question>()
    val questionLiveData: LiveData<Question> = _questionLiveData

    fun getQuestionDetail(questionId: String): LiveData<Question> {
        loadQuestionDetail(questionId)
        return questionLiveData
    }

    private fun loadQuestionDetail(questionId: String) {
        QuestionServices.getQuestionById(questionId) { question ->
            _questionLiveData.postValue(question)
        }
    }

    fun updateQuestion(questionId: String, title: String, content: String, callback: (Boolean) -> Unit) {
        QuestionServices.updateQuestion(questionId, title, content) { success ->
            if (success) {
                loadQuestionDetail(questionId)
            }
            callback(success)
        }
    }

    fun addAnswer(questionId: String, content: String, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        AnswerServices.addAnswer(questionId, content, userId) { success ->
            if (success) {
                QuestionServices.getQuestionById(questionId) { question ->
                    question?.let {
                        val message = "Un usuario ha respondido a tu pregunta: \"${it.title}\""
                        NotificationService.addNotification(it.userId, message) { success ->
                            if (success) {
                                loadQuestionDetail(questionId)
                            }
                        }
                    }
                }
                loadQuestionDetail(questionId)
            }
            callback(success)
        }
    }

    fun deleteQuestion(questionId: String, callback: (Boolean) -> Unit) {
        QuestionServices.deleteQuestion(questionId, callback)
    }
}
