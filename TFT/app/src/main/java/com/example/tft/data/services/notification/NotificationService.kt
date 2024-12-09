package com.example.tft.data.services.notification

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object NotificationService {
    private val firestore = FirebaseFirestore.getInstance()

    fun addNotification(userId: String, message: String, callback: (Boolean) -> Unit) {
        val notificationData = hashMapOf(
            "userId" to userId,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("notifications").add(notificationData)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getNotifications(userId: String, callback: (List<Map<String, Any>>) -> Unit) {
        firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val notifications = result.documents.map { it.data!! }
                callback(notifications)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

}
