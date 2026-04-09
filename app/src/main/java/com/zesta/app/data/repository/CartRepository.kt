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

    private fun getUserId(): String? = auth.currentUser?.uid

    private fun getCartsCollection(uid: String) =
        db.collection("users")
            .document(uid)
            .collection("carts")

    suspend fun getRestaurantCarts(): Result<List<RestaurantCartWithItems>> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val cartsSnapshot = getCartsCollection(uid).get().await()

            val results = cartsSnapshot.documents.mapNotNull { cartDoc ->
                val cart = cartDoc.toObject(RestaurantCart::class.java) ?: return@mapNotNull null

                val itemsSnapshot = cartDoc.reference
                    .collection("items")
                    .get()
                    .await()

                val items = itemsSnapshot.documents.mapNotNull { itemDoc ->
                    itemDoc.toObject(CartItem::class.java)
                }

                RestaurantCartWithItems(cart = cart, items = items)
            }.sortedBy { it.cart.restaurantId }

            Result.success(results)
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
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val cartDoc = getCartsCollection(uid).document(restaurantId.toString())
            val itemDoc = cartDoc.collection("items").document(item.productId)

            db.runTransaction { transaction ->
                // Primero TODAS las lecturas
                val cartSnapshot = transaction.get(cartDoc)
                val itemSnapshot = transaction.get(itemDoc)

                // Después TODAS las escrituras
                if (!cartSnapshot.exists()) {
                    transaction.set(
                        cartDoc,
                        RestaurantCart(
                            restaurantId = restaurantId,
                            restaurantName = restaurantName,
                            restaurantImageResName = restaurantImageResName
                        )
                    )
                }

                if (itemSnapshot.exists()) {
                    val currentItem = itemSnapshot.toObject(CartItem::class.java)
                    val nuevaCantidad = (currentItem?.cantidad ?: 0) + 1
                    transaction.update(itemDoc, "cantidad", nuevaCantidad)
                } else {
                    transaction.set(itemDoc, item.copy(cantidad = 1))
                }

                null
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun increaseQuantity(restaurantId: Int, item: CartItem): Result<Unit> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            getCartsCollection(uid)
                .document(restaurantId.toString())
                .collection("items")
                .document(item.productId)
                .update("cantidad", item.cantidad + 1)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun decreaseQuantity(restaurantId: Int, item: CartItem): Result<Unit> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val cartDoc = getCartsCollection(uid).document(restaurantId.toString())
            val itemDoc = cartDoc.collection("items").document(item.productId)

            if (item.cantidad <= 1) {
                itemDoc.delete().await()
                val remainingItems = cartDoc.collection("items").get().await()
                if (remainingItems.isEmpty) {
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
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val cartDoc = getCartsCollection(uid).document(restaurantId.toString())
            val itemDoc = cartDoc.collection("items").document(item.productId)

            itemDoc.delete().await()

            val remainingItems = cartDoc.collection("items").get().await()
            if (remainingItems.isEmpty) {
                cartDoc.delete().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val cartsSnapshot = getCartsCollection(uid).get().await()

            cartsSnapshot.documents.forEach { cartDoc ->
                val itemsSnapshot = cartDoc.reference.collection("items").get().await()
                itemsSnapshot.documents.forEach { itemDoc ->
                    itemDoc.reference.delete().await()
                }
                cartDoc.reference.delete().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun clearCartByRestaurant(restaurantId: Int): Result<Unit> {
        val uid = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val cartDoc = getCartsCollection(uid).document(restaurantId.toString())
            val itemsSnapshot = cartDoc.collection("items").get().await()
            itemsSnapshot.documents.forEach { it.reference.delete().await() }
            cartDoc.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
