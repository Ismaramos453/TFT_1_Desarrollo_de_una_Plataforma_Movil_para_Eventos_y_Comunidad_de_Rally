package com.example.tft.data.services.User

import android.net.Uri
import android.util.Log
import com.example.tft.model.user.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await



object UserServices {

    private val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    private const val DEFAULT_IMAGE_URL =
        "https://firebasestorage.googleapis.com/v0/b/tft-ismael.appspot.com/o/profile_images%2Fismae%40gmail.com.jpg?alt=media&token=948f4509-7c28-464c-bb67-adbd0d9f7e06"

    /**
     * Obtiene los detalles de un usuario (por UID) desde Firestore.
     */
    suspend fun getUserDetails(userId: String): Users? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(Users::class.java)
        } catch (e: Exception) {
            println("Error fetching user details: ${e.message}")
            null
        }
    }

    /**
     * Sube la imagen de perfil a la ruta "profile_images/UID.jpg"
     * Verifica si currentUser está logueado y si coincide con 'userId'.
     */
    fun uploadProfileImage(imageUri: Uri, userId: String, callback: (Boolean, String?) -> Unit) {
        // <-- CAMBIOS: verificamos que el usuario esté logueado y coincida su UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("FirestoreService", "No user is logged in. Can't upload.")
            callback(false, null)
            return
        }
        if (currentUser.uid != userId) {
            Log.e("FirestoreService", "UID mismatch: currentUser.uid=${currentUser.uid}, targetUid=$userId")
            callback(false, null)
            return
        }

        val profileImageRef = storageReference.child("profile_images/$userId.jpg")

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
        firestore.collection("users").document(documentId)
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

    /**
     * Obtener el perfil del usuario actual (usando su email)
     * Si no existe imagen, asignar la imagen por defecto.
     */
    fun getCurrentUserProfile(callback: (Users?, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users").whereEqualTo("email", user.email).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        val userProfile = document.toObject(Users::class.java)

                        Log.d("FirestoreService", "User profile retrieved: $userProfile")

                        if (userProfile?.image.isNullOrEmpty()) {
                            userProfile?.image = DEFAULT_IMAGE_URL
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

    fun updateUserProfile(user: Users, documentId: String, callback: (Boolean) -> Unit) {
        if (user.image.isEmpty()) {
            user.image = DEFAULT_IMAGE_URL
        }

        firestore.collection("users").document(documentId)
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
