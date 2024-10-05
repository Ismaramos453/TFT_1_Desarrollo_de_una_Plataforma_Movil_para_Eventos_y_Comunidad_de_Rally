package com.example.tft.data


import android.util.Log
import com.example.tft.model.user.Users
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AuthenticationServices {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }
    fun reauthenticateUser(currentPassword: String, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        val email = user?.email

        if (user != null && email != null) {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    callback(task.isSuccessful)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            callback(false)
        }
    }
    // Este método comprueba si el documento del usuario existe en Firestore
    fun checkIfUserExists(callback: (Boolean) -> Unit) {
        val currentUser = getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        callback(true)  // El usuario existe
                    } else {
                        callback(false)  // El usuario no existe
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("AuthenticationServices", "Error checking user existence: ${exception.message}")
                    callback(false)  // En caso de error, tratar como si el usuario no existiera
                }
        } else {
            callback(false)  // No hay un usuario autenticado
        }
    }

    fun updateUserPassword(newPassword: String, callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true, "Contraseña actualizada correctamente.")
                    } else {
                        callback(false, "Error al actualizar la contraseña.")
                    }
                }
        } else {
            callback(false, "Usuario no autenticado.")
        }
    }
    // URL of the default profile image
    val defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/tft-ismael.appspot.com/o/profile_images%2Fperfil.png?alt=media&token=228f72ea-b277-47d7-b2df-b7c0876cdbb1"

    suspend fun register(email: String, password: String, name: String, image: String? = null): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                val profileImageUrl = image ?: defaultProfileImageUrl  // Imagen predeterminada si no se proporciona

                val user = Users(
                    id = firebaseUser.uid,
                    userId = firebaseUser.email ?: "",
                    name = name,
                    image = profileImageUrl,
                    favoritePilots = mutableListOf(),  // Inicializa con una lista vacía de favoritos
                    eventsSaved = emptyList()
                )
                // Guarda el usuario en Firestore
                saveUserToFirestore(user)
            }
            result.user
        } catch (e: Exception) {
            Log.e("AuthenticationServices", "Registration failed", e)
            null
        }
    }


    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val result = auth.signInWithCredential(credential).await()
            result.user?.let {
                val user = Users(
                    id = it.uid,
                    userId = it.email ?: "",
                    name = it.displayName ?: "",
                    image = it.photoUrl?.toString() ?: "",
                    favoritePilots = mutableListOf(),  // Asegura que se inicializa la lista de favoritos
                    eventsSaved = emptyList()
                )
                saveUserToFirestore(user)
            }
            result.user
        } catch (e: Exception) {
            Log.e("AuthenticationServices", "Google sign-in failed", e)
            null
        }
    }


    private suspend fun saveUserToFirestore(user: Users) {
        Log.d("FirestoreService", "Attempting to save user: ${user}")
        firestore.collection("users").document(user.id!!).set(user)
            .addOnSuccessListener {
                Log.d("FirestoreService", "User document successfully written with image: ${user.image}")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreService", "Error writing user document", e)
            }
            .await() // Asegúrate de que el await está siendo usado correctamente dentro del contexto de una coroutine
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return getCurrentUser() != null
    }

    fun signOut() {
        auth.signOut()
    }
}