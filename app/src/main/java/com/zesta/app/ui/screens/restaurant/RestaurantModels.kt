package com.zesta.app.data.restaurant

data class ProductItem(
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val imageRes: Int
)

data class Restaurant(
    val id: Int,
    val name: String,
    val deliveryInfo: String,
    val rating: String,
    val imageRes: Int,
    val promoText: String? = null,
    val products: List<ProductItem> = emptyList()
)
