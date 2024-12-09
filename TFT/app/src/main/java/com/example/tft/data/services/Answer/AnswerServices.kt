package com.example.tft.data.services.Answer

import com.example.tft.model.foro.Answer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object AnswerServices {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    fun addAnswer(questionId: String, content: String, userId: String, callback: (Boolean) -> Unit) {
        val userDocRef = AnswerServices.firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            val userName = documentSnapshot.getString("name") ?: "Nombre Desconocido"

            val answer = Answer(
                userId = userId,
                userName = userName,
                content = content,
                timestamp = System.currentTimeMillis()
            )

            val questionRef = AnswerServices.firestore.collection("questions").document(questionId)
            questionRef.update("answers", FieldValue.arrayUnion(answer))
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    callback(false)
                }
        }
    }

}