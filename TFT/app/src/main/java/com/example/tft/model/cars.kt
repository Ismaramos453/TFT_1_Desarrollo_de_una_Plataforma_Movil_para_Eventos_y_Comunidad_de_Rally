package com.example.tft.model
data class Summary(
    val averagePower: String = "",
    val averageWeight: String = "",
    val title: String = "",
    val image: String = ""
)

data class CarCategory(
    val id: String = "",
    val summary: Summary = Summary(),
    val cars: List<Car> = emptyList(),
    val category: String = "",
)

data class Car(
    val model: String = "",
    val power: String = "",
    val weight: String = "",
    val price: String = "",
    val image: String = ""
)