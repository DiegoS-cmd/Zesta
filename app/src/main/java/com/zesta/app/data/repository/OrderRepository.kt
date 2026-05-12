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
    private fun uid() = auth.currentUser?.uid

    // Devuelve el descuento si el código existe, null si no es válido
    fun validatePromoCode(code: String): Double? = PROMO_CODES[code.uppercase().trim()]

    // Guarda el pedido en Firestore y vacía el carrito del restaurante
    suspend fun placeOrder(order: Order): Result<String> {
        val uid = uid() ?: return Result.failure(Exception("Usuario no autenticado"))

        if (order.restaurantId <= 0)
            return Result.failure(Exception("Restaurante no válido"))
        if (order.items.isEmpty() || order.items.all { it.cantidad <= 0 })
            return Result.failure(Exception("El pedido no contiene artículos válidos"))

        return try {
            val orderId = UUID.randomUUID().toString()

            // Guardamos en users/{uid}/orders/{orderId}
            db.collection("users").document(uid)
                .collection("orders").document(orderId)
                .set(order.copy(orderId = orderId))
                .await()

            // El pedido ya está guardado; si falla el vaciado del carrito lo notificamos igualmente
            val clearResult = cartRepository.clearCartByRestaurant(order.restaurantId)
            if (clearResult.isFailure) {
                return Result.failure(
                    clearResult.exceptionOrNull()
                        ?: Exception("El pedido se guardó, pero no se pudo vaciar el carrito")
                )
            }

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}