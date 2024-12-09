package com.example.tft.ui.votationDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.model.foro.Votation
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore


class VotationDetailViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _votationLiveData = MutableLiveData<Votation>()
    val votationLiveData: LiveData<Votation> = _votationLiveData

    // Obtiene los detalles de la votación y escucha por cambios
    fun getVotationDetail(votationId: String): LiveData<Votation> {
        loadVotationDetail(votationId)
        return votationLiveData
    }

    // Carga los detalles de la votación desde Firestore
    private fun loadVotationDetail(votationId: String) {
        firestore.collection("votations").document(votationId).get()
            .addOnSuccessListener { document ->
                val votation = document.toObject(Votation::class.java)
                _votationLiveData.postValue(votation)
            }
            .addOnFailureListener {
                _votationLiveData.postValue(null)
            }
    }

    // Función para votar en una votación
    fun voteOnVotation(votationId: String, option: String, callback: (Boolean) -> Unit) {
        val votationRef = firestore.collection("votations").document(votationId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(votationRef)
            val existingVote = snapshot.getString("userVote")
            val votes = (snapshot.get("votes") as? Map<String, Number> ?: emptyMap<String, Number>()).toMutableMap()

            // Decrementar el voto anterior, si existe
            if (!existingVote.isNullOrBlank() && votes[existingVote] != null) {
                val currentVotes = votes[existingVote]!!.toInt()  // Asegura la conversión a Int
                votes[existingVote] = (currentVotes - 1).coerceAtLeast(0)
            }

            // Incrementar el nuevo voto
            val newVotes = votes[option]?.toInt() ?: 0  // Maneja null y asegura Int
            votes[option] = newVotes + 1

            // Actualizar el voto del usuario y el mapa de votos
            transaction.update(votationRef, mapOf(
                "userVote" to option,
                "votes" to votes.mapValues { it.value.toInt() }
            ))
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener { e ->
            Log.e("VotationDetailViewModel", "Error updating votes: ${e.localizedMessage}", e)
            callback(false)
        }
    }

    // Actualiza la votación en Firestore y recarga los datos
    fun updateVotation(votationId: String, updatedVotation: Votation, callback: (Boolean) -> Unit) {
        val votationRef = firestore.collection("votations").document(votationId)
        val updatedData = mapOf(
            "title" to updatedVotation.title,
            "options" to updatedVotation.options
        )
        votationRef.update(updatedData)
            .addOnSuccessListener {
                loadVotationDetail(votationId)
                callback(true)
            }
            .addOnFailureListener { callback(false) }
    }

    // Elimina la votación en Firestore
    fun deleteVotation(votationId: String, callback: (Boolean) -> Unit) {
        firestore.collection("votations").document(votationId).delete()
            .addOnSuccessListener {
                _votationLiveData.postValue(null)
                callback(true)
            }
            .addOnFailureListener { callback(false) }
    }

    // Función para verificar si el usuario actual es el dueño de la votación
    fun isUserOwner(votation: Votation): Boolean {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        return votation.userId == currentUserId
    }
}

