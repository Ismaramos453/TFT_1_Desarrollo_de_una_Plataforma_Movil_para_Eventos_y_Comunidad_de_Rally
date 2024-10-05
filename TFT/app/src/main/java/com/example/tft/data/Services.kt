package com.example.tft.data
import android.content.Context
import com.example.tft.model.RallyEvent
import com.example.tft.model.SavedEvent
import com.example.tft.model.pilot.Team
import com.example.tft.model.user.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


object EventService {
    private val firestore = FirebaseFirestore.getInstance()



    suspend fun saveEvent(context: Context, userId: String, event: RallyEvent): Boolean {
        val userRef = firestore.collection("users").document(userId)
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
                    image = event.image ?: ""
                )

                updatedEvents.add(savedEvent)
                userRef.update("eventsSaved", updatedEvents).await()
                return true
            }
        }
        return false
    }

    suspend fun loadEvents(userId: String): List<SavedEvent> {
        val userSnapshot = firestore.collection("users").document(userId).get().await()
        if (userSnapshot.exists()) {
            val user = userSnapshot.toObject(Users::class.java)
            return user?.eventsSaved ?: emptyList()
        }
        return emptyList()
    }

    suspend fun deleteEvent(userId: String, eventId: String) {
        val userRef = firestore.collection("users").document(userId)
        val userSnapshot = userRef.get().await()

        if (userSnapshot.exists()) {
            val user = userSnapshot.toObject(Users::class.java)
            user?.let {
                val updatedEvents = it.eventsSaved.filterNot { savedEvent -> savedEvent.id == eventId }
                userRef.update("eventsSaved", updatedEvents).await()
            }
        }
    }
}


object PilotServices {
    private val favorites = mutableListOf<Team>()

    fun addPilotToFavorites(pilot: Team) {
        if (!favorites.any { it.id == pilot.id }) {
            favorites.add(pilot)
        }
    }

    fun removePilotFromFavorites(pilotId: Int) {
        favorites.removeAll { it.id == pilotId }
    }

    fun isPilotFavorite(pilotId: Int): Boolean {
        return favorites.any { it.id == pilotId }
    }

    fun getFavorites(): List<Team> = favorites
}