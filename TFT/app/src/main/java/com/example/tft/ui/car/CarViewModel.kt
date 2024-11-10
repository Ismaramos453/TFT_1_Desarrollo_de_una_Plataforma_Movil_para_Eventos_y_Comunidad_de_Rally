package com.example.tft.ui.car

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Cars.CarsServices
import com.example.tft.model.Car
import com.example.tft.model.CarCategory


class CarViewModel : ViewModel() {
    private val _cars = MutableLiveData<List<Car>>()
    val cars: LiveData<List<Car>> = _cars

    private val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String> = _categoryName

    fun loadCars(categoryId: String) {
        // Primero, obtenemos los detalles de la categoría
        CarsServices.getCategoryById(categoryId) { category ->
            if (category != null) {
                _categoryName.postValue(category.category)  // Asumiendo que el nombre de la categoría está en el campo 'category'
                // Luego, cargamos los coches de la categoría
                CarsServices.getCarsFromCategory(categoryId) { carList ->
                    _cars.postValue(carList)
                }
            } else {
                // Manejar el caso en que la categoría no se pudo cargar
                _categoryName.postValue("Categoría desconocida")
                _cars.postValue(emptyList())
            }
        }
    }
}
