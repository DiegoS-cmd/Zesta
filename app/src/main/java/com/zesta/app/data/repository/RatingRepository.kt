package com.zesta.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RatingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // el docId combina restaurante y usuario para que cada par tenga su propia valoración
    private fun docId(restaurantId: Int): String? {
        val uid = auth.currentUser?.uid ?: return null
        return "${restaurantId}_${uid}"
    }

    suspend fun getUserRating(restaurantId: Int): Int? {
        val id = docId(restaurantId) ?: return null
        return try {
            val snap = db.collection("ratings").document(id).get().await()
            android.util.Log.d("RATING", "docId=$id exists=${snap.exists()} stars=${snap.getLong("stars")}")
            if (snap.exists()) snap.getLong("stars")?.toInt() else null
        } catch (e: Exception) {
            android.util.Log.e("RATING", "Error leyendo rating", e)
            null
        }
    }

    suspend fun saveRating(restaurantId: Int, stars: Int) {
        val id = docId(restaurantId) ?: return
        try {
            db.collection("ratings").document(id).set(
                mapOf(
                    "restaurantId" to restaurantId,
                    "userId" to auth.currentUser!!.uid,
                    "stars" to stars,
                    "timestamp" to Timestamp.now()
                )
            ).await()
            android.util.Log.d("RATING", "Guardado OK docId=$id stars=$stars")
        } catch (e: Exception) {
            android.util.Log.e("RATING", "Error guardando rating", e)
        }
    }
}