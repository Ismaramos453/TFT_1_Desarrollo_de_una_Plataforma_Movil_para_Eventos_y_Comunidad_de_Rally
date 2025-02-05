package com.example.tft.data.services.Authentication


import android.util.Log
import com.example.tft.model.user.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AuthenticationServices {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    val defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/tft-ismael.appspot.com/o/profile_images%2Fperfil.png?alt=media&token=228f72ea-b277-47d7-b2df-b7c0876cdbb1"
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                // Verificamos en Firestore el campo identifier
                val documentSnapshot = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                val identifierFromDb = documentSnapshot.getString("identifier")
                // Si no existe el campo o es distinto de "user", no se permite acceso
                if (identifierFromDb != "user") {
                    signOut() // Deslogea inmediatamente
                    return null // Retorna null -> forzamos error en la capa superior
                }
            }
            firebaseUser
        } catch (e: Exception) {
            null
        }
    }
    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun checkIfUserExists(callback: (Boolean) -> Unit) {
        val currentUser = getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("AuthenticationServices", "Error checking user existence: ${exception.message}")
                    callback(false)
                }
        } else {
            callback(false)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        name: String,
        image: String? = null,
        identifier: String = "user" // <-- Nuevo parámetro (por defecto "user")
    ): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                val profileImageUrl = image ?: defaultProfileImageUrl

                val user = Users(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = name,
                    image = profileImageUrl,
                    favoritePilots = mutableListOf(),
                    eventsSaved = emptyList(),
                    identifier = identifier // <-- Asignar el nuevo campo
                )
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
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val userDocRef = firestore.collection("users").document(firebaseUser.uid)
                val documentSnapshot = userDocRef.get().await()

                // 1. Si el doc NO existe -> es la primera vez que se registra con Google -> se crea
                if (!documentSnapshot.exists()) {
                    val newUser = Users(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        name = firebaseUser.displayName ?: "",
                        image = firebaseUser.photoUrl?.toString() ?: "",
                        favoritePilots = mutableListOf(),
                        eventsSaved = emptyList(),
                        identifier = "user"
                    )
                    saveUserToFirestore(newUser)
                } else {
                    // 2. Si YA existe, verificamos que identifier sea "user"
                    val identifierFromDb = documentSnapshot.getString("identifier")
                    if (identifierFromDb != "user") {
                        signOut()
                        return null
                    }
                }
            }
            firebaseUser
        } catch (e: Exception) {
            Log.e("AuthenticationServices", "Google sign-in failed", e)
            null
        }
    }


    private suspend fun saveUserToFirestore(user: Users) {
        Log.d("FirestoreService", "Attempting to save user: $user")
        firestore.collection("users").document(user.id!!).set(user)
            .addOnSuccessListener {
                Log.d("FirestoreService", "User document successfully written with image: ${user.image}")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreService", "Error writing user document", e)
            }
            .await()
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut() {
        auth.signOut()
    }
}