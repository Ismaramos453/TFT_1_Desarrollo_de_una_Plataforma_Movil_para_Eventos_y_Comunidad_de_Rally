package com.example.tft.data.services.User

import android.net.Uri
import android.util.Log
import com.example.tft.model.user.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object UserServices{

    private val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference
    private const val DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/tft-ismael.appspot.com/o/profile_images%2Fismae%40gmail.com.jpg?alt=media&token=948f4509-7c28-464c-bb67-adbd0d9f7e06"
    suspend fun getUserDetails(userId: String): Users? {
        return try {
            val document = UserServices.firestore.collection("users").document(userId).get().await()
            document.toObject(Users::class.java)
        } catch (e: Exception) {
            println("Error fetching user details: ${e.message}")
            null
        }
    }

    fun uploadProfileImage(imageUri: Uri, userId: String, callback: (Boolean, String?) -> Unit) {
        val profileImageRef = UserServices.storageReference.child("profile_images/$userId.jpg")
        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Log.d("FirestoreService", "Got download URL: $imageUrl")
                    callback(true, imageUrl)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error uploading profile image: ${exception.message}")
                callback(false, null)
            }
    }

    private fun updateUserImage(documentId: String, imageUrl: String, callback: (Boolean) -> Unit) {
        UserServices.firestore.collection("users").document(documentId)
            .update("image", imageUrl)
            .addOnSuccessListener {
                Log.d("FirestoreService", "User profile image URL updated in Firestore: $imageUrl")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error updating user image in Firestore: ${exception.message}")
                callback(false)
            }
    }

    // Lógica para obtener el perfil actual del usuario
    fun getCurrentUserProfile(callback: (Users?, String?) -> Unit) {
        val user = UserServices.auth.currentUser
        if (user != null) {
            UserServices.firestore.collection("users").whereEqualTo("email", user.email).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        val userProfile = document.toObject(Users::class.java)

                        Log.d("FirestoreService", "User profile retrieved: $userProfile")

                        // Verificar si la imagen está vacía y asignar la imagen por defecto
                        if (userProfile?.image.isNullOrEmpty()) {
                            userProfile?.image = DEFAULT_IMAGE_URL
                            // Actualizar el documento de Firestore solo si es necesario
                            updateUserImage(document.id, DEFAULT_IMAGE_URL) { success ->
                                Log.d("FirestoreService", "Imagen por defecto asignada: $DEFAULT_IMAGE_URL")
                            }
                        }

                        callback(userProfile, document.id)
                    } else {
                        Log.w("FirestoreService", "User document does not exist")
                        callback(null, null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreService", "Error getting user document", exception)
                    callback(null, null)
                }
        } else {
            Log.w("FirestoreService", "No user is logged in")
            callback(null, null)
        }
    }


    // Función para crear o actualizar el perfil de usuario
    fun updateUserProfile(user: Users, documentId: String, callback: (Boolean) -> Unit) {
        // Si la imagen está vacía, asignar la imagen por defecto
        if (user.image.isEmpty()) {
            user.image = DEFAULT_IMAGE_URL
        }

        UserServices.firestore.collection("users").document(documentId)
            .set(user)
            .addOnSuccessListener {
                Log.d("FirestoreService", "UserProfile updated")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error updating user document", exception)
                callback(false)
            }
    }

}