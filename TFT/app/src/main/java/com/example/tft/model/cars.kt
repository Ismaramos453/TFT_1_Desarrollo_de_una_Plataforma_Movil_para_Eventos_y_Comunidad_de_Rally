package com.example.tft.model
data class Summary(
    val averagePower: String = "",
    val averageWeight: String = "",
    val title: String = "",
    val image: String = ""  // URL de la imagen
)

data class CarCategory(
    val id: String = "",
    val summary: Summary = Summary(),
    val cars: List<Car> = emptyList()
)



data class Car(
    val model: String = "",
    val power: String = "",
    val weight: String = "",
    val price: String = "",
    val image: String = ""  // Campo adicional para la URL de la imagen
)