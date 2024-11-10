package com.example.tft.data.services.PilotFavorites

import android.net.Uri
import android.util.Log

import com.example.tft.model.Car
import com.example.tft.model.CarCategory
import com.example.tft.model.GalleryItem
import com.example.tft.model.News
import com.example.tft.model.RallyEvent
import com.example.tft.model.foro.Answer
import com.example.tft.model.foro.Question
import com.example.tft.model.foro.Votation
import com.example.tft.model.user.Users
import com.example.tft.model.wrc.TeamWrc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await


object PilotFavoritesServices {

    private val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    // Add a pilot to favorites
    suspend fun addPilotToFavorites(userId: String, pilotId: Int) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("favoritePilots", FieldValue.arrayUnion(pilotId)).await()
    }

    // Remove a pilot from favorites
    suspend fun removePilotFromFavorites(userId: String, pilotId: Int) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("favoritePilots", FieldValue.arrayRemove(pilotId)).await()
    }

    // Get the list of favorite pilots
    suspend fun getFavoritePilots(userId: String): List<Int> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            val user = document.toObject(Users::class.java)
            user?.favoritePilots ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
