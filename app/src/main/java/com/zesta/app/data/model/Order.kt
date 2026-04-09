package com.zesta.app.data.model

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",
    val restaurantId: Int = 0,
    val restaurantName: String = "",
    val restaurantImageResName: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val serviceFee: Double = 2.50,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val promoCode: String? = null,
    val address: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val status: String = "completado"
)