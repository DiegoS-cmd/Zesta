package com.zesta.app.data.restaurant

import com.zesta.app.R

object RestaurantRepository {

    private val allRestaurants = listOf(
        Restaurant(
            id = 1,
            nameRes = R.string.restaurante_nombre_burger_king,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 20,
            ratingValue = 4.4,
            ratingCount = 170,
            imageRes = R.drawable.bk,
            products = listOf(
                Product(
                    id = 1,
                    nameRes = R.string.producto_whopper,
                    price = 8.25,
                    descriptionRes = R.string.producto_descripcion_generica,
                    imageRes = R.drawable.whopper
                ),
                Product(
                    id = 2,
                    nameRes = R.string.producto_chili_cheese,
                    price = 10.50,
                    descriptionRes = R.string.producto_descripcion_generica,
                    imageRes = R.drawable.chilicheese
                )
            )
        ),
        Restaurant(
            id = 2,
            nameRes = R.string.restaurante_nombre_dominos,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 25,
            ratingValue = 4.2,
            ratingCount = 320,
            imageRes = R.drawable.dominos,
            products = listOf(
                Product(
                    id = 3,
                    nameRes = R.string.producto_pizza_carbonara,
                    price = 12.95,
                    descriptionRes = R.string.producto_descripcion_generica,
                    imageRes = R.drawable.dominos
                ),
                Product(
                    id = 4,
                    nameRes = R.string.producto_pizza_pepperoni,
                    price = 13.50,
                    descriptionRes = R.string.producto_descripcion_generica,
                    imageRes = R.drawable.dominos
                )
            )
        ),
        Restaurant(
            id = 3,
            nameRes = R.string.restaurante_nombre_wingstop,
            hasFreeDelivery = false,
            deliveryFee = 2.50,
            deliveryTimeMinutes = 20,
            ratingValue = 4.8,
            ratingCount = 70,
            imageRes = R.drawable.wingstop,
            promoTextRes = R.string.promocion_compra_una_llevate_otra,
            products = listOf(
                Product(
                    id = 5,
                    nameRes = R.string.producto_alitas_bbq,
                    price = 9.95,
                    descriptionRes = R.string.producto_descripcion_generica,
                    imageRes = R.drawable.wingstop
                ),
                Product(
                    id = 6,
                    nameRes = R.string.producto_combo_wings,
                    price = 12.50,
                    descriptionRes = R.string.producto_descripcion_generica,
                    imageRes = R.drawable.wingstop
                )
            )
        )
    )

    fun getExploreRestaurants(): List<Restaurant> {
        val excludedIds = getFeaturedRestaurants().map { it.id } + getPromoRestaurants().map { it.id }
        return allRestaurants.filter { it.id !in excludedIds }
    }

    fun getAllRestaurants(): List<Restaurant> = allRestaurants

    fun getRestaurantById(id: Int): Restaurant? = allRestaurants.find { it.id == id }

    fun getFeaturedRestaurants(): List<Restaurant> = allRestaurants.take(6)

    fun getPromoRestaurants(): List<Restaurant> = allRestaurants.filter { it.promoTextRes != null }

    fun searchRestaurants(query: String, resolveName: (Int) -> String): List<Restaurant> {
        if (query.isBlank()) return allRestaurants

        return allRestaurants.filter { restaurant ->
            resolveName(restaurant.nameRes).contains(query, ignoreCase = true)
        }
    }
}
