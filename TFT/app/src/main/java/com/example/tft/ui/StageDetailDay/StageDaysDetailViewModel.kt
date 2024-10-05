package com.example.tft.ui.StageDetailDay
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
class StageDaysDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _substages = MutableLiveData<SubstagesResponse>()
    val substages: LiveData<SubstagesResponse> = _substages

    private val _stageImage = MutableLiveData<String>()
    val stageImage: LiveData<String> = _stageImage

    private val sharedPreferences = application.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)

    fun fetchSubstages(stageId: String) {
        viewModelScope.launch {
            val cachedSubstages = getCachedSubstages(stageId)
            if (cachedSubstages != null) {
                // Usar datos en caché
                _substages.value = cachedSubstages
                Log.d("StageDaysDetailViewModel", "Using cached substages data")
            } else {
                // Hacer la llamada a la API
                try {
                    val response = RetrofitInstance.api.getSubstages(stageId)
                    // Actualizar la caché
                    cacheSubstages(stageId, response)
                    _substages.value = response
                    Log.d("StageDaysDetailViewModel", "Fetched substages data from API")
                } catch (e: Exception) {
                    Log.e("StageDaysDetailViewModel", "Error fetching substages", e)
                }
            }
        }
    }



    private fun cacheSubstages(stageId: String, substages: SubstagesResponse) {
        val editor = sharedPreferences.edit()
        val substagesJson = Gson().toJson(substages)
        editor.putString("substages_cache_$stageId", substagesJson)
        editor.apply()
    }

    private fun getCachedSubstages(stageId: String): SubstagesResponse? {
        val substagesJson = sharedPreferences.getString("substages_cache_$stageId", null)
        return if (substagesJson != null) {
            val type = object : TypeToken<SubstagesResponse>() {}.type
            Gson().fromJson(substagesJson, type)
        } else {
            null
        }
    }

    private fun cacheStageImage(stageId: String, imageUrl: String) {
        val editor = sharedPreferences.edit()
        editor.putString("stage_image_cache_$stageId", imageUrl)
        editor.apply()
    }

    private fun getCachedStageImage(stageId: String): String? {
        return sharedPreferences.getString("stage_image_cache_$stageId", null)
    }
}
