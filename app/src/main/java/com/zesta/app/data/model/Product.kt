package com.zesta.app.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class PromoType { NONE, DOS_POR_UNO, DESCUENTO_20, DESCUENTO_10 }
data class Product(
    val id: Int,
    @StringRes val nameRes: Int,
    val price: Double,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int,
    val imageKey: String,
    val promoType: PromoType = PromoType.NONE
)


