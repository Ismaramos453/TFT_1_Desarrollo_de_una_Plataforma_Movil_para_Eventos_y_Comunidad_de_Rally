package com.example.tft.ui.StageDetail
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tft.data.api.RetrofitInstance
import com.example.tft.model.pilot.RallyStandingsResponse2
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch

class StageDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _standings = MutableLiveData<RallyStandingsResponse2>()
    val standings: LiveData<RallyStandingsResponse2> = _standings

    private val sharedPreferences = application.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)

    fun fetchStandings(stageId: String) {
        viewModelScope.launch {
            val cachedStandings = getCachedStandings(stageId)
            if (cachedStandings != null && cachedStandings.standings.isNotEmpty()) {
                // Usar datos en caché
                _standings.value = cachedStandings
                Log.d("StageDetailViewModel", "Using cached standings data")
            } else {
                // Hacer la llamada a la API
                try {
                    val response = RetrofitInstance.api.getStandings(stageId)

                    // Verificar si la respuesta está vacía
                    if (response.standings.isNullOrEmpty()) {
                        Log.w("StageDetailViewModel", "No standings data available for this stage.")
                        // Opcional: asignar un valor vacío para indicar que no hay datos
                        _standings.value = RallyStandingsResponse2(emptyList())
                    } else {
                        // Actualizar la caché
                        cacheStandings(stageId, response)
                        _standings.value = response
                        Log.d("StageDetailViewModel", "Fetched standings data from API")
                    }
                } catch (e: Exception) {
                    Log.e("StageDetailViewModel", "Error fetching standings", e)
                    // Si falla la API y no hay datos en caché, puedes asignar un valor vacío o manejar el error
                    if (cachedStandings == null) {
                        _standings.value = RallyStandingsResponse2(emptyList())
                    }
                }
            }
        }
    }


    private fun cacheStandings(stageId: String, standings: RallyStandingsResponse2) {
        val editor = sharedPreferences.edit()
        val standingsJson = Gson().toJson(standings)
        editor.putString("standings_cache_$stageId", standingsJson)
        editor.apply()
    }

    private fun getCachedStandings(stageId: String): RallyStandingsResponse2? {
        val standingsJson = sharedPreferences.getString("standings_cache_$stageId", null)
        return if (standingsJson != null) {
            val type = object : TypeToken<RallyStandingsResponse2>() {}.type
            Gson().fromJson(standingsJson, type)
        } else {
            null
        }
    }
}