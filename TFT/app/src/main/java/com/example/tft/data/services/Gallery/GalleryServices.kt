package com.example.tft.data.services.Gallery

import android.util.Log
import com.example.tft.model.GalleryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object GalleryServices{

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    fun getGalleryItems(callback: (List<GalleryItem>) -> Unit) {
        GalleryServices.firestore.collection("gallery")
            .get()
            .addOnSuccessListener { result ->
                val galleryItems = result.documents.mapNotNull { doc ->
                    val galleryItem = doc.toObject(GalleryItem::class.java)
                    galleryItem?.copy(id = doc.id)
                }
                callback(galleryItems)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error getting gallery items", exception)
                callback(emptyList())
            }
    }
}