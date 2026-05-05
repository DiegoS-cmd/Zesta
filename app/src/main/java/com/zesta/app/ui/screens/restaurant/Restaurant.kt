package com.zesta.app.ui.screens.restaurant

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zesta.app.data.model.Product

data class Restaurant(
    val id: Int,
    @StringRes val nameRes: Int,
    val hasFreeDelivery: Boolean,
    val deliveryFee: Double?,
    val deliveryTimeMinutes: Int,
    val ratingValue: Double,
    val ratingCount: Int,
    @DrawableRes val imageRes: Int,
    @StringRes val promoTextRes: Int? = null,
    val promoDiscount: Double? = null,
    val categories: List<String> = emptyList(),
    val products: List<Product>,
    val addressRes: Int,
)