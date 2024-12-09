package com.example.tft.ui.pilots

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tft.data.api.RetrofitInstance
import com.example.tft.model.pilot.Team
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch

class PilotViewModel(application: Application) : AndroidViewModel(application) {

    private val _allRallyPilots = MutableLiveData<List<Team>>()
    private val _filteredRallyPilots = MutableLiveData<List<Team>>()
    val rallyPilots: LiveData<List<Team>> = _filteredRallyPilots

    private val _countries = MutableLiveData<List<String>>()
    val countries: LiveData<List<String>> = _countries

    private val sharedPreferences = application.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)
    private var isLoading = false

    init {
        loadRallyPilots()
    }

    fun loadRallyPilots() {
        if (isLoading) return
        val cachedRallyPilots = getCachedRallyPilots()
        if (cachedRallyPilots != null) {
            _allRallyPilots.value = cachedRallyPilots
            _filteredRallyPilots.value = cachedRallyPilots
            _countries.value = listOf("All") + cachedRallyPilots.map { it.country.name ?: "" }.distinct()
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val seasonsResponse = RetrofitInstance.api.getSeasons()
                val seasonId = seasonsResponse.seasons.firstOrNull()?.id ?: return@launch

                val substagesResponse = RetrofitInstance.api.getSubstages(seasonId.toString())
                val substageId = substagesResponse.stages.firstOrNull()?.id ?: return@launch

                val standingsResponse = RetrofitInstance.api.getStandings(substageId.toString())
                val rallyTeams = standingsResponse.standings
                    .map { it.team }
                    .filter { it.category.name == "Rally" }

                cacheRallyPilots(rallyTeams)
                _allRallyPilots.value = rallyTeams
                _filteredRallyPilots.value = rallyTeams
                _countries.value = listOf("All") + rallyTeams.map { it.country.name ?: "" }.distinct()
            } catch (e: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    private fun cacheRallyPilots(rallyPilots: List<Team>) {
        val editor = sharedPreferences.edit()
        val rallyPilotsJson = Gson().toJson(rallyPilots)
        editor.putString("rally_pilots_cache", rallyPilotsJson)
        editor.apply()
    }

    private fun getCachedRallyPilots(): List<Team>? {
        val rallyPilotsJson = sharedPreferences.getString("rally_pilots_cache", null)
        return if (rallyPilotsJson != null) {
            val type = object : TypeToken<List<Team>>() {}.type
            Gson().fromJson(rallyPilotsJson, type)
        } else {
            null
        }
    }

    fun searchPilots(query: String) {
        val allPilots = _allRallyPilots.value ?: return
        val filtered = allPilots.filter {
            it.name.contains(query, ignoreCase = true)
        }
        _filteredRallyPilots.value = filtered
    }

    fun filterByCountry(country: String) {
        val allPilots = _allRallyPilots.value ?: return
        val filtered = if (country == "All") {
            allPilots
        } else {
            allPilots.filter { it.country.name == country }
        }
        _filteredRallyPilots.value = filtered
    }
}








