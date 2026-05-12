package com.zesta.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RatingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // El docId combina restaurante y usuario para que cada par tenga su propia valoración
    private fun docId(restaurantId: Int): String? {
        val uid = auth.currentUser?.uid ?: return null
        return "${restaurantId}_${uid}"
    }

    suspend fun getUserRating(restaurantId: Int): Int? {
        val id = docId(restaurantId) ?: return null
        return try {
            val snap = db.collection("ratings").document(id).get().await()
            if (snap.exists()) snap.getLong("stars")?.toInt() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveRating(restaurantId: Int, stars: Int) {
        val id = docId(restaurantId) ?: return
        val uid = auth.currentUser?.uid ?: return
        try {
            db.collection("ratings").document(id).set(mapOf(
                "restaurantId" to restaurantId,
                "userId" to uid,
                "stars" to stars,
                "timestamp" to Timestamp.now()
            )).await()
        } catch (e: Exception) {
            // una valoración que no se guarda no rompe nada
        }
    }
}