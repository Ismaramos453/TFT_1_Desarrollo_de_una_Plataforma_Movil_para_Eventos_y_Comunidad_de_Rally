package com.example.tft.ui.season

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tft.data.api.RetrofitInstance
import com.example.tft.model.detail_stage.Season
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SeasonViewModel(application: Application) : AndroidViewModel(application) {

    private val _seasons = MutableLiveData<List<Season>>()
    val seasons: LiveData<List<Season>> = _seasons

    private val sharedPreferences = application.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)

    init {
        fetchSeasons()
    }

    private fun fetchSeasons() {
        viewModelScope.launch {
            val cachedSeasons = getCachedSeasons()
            if (cachedSeasons != null) {
                // Usar datos en caché
                _seasons.value = cachedSeasons
                Log.d("SeasonViewModel", "Using cached seasons data")
            } else {
                // Hacer la llamada a la API
                try {
                    val response = RetrofitInstance.api.getSeasons()
                    // Actualizar la caché
                    cacheSeasons(response.seasons)
                    _seasons.value = response.seasons
                    Log.d("SeasonViewModel", "Fetched seasons data from API")
                } catch (e: Exception) {
                    Log.e("SeasonViewModel", "Error fetching seasons", e)
                }
            }
        }
    }

    private fun cacheSeasons(seasons: List<Season>) {
        val editor = sharedPreferences.edit()
        val seasonsJson = Gson().toJson(seasons)
        editor.putString("seasons_cache", seasonsJson)
        editor.apply()
    }

    private fun getCachedSeasons(): List<Season>? {
        val seasonsJson = sharedPreferences.getString("seasons_cache", null)
        return if (seasonsJson != null) {
            val type = object : TypeToken<List<Season>>() {}.type
            Gson().fromJson(seasonsJson, type)
        } else {
            null
        }
    }
}