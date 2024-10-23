package com.example.tft.data.services.Event

import android.content.Context
import com.example.tft.model.RallyEvent
import com.example.tft.model.SavedEvent
import com.example.tft.model.pilot.Team
import com.example.tft.model.user.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object EventServices {
    val firestore = FirebaseFirestore.getInstance()
    suspend fun saveEvent(context: Context, userId: String, event: RallyEvent): Boolean {
        val userRef = EventServices.firestore.collection("users").document(userId)
        val userSnapshot = userRef.get().await()

        if (userSnapshot.exists()) {
            val user = userSnapshot.toObject(Users::class.java)
            user?.let {
                val updatedEvents = it.eventsSaved.toMutableList()
                val savedEvent = SavedEvent(
                    id = event.id,
                    title = event.title,
                    date = event.date,
                    time = event.time,
                    location = event.location,
                    type = event.type,
                    description = event.description,
                    image = event.getImageAsString() ?: "" // Usamos getImageAsString() para asegurar que sea un String
                )

                updatedEvents.add(savedEvent)
                userRef.update("eventsSaved", updatedEvents).await()
                return true
            }
        }
        return false
    }


    suspend fun loadEvents(userId: String): List<SavedEvent> {
        val userSnapshot = EventServices.firestore.collection("users").document(userId).get().await()
        if (userSnapshot.exists()) {
            val user = userSnapshot.toObject(Users::class.java)
            return user?.eventsSaved ?: emptyList()
        }
        return emptyList()
    }

    suspend fun deleteEvent(userId: String, eventId: String) {
        val userRef = EventServices.firestore.collection("users").document(userId)
        val userSnapshot = userRef.get().await()

        if (userSnapshot.exists()) {
            val user = userSnapshot.toObject(Users::class.java)
            user?.let {
                val updatedEvents = it.eventsSaved.filterNot { savedEvent -> savedEvent.id == eventId }
                userRef.update("eventsSaved", updatedEvents).await()
            }
        }
    }

    fun loadEvents(callback: (List<RallyEvent>) -> Unit) {
        EventServices.firestore.collection("events")
            .get()
            .addOnSuccessListener { result ->
                val events = result.documents.mapNotNull { doc ->
                    doc.toObject(RallyEvent::class.java)?.copy(id = doc.id)
                }
                callback(events)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getEventById(eventId: String, callback: (RallyEvent?) -> Unit) {
        EventServices.firestore.collection("events").document(eventId).get()
            .addOnSuccessListener { document ->
                val event = document.toObject(RallyEvent::class.java)
                callback(event)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

}