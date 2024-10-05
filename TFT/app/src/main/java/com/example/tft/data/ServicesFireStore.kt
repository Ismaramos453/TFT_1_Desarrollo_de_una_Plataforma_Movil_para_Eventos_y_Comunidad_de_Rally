package com.example.tft.data

import android.net.Uri
import android.util.Log
import com.example.tft.model.Car
import com.example.tft.model.CarCategory
import com.example.tft.model.GalleryItem
import com.example.tft.model.News
import com.example.tft.model.RallyEvent
import com.example.tft.model.foro.Answer
import com.example.tft.model.foro.Question
import com.example.tft.model.foro.Votation
import com.example.tft.model.user.Users
import com.example.tft.model.wrc.TeamWrc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await


object FirestoreService {

    private val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference



    // Agregar un piloto a favoritos
    fun addPilotToFavorites(userId: String, pilotId: Int) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("favoritePilots", FieldValue.arrayUnion(pilotId))
    }

    // Eliminar un piloto de favoritos
    fun removePilotFromFavorites(userId: String, pilotId: Int) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("favoritePilots", FieldValue.arrayRemove(pilotId))
    }

    // Obtener la lista de pilotos favoritos
    fun getFavoritePilots(userId: String, callback: (List<Int>) -> Unit) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(Users::class.java)
                user?.let {
                    callback(it.favoritePilots)
                }
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    suspend fun getUserDetails(userId: String): Users? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(Users::class.java) // Convertir el documento a un objeto Users
        } catch (e: Exception) {
            println("Error fetching user details: ${e.message}")
            null
        }
    }
    fun getTeams(callback: (List<TeamWrc>) -> Unit) {
        firestore.collection("wrc_teams")
            .get()
            .addOnSuccessListener { result ->
                val teams = result.documents.mapNotNull { doc ->
                    val team = doc.toObject(TeamWrc::class.java)
                    // Asegurarse de que la conversión de pilotos como array de strings se maneja bien
                    team?.copy(pilots = doc.get("pilots") as List<String>? ?: listOf())
                }
                callback(teams)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getNews(callback: (List<News>) -> Unit) {
        firestore.collection("news")
            .get()
            .addOnSuccessListener { result ->
                val newsList = result.documents.mapNotNull { doc ->
                    doc.toObject(News::class.java)?.copy(id = doc.id)
                }
                callback(newsList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun uploadProfileImage(imageUri: Uri, userId: String, documentId: String, callback: (Boolean) -> Unit) {
        val profileImageRef = storageReference.child("profile_images/$userId.jpg")
        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Log.d("FirestoreService", "Got download URL: $imageUrl, updating Firestore...")
                    updateUserImage(documentId, imageUrl, callback) // Pasa la URL obtenida
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error uploading profile image: ${exception.message}")
                callback(false)
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


    private const val DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/tft-ismael.appspot.com/o/profile_images%2Fismae%40gmail.com.jpg?alt=media&token=948f4509-7c28-464c-bb67-adbd0d9f7e06"

    // Lógica para obtener el perfil actual del usuario
    fun getCurrentUserProfile(callback: (Users?, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users").whereEqualTo("userId", user.email).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        val userProfile = document.toObject(Users::class.java)

                        // Verificar si la imagen está vacía y asignar la imagen por defecto
                        if (userProfile?.image.isNullOrEmpty()) {
                            userProfile?.image = DEFAULT_IMAGE_URL
                            // Actualizamos el documento de Firestore
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
                Log.e("FirestoreService", "Error getting car categories", exception)
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
                        Log.e("FirestoreService", "Error parsing car document", e)
                        null
                    }
                }
                callback(cars)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error getting cars from category", exception)
                callback(emptyList())
            }
    }
    fun loadEvents(callback: (List<RallyEvent>) -> Unit) {
        firestore.collection("events")
            .get()
            .addOnSuccessListener { result ->
                val events = result.documents.mapNotNull { doc ->
                    doc.toObject(RallyEvent::class.java)?.copy(id = doc.id)
                }
                callback(events)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getEventById(eventId: String, callback: (RallyEvent?) -> Unit) {
        firestore.collection("events").document(eventId).get()
            .addOnSuccessListener { document ->
                val event = document.toObject(RallyEvent::class.java)
                callback(event)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
    fun getGalleryItems(callback: (List<GalleryItem>) -> Unit) {
        firestore.collection("gallery")
            .get()
            .addOnSuccessListener { result ->
                val galleryItems = result.documents.mapNotNull { doc ->
                    val galleryItem = doc.toObject(GalleryItem::class.java)
                    galleryItem?.copy(id = doc.id)
                }
                callback(galleryItems)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error getting gallery items", exception)
                callback(emptyList())
            }
    }
    fun addQuestion(title: String, content: String, userId: String, callback: (Boolean) -> Unit) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            val userName = documentSnapshot.getString("name") ?: "Nombre Desconocido"
            val userImage = documentSnapshot.getString("image") ?: ""

            val docRef = firestore.collection("questions").document()
            val questionId = docRef.id

            val question = Question(
                id = questionId,
                userId = userId,
                userName = userName,
                userImage = userImage,
                title = title,
                content = content,
                timestamp = System.currentTimeMillis() // Asegúrate de que este campo se establece correctamente
            )

            docRef.set(question)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    callback(false)
                }
        }
    }

    fun updateQuestion(questionId: String, title: String, content: String, callback: (Boolean) -> Unit) {
        val questionRef = firestore.collection("questions").document(questionId)
        questionRef.update(mapOf(
            "title" to title,
            "content" to content
        ))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun deleteQuestion(questionId: String, callback: (Boolean) -> Unit) {
        firestore.collection("questions").document(questionId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }


    fun addVotation(title: String, userId: String, options: List<String>, callback: (Boolean, String?) -> Unit) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            val userName = documentSnapshot.getString("name") ?: "Nombre Desconocido"
            val userImage = documentSnapshot.getString("image") ?: ""
            val votation = Votation(
                userId = userId,
                userName = userName,
                userImage = userImage,
                title = title,
                options = options,
                timestamp = System.currentTimeMillis(),
                votes = options.associateWith { 0 }
            )
            val docRef = firestore.collection("votations").document()
            votation.id = docRef.id
            docRef.set(votation)
                .addOnSuccessListener {
                    callback(true, docRef.id)
                }
                .addOnFailureListener { e ->
                    callback(false, null)
                }
        }
    }


    fun getQuestionById(questionId: String, callback: (Question?) -> Unit) {
        val questionRef = firestore.collection("questions").document(questionId)
        questionRef.get().addOnSuccessListener { documentSnapshot ->
            val question = documentSnapshot.toObject(Question::class.java)
            callback(question)
        }.addOnFailureListener { e ->
            Log.e("FirestoreService", "Error getting question: ${e.localizedMessage}", e)
            callback(null)
        }
    }


    fun getAllItems(callback: (List<Any>) -> Unit) {
        firestore.collection("questions").get().addOnSuccessListener { questionSnapshot ->
            val questions = questionSnapshot.documents.mapNotNull { it.toObject(Question::class.java) }
            firestore.collection("votations").get().addOnSuccessListener { votationSnapshot ->
                val votations = votationSnapshot.documents.mapNotNull { it.toObject(Votation::class.java) }
                callback(questions + votations)
            }
        }
    }

    fun voteOnVotation(votationId: String, option: String, userId: String, callback: (Boolean) -> Unit) {
        val votationRef = firestore.collection("votations").document(votationId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(votationRef)
            val existingVote = snapshot.getString("userVote")
            val votes = (snapshot.get("votes") as? Map<String, Number> ?: emptyMap<String, Number>()).toMutableMap()

            // Decrementar el voto anterior, si existe
            if (!existingVote.isNullOrBlank() && votes[existingVote] != null) {
                val currentVotes = votes[existingVote]!!.toInt()  // Asegura la conversión a Int
                votes[existingVote] = (currentVotes - 1).coerceAtLeast(0)
            }

            // Incrementar el nuevo voto
            val newVotes = votes[option]?.toInt() ?: 0  // Maneja null y asegura Int
            votes[option] = newVotes + 1

            // Actualizar el voto del usuario y el mapa de votos
            transaction.update(votationRef, mapOf(
                "userVote" to option,
                "votes" to votes.mapValues { it.value.toInt() }  // Asegura que todos los valores sean Int antes de enviar
            ))
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener { e ->
            Log.e("FirestoreService", "Error updating votes: ${e.localizedMessage}", e)
            callback(false)
        }
    }

    fun addAnswer(questionId: String, content: String, userId: String, callback: (Boolean) -> Unit) {
        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            val userName = documentSnapshot.getString("name") ?: "Nombre Desconocido"

            val answer = Answer(
                userId = userId,
                userName = userName,  // Aquí añades el nombre del usuario
                content = content,
                timestamp = System.currentTimeMillis()
            )

            val questionRef = firestore.collection("questions").document(questionId)
            questionRef.update("answers", FieldValue.arrayUnion(answer))
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    callback(false)
                }
        }
    }


    fun getVotationById(votationId: String, callback: (Votation?) -> Unit) {
        val votationRef = firestore.collection("votations").document(votationId)
        votationRef.get().addOnSuccessListener { document ->
            val votation = document.toObject(Votation::class.java)
            callback(votation)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun updateVotation(votationId: String, updatedData: Map<String, Any>, callback: (Boolean) -> Unit) {
        val votationRef = firestore.collection("votations").document(votationId)
        votationRef.update(updatedData)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun deleteVotation(votationId: String, callback: (Boolean) -> Unit) {
        val votationRef = firestore.collection("votations").document(votationId)
        votationRef.delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getQuestions(callback: (List<Question>) -> Unit) {
        firestore.collection("questions")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Ordenar por timestamp descendente
            .get()
            .addOnSuccessListener { result ->
                val questions = result.documents.mapNotNull { it.toObject(Question::class.java) }
                callback(questions)
            }
            .addOnFailureListener {
                Log.e("FirestoreService", "Error getting questions", it)
                callback(emptyList())
            }
    }

}


