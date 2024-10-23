package com.example.tft.data.services.News

import com.example.tft.model.News
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object NewsServices {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storageReference = FirebaseStorage.getInstance().reference
    fun getNews(callback: (List<News>) -> Unit) {
        NewsServices.firestore.collection("news")
            .get()
            .addOnSuccessListener { result ->
                val newsList = result.documents.mapNotNull { doc ->
                    doc.toObject(News::class.java)?.copy(id = doc.id)
                }
                callback(newsList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}
