package com.example.tft.data.services.Votation

import android.util.Log
import com.example.tft.model.foro.Votation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object VotationServices {

    private val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    fun addVotation(title: String, userId: String, options: List<String>, callback: (Boolean, String?) -> Unit) {
        val userDocRef = VotationServices.firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            val userName = documentSnapshot.getString("name") ?: "Nombre Desconocido"
            val userImage = documentSnapshot.getString("image") ?: ""
            val votation = Votation(
                userId = userId,
                userName = userName,
                userImage = userImage,
                title = title,
                options = options,
                timestamp = System.currentTimeMillis(),
                votes = options.associateWith { 0 }
            )
            val docRef = VotationServices.firestore.collection("votations").document()
            votation.id = docRef.id
            docRef.set(votation)
                .addOnSuccessListener {
                    callback(true, docRef.id)
                }
                .addOnFailureListener { e ->
                    callback(false, null)
                }
        }
    }

    fun voteOnVotation(votationId: String, option: String, userId: String, callback: (Boolean) -> Unit) {
        val votationRef = VotationServices.firestore.collection("votations").document(votationId)
        VotationServices.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(votationRef)
            val existingVote = snapshot.getString("userVote")
            val votes = (snapshot.get("votes") as? Map<String, Number> ?: emptyMap<String, Number>()).toMutableMap()

            // Decrementar el voto anterior, si existe
            if (!existingVote.isNullOrBlank() && votes[existingVote] != null) {
                val currentVotes = votes[existingVote]!!.toInt()
                votes[existingVote] = (currentVotes - 1).coerceAtLeast(0)
            }

            // Incrementar el nuevo voto
            val newVotes = votes[option]?.toInt() ?: 0
            votes[option] = newVotes + 1

            // Actualizar el voto del usuario y el mapa de votos
            transaction.update(votationRef, mapOf(
                "userVote" to option,
                "votes" to votes.mapValues { it.value.toInt() }
            ))
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener { e ->
            Log.e("FirestoreService", "Error updating votes: ${e.localizedMessage}", e)
            callback(false)
        }
    }
}