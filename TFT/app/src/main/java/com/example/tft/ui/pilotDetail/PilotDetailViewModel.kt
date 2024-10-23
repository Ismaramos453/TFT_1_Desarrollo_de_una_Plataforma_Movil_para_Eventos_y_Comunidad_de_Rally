package com.example.tft.ui.pilotDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.api.RetrofitInstance
import com.example.tft.data.services.PilotFavorites.PilotFavoritesServices
import com.example.tft.model.detail_stage.Race
import com.example.tft.model.detail_stage.Season

import com.example.tft.model.pilot.Team
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PilotDetailViewModel : ViewModel() {

    private val _pilotDetail = MutableLiveData<Team>()
    val pilotDetail: LiveData<Team> get() = _pilotDetail

    private val _pilotRaces = MutableLiveData<List<Race>>()
    val pilotRaces: LiveData<List<Race>> get() = _pilotRaces

    private val _seasons = MutableLiveData<List<Season>>()
    val seasons: LiveData<List<Season>> get() = _seasons

    fun loadPilotDetail(pilotId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPilotDetail(pilotId)
                _pilotDetail.value = response.team
            } catch (e: IOException) {
                // Manejo de errores de red
            } catch (e: HttpException) {
                // Manejo de errores HTTP
            }
        }
    }
    fun addPilotToFavorites(userId: String, pilotId: Int) {
        viewModelScope.launch {
            try {
                PilotFavoritesServices.addPilotToFavorites(userId, pilotId)
            } catch (e: Exception) {
                // Handle the error appropriately
            }
        }
    }

    fun loadSeasons() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getSeasons()
                _seasons.value = response.seasons
            } catch (e: IOException) {
                // Manejo de errores de red
            } catch (e: HttpException) {
                // Manejo de errores HTTP
            }
        }
    }

    fun loadPilotRaces(pilotId: Int, seasonId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPilotRaces(pilotId, seasonId)
                _pilotRaces.value = response.races
            } catch (e: IOException) {
                // Manejo de errores de red
            } catch (e: HttpException) {
                // Manejo de errores HTTP
            }
        }
    }

}
