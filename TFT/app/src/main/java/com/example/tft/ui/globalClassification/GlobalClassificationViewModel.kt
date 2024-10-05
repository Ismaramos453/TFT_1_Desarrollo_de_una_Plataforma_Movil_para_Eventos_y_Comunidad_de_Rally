package com.example.tft.ui.globalClassification

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tft.data.api.RetrofitInstance
import com.example.tft.model.pilot.Standing
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch

class GlobalClassificationViewModel(application: Application) : AndroidViewModel(application) {

    private val _standings = MutableLiveData<List<Standing>>()
    val standings: LiveData<List<Standing>> = _standings

    private val sharedPreferences = application.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)

    fun fetchStandings(seasonId: String) {
        viewModelScope.launch {
            val cachedStandings = getCachedStandings(seasonId)
            if (cachedStandings != null) {
                _standings.value = cachedStandings
                Log.d("GlobalClassificationViewModel", "Using cached standings data")
            } else {
                try {
                    val response = RetrofitInstance.api.getStandings(seasonId)
                    cacheStandings(seasonId, response.standings)
                    _standings.value = response.standings
                    Log.d("GlobalClassificationViewModel", "Fetched standings data from API")
                } catch (e: Exception) {
                    Log.e("GlobalClassificationViewModel", "Error fetching standings", e)
                }
            }
        }
    }

    private fun cacheStandings(seasonId: String, standings: List<Standing>) {
        val editor = sharedPreferences.edit()
        val standingsJson = Gson().toJson(standings)
        editor.putString("standings_cache_$seasonId", standingsJson)
        editor.apply()
    }

    private fun getCachedStandings(seasonId: String): List<Standing>? {
        val standingsJson = sharedPreferences.getString("standings_cache_$seasonId", null)
        return if (standingsJson != null) {
            val type = object : TypeToken<List<Standing>>() {}.type
            Gson().fromJson(standingsJson, type)
        } else {
            null
        }
    }
}

