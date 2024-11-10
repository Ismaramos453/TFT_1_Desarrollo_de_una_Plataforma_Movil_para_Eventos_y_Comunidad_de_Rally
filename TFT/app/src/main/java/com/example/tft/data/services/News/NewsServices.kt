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
        firestore.collection("news")
            .get()
            .addOnSuccessListener { result ->
                val newsList = result.documents.mapNotNull { doc ->
                    // Extrae los valores manualmente y convierte el campo "publicacion" de Timestamp a String
                    val id = doc.id
                    val title = doc.getString("title") ?: ""
                    val author = doc.getString("author") ?: ""
                    val description = doc.getString("description") ?: ""
                    val image = doc.getString("image") ?: ""
                    val type = doc.getString("type") ?: "news"

                    // Convierte el Timestamp en una fecha formateada
                    val timestamp = doc.getTimestamp("publicacion")
                    val formattedDate = timestamp?.toDate()?.toString() ?: ""  // Convierte a String; ajusta el formato si es necesario

                    // Crea el objeto News manualmente
                    News(
                        id = id,
                        title = title,
                        author = author,
                        description = description,
                        image = image,
                        publicacion = formattedDate,
                        type = type
                    )
                }
                callback(newsList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}

