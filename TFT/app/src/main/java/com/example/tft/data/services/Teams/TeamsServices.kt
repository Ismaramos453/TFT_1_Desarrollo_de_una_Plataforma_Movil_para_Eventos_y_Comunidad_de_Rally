package com.example.tft.data.services.Teams


import com.example.tft.model.wrc.TeamWrc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object TeamsServices {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storageReference = FirebaseStorage.getInstance().reference
    fun getTeams(callback: (List<TeamWrc>) -> Unit) {
        TeamsServices.firestore.collection("wrc_teams")
            .get()
            .addOnSuccessListener { result ->
                val teams = result.documents.mapNotNull { doc ->
                    val team = doc.toObject(TeamWrc::class.java)
                    // Asegurarse de que la conversión de pilotos como array de strings se maneja bien
                    team?.copy(pilots = doc.get("pilots") as List<String>? ?: listOf())
                }
                callback(teams)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}