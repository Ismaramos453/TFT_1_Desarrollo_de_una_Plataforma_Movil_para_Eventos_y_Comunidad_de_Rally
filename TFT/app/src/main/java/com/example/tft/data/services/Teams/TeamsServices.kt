package com.example.tft.data.services.Teams


import com.example.tft.model.wrc.TeamWrc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object TeamsServices {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    fun getTeams(callback: (List<TeamWrc>) -> Unit) {
        TeamsServices.firestore.collection("wrc_teams")
            .get()
            .addOnSuccessListener { result ->
                val teams = result.documents.mapNotNull { doc ->
                    val team = doc.toObject(TeamWrc::class.java)
                    team?.copy(pilots = doc.get("pilots") as List<String>? ?: listOf())
                }
                callback(teams)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}