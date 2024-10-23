package com.example.tft.ui.carCategories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.Cars.CarsServices
import com.example.tft.model.Car
import com.example.tft.model.CarCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarCategoriesViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<CarCategory>>()
    val categories: LiveData<List<CarCategory>> = _categories

    init {
        viewModelScope.launch {
            loadCategories()
        }
    }

    private suspend fun loadCategories() {
        withContext(Dispatchers.IO) { // Ejecuta en un hilo de fondo
            CarsServices.getCarCategories {
                _categories.postValue(it)
            }
        }
    }
}

