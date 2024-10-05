package com.example.tft.ui.SeasonDetail

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tft.data.api.RetrofitInstance
import com.example.tft.model.detail_stage.SubstagesResponse
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SeasonDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _substages = MutableLiveData<SubstagesResponse>()
    val substages: LiveData<SubstagesResponse> = _substages

    private val sharedPreferences = application.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)

    fun fetchSubstages(seasonId: String) {
        viewModelScope.launch {
            val cachedSubstages = getCachedSubstages(seasonId)
            if (cachedSubstages != null) {
                // Usar datos en caché
                _substages.value = cachedSubstages
                Log.d("SeasonDetailViewModel", "Using cached substages data")
            } else {
                // Hacer la llamada a la API
                try {
                    val response = RetrofitInstance.api.getSubstages(seasonId)
                    // Actualizar la caché
                    cacheSubstages(seasonId, response)
                    _substages.value = response
                    Log.d("SeasonDetailViewModel", "Fetched substages data from API")
                } catch (e: Exception) {
                    Log.e("SeasonDetailViewModel", "Error fetching substages", e)
                }
            }
        }
    }

    private fun cacheSubstages(seasonId: String, substages: SubstagesResponse) {
        val editor = sharedPreferences.edit()
        val substagesJson = Gson().toJson(substages)
        editor.putString("substages_cache_$seasonId", substagesJson)
        editor.apply()
    }

    private fun getCachedSubstages(seasonId: String): SubstagesResponse? {
        val substagesJson = sharedPreferences.getString("substages_cache_$seasonId", null)
        return if (substagesJson != null) {
            val type = object : TypeToken<SubstagesResponse>() {}.type
            Gson().fromJson(substagesJson, type)
        } else {
            null
        }
    }
}
