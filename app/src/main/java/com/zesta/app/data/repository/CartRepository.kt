package com.zesta.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.model.RestaurantCart
import kotlinx.coroutines.tasks.await

data class RestaurantCartWithItems(
    val cart: RestaurantCart,
    val items: List<CartItem>
)

class CartRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun uid() = auth.currentUser?.uid

    // Ruta base a los carritos del usuario: users/{uid}/carts
    private fun cartsRef(uid: String) =
        db.collection("users").document(uid).collection("carts")

    // Devuelve todos los carritos activos con sus items, ordenados por restaurante
    suspend fun getRestaurantCarts(): Result<List<RestaurantCartWithItems>> {
        val uid = uid() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val carts = cartsRef(uid).get().await().documents.mapNotNull { doc ->
                val cart = doc.toObject(RestaurantCart::class.java) ?: return@mapNotNull null
                val items = doc.reference.collection("items").get().await()
                    .documents.mapNotNull { it.toObject(CartItem::class.java) }
                RestaurantCartWithItems(cart, items)
            }.sortedBy { it.cart.restaurantId }
            Result.success(carts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addItem(
        restaurantId: Int,
        restaurantName: String,
        restaurantImageResName: String,
        item: CartItem
    ): Result<Unit> {
        val uid = uid() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val cartDoc = cartsRef(uid).document(restaurantId.toString())
            val itemDoc = cartDoc.collection("items").document(item.productId)

            db.runTransaction { tx ->
                // Primero leemos y luego escribimos
                val cartSnap = tx.get(cartDoc)
                val itemSnap = tx.get(itemDoc)

                if (!cartSnap.exists()) {
                    tx.set(cartDoc, RestaurantCart(restaurantId, restaurantName, restaurantImageResName))
                }

                if (itemSnap.exists()) {
                    val cant = itemSnap.toObject(CartItem::class.java)?.cantidad ?: 0
                    tx.update(itemDoc, "cantidad", cant + 1)
                } else {
                    tx.set(itemDoc, item.copy(cantidad = 1))
                }
                null
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun increaseQuantity(restaurantId: Int, item: CartItem): Result<Unit> {
        val uid = uid() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            cartsRef(uid).document(restaurantId.toString())
                .collection("items").document(item.productId)
                .update("cantidad", item.cantidad + 1).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun decreaseQuantity(restaurantId: Int, item: CartItem): Result<Unit> {
        val uid = uid() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val cartDoc = cartsRef(uid).document(restaurantId.toString())
            val itemDoc = cartDoc.collection("items").document(item.productId)

            if (item.cantidad <= 1) {
                itemDoc.delete().await()
                // Si era el último item borramos también el documento del carrito
                if (cartDoc.collection("items").get().await().isEmpty) {
                    cartDoc.delete().await()
                }
            } else {
                itemDoc.update("cantidad", item.cantidad - 1).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeItem(restaurantId: Int, item: CartItem): Result<Unit> {
        val uid = uid() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val cartDoc = cartsRef(uid).document(restaurantId.toString())
            val itemDoc = cartDoc.collection("items").document(item.productId)

            itemDoc.delete().await()
            // Limpiamos el carrito del restaurante si se queda sin items
            if (cartDoc.collection("items").get().await().isEmpty) {
                cartDoc.delete().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Vacía todos los carritos del usuario, borrando items y documentos padre
    suspend fun clearCart(): Result<Unit> {
        val uid = uid() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            cartsRef(uid).get().await().documents.forEach { cartDoc ->
                cartDoc.reference.collection("items").get().await()
                    .documents.forEach { it.reference.delete().await() }
                cartDoc.reference.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Igual que clearCart pero solo para un restaurante concreto
    suspend fun clearCartByRestaurant(restaurantId: Int): Result<Unit> {
        val uid = uid() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val cartDoc = cartsRef(uid).document(restaurantId.toString())
            cartDoc.collection("items").get().await()
                .documents.forEach { it.reference.delete().await() }
            cartDoc.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}