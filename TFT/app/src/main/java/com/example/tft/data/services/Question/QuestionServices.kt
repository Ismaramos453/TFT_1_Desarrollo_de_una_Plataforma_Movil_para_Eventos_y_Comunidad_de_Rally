package com.example.tft.data.services.Question

import android.util.Log
import com.example.tft.model.foro.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object QuestionServices {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    fun addQuestion(title: String, content: String, userId: String, callback: (Boolean) -> Unit) {
        val userDocRef = QuestionServices.firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            val userName = documentSnapshot.getString("name") ?: "Nombre Desconocido"
            val userImage = documentSnapshot.getString("image") ?: ""

            val docRef = QuestionServices.firestore.collection("questions").document()
            val questionId = docRef.id

            val question = Question(
                id = questionId,
                userId = userId,
                userName = userName,
                userImage = userImage,
                title = title,
                content = content,
                timestamp = System.currentTimeMillis() 
            )

            docRef.set(question)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    callback(false)
                }
        }
    }

    fun updateQuestion(questionId: String, title: String, content: String, callback: (Boolean) -> Unit) {
        val questionRef = QuestionServices.firestore.collection("questions").document(questionId)
        questionRef.update(mapOf(
            "title" to title,
            "content" to content
        ))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun deleteQuestion(questionId: String, callback: (Boolean) -> Unit) {
        QuestionServices.firestore.collection("questions").document(questionId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getQuestionById(questionId: String, callback: (Question?) -> Unit) {
        val questionRef = QuestionServices.firestore.collection("questions").document(questionId)
        questionRef.get().addOnSuccessListener { documentSnapshot ->
            val question = documentSnapshot.toObject(Question::class.java)
            callback(question)
        }.addOnFailureListener { e ->
            Log.e("FirestoreService", "Error getting question: ${e.localizedMessage}", e)
            callback(null)
        }
    }
    fun getQuestions(callback: (List<Question>) -> Unit) {
        QuestionServices.firestore.collection("questions")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Ordenar por timestamp descendente
            .get()
            .addOnSuccessListener { result ->
                val questions = result.documents.mapNotNull { it.toObject(Question::class.java) }
                callback(questions)
            }
            .addOnFailureListener {
                Log.e("FirestoreService", "Error getting questions", it)
                callback(emptyList())
            }
    }
}