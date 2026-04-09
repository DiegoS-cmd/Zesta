package com.zesta.app.data.model

data class CartItem(
    val productId: String = "",
    val restaurantId: Int = 0,
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0,
    val imageKey: String = ""
)
