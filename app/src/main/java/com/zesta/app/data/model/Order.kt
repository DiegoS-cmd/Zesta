package com.zesta.app.data.model

data class Order(
    val orderId: String = "",
    val restaurantId: Int = 0,
    val restaurantName: String = "",
    val restaurantImageResName: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val promoCode: String = "",
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "completado"
)