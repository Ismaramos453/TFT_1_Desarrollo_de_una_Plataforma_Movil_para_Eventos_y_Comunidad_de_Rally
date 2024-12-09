package com.example.tft.data.services.PilotFavorites


import com.example.tft.model.user.Users
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


object PilotFavoritesServices {

    val firestore = FirebaseFirestore.getInstance()

    suspend fun addPilotToFavorites(userId: String, pilotId: Int) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("favoritePilots", FieldValue.arrayUnion(pilotId)).await()
    }

    suspend fun removePilotFromFavorites(userId: String, pilotId: Int) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("favoritePilots", FieldValue.arrayRemove(pilotId)).await()
    }

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
