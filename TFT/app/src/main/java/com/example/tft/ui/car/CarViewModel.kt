package com.example.tft.ui.car

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Cars.CarsServices
import com.example.tft.model.Car


class CarViewModel : ViewModel() {
    private val _cars = MutableLiveData<List<Car>>()
    val cars: LiveData<List<Car>> = _cars

    fun loadCars(categoryId: String) {
        CarsServices.getCarsFromCategory(categoryId) {
            _cars.postValue(it)
        }
    }
}