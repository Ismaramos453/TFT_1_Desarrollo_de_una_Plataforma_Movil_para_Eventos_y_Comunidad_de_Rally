package com.example.tft.ui.PilotFavorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.PilotFavorites.PilotFavoritesServices
import com.example.tft.data.api.RetrofitInstance
import com.example.tft.data.services.User.UserServices
import com.example.tft.model.pilot.Team
import com.example.tft.model.user.Users
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PilotFavoritesViewModel : ViewModel() {
    private val _favoritePilotsDetails = MutableStateFlow<List<Team>>(emptyList())
    val favoritePilotsDetails = _favoritePilotsDetails.asStateFlow()

    // Suponemos que tenemos un método para obtener la información del usuario actual
    fun loadFavoritePilotsDetails(userId: String) {
        viewModelScope.launch {
            val user = fetchUserDetails(userId)  // Obtener los datos del usuario
            val pilotIds = user?.favoritePilots
            val pilots = mutableListOf<Team>()
            pilotIds?.forEach { pilotId ->
                try {
                    val response = RetrofitInstance.api.getPilotDetail(pilotId)
                    pilots.add(response.team)
                } catch (e: Exception) {
                    // Manejar excepciones aquí
                }
            }
            _favoritePilotsDetails.value = pilots
        }
    }
    fun removePilotFromFavorites(userId: String, pilotId: Int) {
        viewModelScope.launch {
            PilotFavoritesServices.removePilotFromFavorites(userId, pilotId)
            loadFavoritePilotsDetails(userId)  // Recargar la lista después de eliminar
        }
    }

    private suspend fun fetchUserDetails(userId: String): Users? {
        // Aquí deberías implementar la lógica para obtener los datos del usuario
        // Podría ser desde Firestore, una API REST, etc.
        return UserServices.getUserDetails(userId)  // Este es un ejemplo, necesitas implementar este método
    }
}
