package com.zesta.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RatingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getUserRating(restaurantId: Int): Int? {
        val uid = auth.currentUser?.uid ?: return null
        val docId = "${restaurantId}_${uid}"
        return try {
            val snap = db.collection("ratings").document(docId).get().await()
            android.util.Log.d(
                "RATING",
                "docId=$docId exists=${snap.exists()} stars=${snap.getLong("stars")}"
            )
            if (snap.exists()) snap.getLong("stars")?.toInt() else null
        } catch (e: Exception) {
            android.util.Log.e("RATING", "Error leyendo rating", e)
            null
        }
    }

    suspend fun saveRating(restaurantId: Int, stars: Int) {
        val uid = auth.currentUser?.uid ?: return
        val docId = "${restaurantId}_${uid}"
        try {
            db.collection("ratings").document(docId).set(
                mapOf(
                    "restaurantId" to restaurantId,
                    "userId" to uid,
                    "stars" to stars,
                    "timestamp" to com.google.firebase.Timestamp.now()
                )
            ).await()
            android.util.Log.d("RATING", "Guardado OK docId=$docId stars=$stars")
        } catch (e: Exception) {
            android.util.Log.e("RATING", "Error guardando rating", e)
        }
    }
}