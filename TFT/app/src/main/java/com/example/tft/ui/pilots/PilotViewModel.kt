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
        if (cachedRallyPilots != null && cachedRallyPilots.isNotEmpty()) {
            _allRallyPilots.value = cachedRallyPilots
            _filteredRallyPilots.value = cachedRallyPilots
            _countries.value = listOf("All") + cachedRallyPilots.map { it.country.name ?: "" }.distinct()
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val seasonsResponse = RetrofitInstance.api.getSeasons()

                // Ordenar temporadas por año descendente (primero la más reciente con datos pasados)
                val sortedSeasons = seasonsResponse.seasons
                    .filter { it.year.toIntOrNull() != null }
                    .sortedByDescending { it.year.toInt() }

                var rallyTeams: List<Team>? = null

                // Intentar encontrar standings en alguna temporada
                for (season in sortedSeasons) {
                    // Evitar temporadas futuras (ej: año >= 2025 si aún no ha empezado)
                    val currentTime = System.currentTimeMillis() / 1000
                    if (season.startDateTimestamp > currentTime) {
                        // Esta temporada no ha empezado, no vale la pena buscar standings
                        continue
                    }

                    val substagesResponse = RetrofitInstance.api.getSubstages(season.id.toString())
                    for (substage in substagesResponse.stages) {
                        // Verifica que el evento esté empezado o finalizado
                        if (substage.status.type == "notstarted") {
                            continue // Este evento no tiene standings todavía
                        }

                        val standingsResponse = RetrofitInstance.api.getStandings(substage.id.toString())
                        if (standingsResponse.standings.isNotEmpty()) {
                            rallyTeams = standingsResponse.standings
                                .map { it.team }
                                .filter { it.category.name == "Rally" }
                            if (rallyTeams.isNotEmpty()) {
                                break
                            }
                        }
                    }

                    if (!rallyTeams.isNullOrEmpty()) break
                }

                if (rallyTeams.isNullOrEmpty()) {
                    // No se encontraron standings en ninguna temporada pasada
                    // Muestra un mensaje o rellena con otro tipo de información
                    _allRallyPilots.value = emptyList()
                    _filteredRallyPilots.value = emptyList()
                    _countries.value = listOf("All")
                } else {
                    // Cachear y mostrar los pilotos encontrados
                    cacheRallyPilots(rallyTeams)
                    _allRallyPilots.value = rallyTeams
                    _filteredRallyPilots.value = rallyTeams
                    _countries.value = listOf("All") + rallyTeams.map { it.country.name ?: "" }.distinct()
                }

            } catch (e: Exception) {
                // Manejo de error
                _allRallyPilots.value = emptyList()
                _filteredRallyPilots.value = emptyList()
                _countries.value = listOf("All")
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








