package com.example.tft.data.services.Cars

import android.util.Log
import com.example.tft.model.Car
import com.example.tft.model.CarCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object CarsServices {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storageReference = FirebaseStorage.getInstance().reference

    fun getCarCategories(callback: (List<CarCategory>) -> Unit) {
        firestore.collection("categories")
            .orderBy("priority")  // Asumiendo que hay un campo 'priority' para el orden
            .get()
            .addOnSuccessListener { result ->
                val categories = result.documents.mapNotNull { document ->
                    try {
                        val category = document.toObject(CarCategory::class.java)
                        category?.copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                callback(categories)
            }
            .addOnFailureListener { exception ->
                Log.e("CarsServices", "Error getting car categories", exception)
                callback(emptyList())
            }
    }

    fun getCarsFromCategory(categoryId: String, callback: (List<Car>) -> Unit) {
        firestore.collection("categories").document(categoryId).collection("cars").get()
            .addOnSuccessListener { result ->
                val cars = result.documents.mapNotNull { document ->
                    try {
                        document.toObject(Car::class.java)
                    } catch (e: Exception) {
                        Log.e("CarsServices", "Error parsing car document", e)
                        null
                    }
                }
                callback(cars)
            }
            .addOnFailureListener { exception ->
                Log.e("CarsServices", "Error getting cars from category", exception)
                callback(emptyList())
            }
    }

    // Nueva función para obtener los detalles de una categoría específica
    fun getCategoryById(categoryId: String, callback: (CarCategory?) -> Unit) {
        firestore.collection("categories").document(categoryId).get()
            .addOnSuccessListener { document ->
                try {
                    val category = document.toObject(CarCategory::class.java)
                    category?.let {
                        callback(it.copy(id = document.id))
                    } ?: callback(null)
                } catch (e: Exception) {
                    Log.e("CarsServices", "Error parsing category document", e)
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CarsServices", "Error getting category by ID", exception)
                callback(null)
            }
    }
}
