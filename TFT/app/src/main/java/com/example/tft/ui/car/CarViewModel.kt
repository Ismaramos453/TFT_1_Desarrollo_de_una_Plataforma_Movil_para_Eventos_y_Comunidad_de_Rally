package com.example.tft.ui.car

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Cars.CarsServices
import com.example.tft.model.Car

class CarViewModel : ViewModel() {
    private val _cars = MutableLiveData<List<Car>>()
    val cars: LiveData<List<Car>> = _cars

    private val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String> = _categoryName

    fun loadCars(categoryId: String) {
        // Detalles de la categoría
        CarsServices.getCategoryById(categoryId) { category ->
            if (category != null) {
                _categoryName.postValue(category.category)
                // Cargamos los coches de la categoría
                CarsServices.getCarsFromCategory(categoryId) { carList ->
                    _cars.postValue(carList)
                }
            } else {
                _categoryName.postValue("Categoría desconocida")
                _cars.postValue(emptyList())
            }
        }
    }
}
