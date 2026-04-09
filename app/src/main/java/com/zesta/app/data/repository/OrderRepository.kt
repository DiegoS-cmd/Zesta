package com.zesta.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zesta.app.data.model.Order
import kotlinx.coroutines.tasks.await
import java.util.UUID

val PROMO_CODES = mapOf(
    "ZESTA10" to 0.10,
    "ZESTA20" to 0.20,
    "BIENVENIDO" to 0.15
)

class OrderRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val cartRepository: CartRepository = CartRepository()
) {
    private fun getUserId(): String? = auth.currentUser?.uid

    fun validatePromoCode(code: String): Double? {
        return PROMO_CODES[code.uppercase().trim()]
    }

    suspend fun placeOrder(order: Order): Result<String> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val orderId = UUID.randomUUID().toString()
            val finalOrder = order.copy(orderId = orderId)
            db.collection("users")
                .document(uid)
                .collection("orders")
                .document(orderId)
                .set(finalOrder)
                .await()

            cartRepository.clearCartByRestaurant(order.restaurantId)

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}