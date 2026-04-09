package com.zesta.app.data.restaurant

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Product(
    val id: Int,
    @StringRes val nameRes: Int,
    val price: Double,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int,
    val imageKey: String
)
