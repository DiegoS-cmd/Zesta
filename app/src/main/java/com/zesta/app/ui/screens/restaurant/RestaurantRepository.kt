package com.zesta.app.data.restaurant

import com.zesta.app.R

object RestaurantRepository {

    private val allRestaurants = listOf(

        // ── RESTAURANTES ORIGINALES ──────────────────────────────────────

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
                Product(id = 1, nameRes = R.string.producto_whopper, price = 8.25,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.whopper),
                Product(id = 2, nameRes = R.string.producto_chili_cheese, price = 10.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.chilicheese),
                Product(id = 3, nameRes = R.string.producto_big_mac, price = 7.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.crazybacon),
                Product(id = 4, nameRes = R.string.producto_combo_wings, price = 9.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.burgerkingicono)
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
                Product(id = 5, nameRes = R.string.producto_pizza_carbonara, price = 12.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dominos),
                Product(id = 6, nameRes = R.string.producto_pizza_pepperoni, price = 13.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dominos),
                Product(id = 7, nameRes = R.string.producto_pizza_4quesos, price = 11.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dominos),
                Product(id = 8, nameRes = R.string.producto_pasta_carbonara, price = 8.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dominos)
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
                Product(id = 9, nameRes = R.string.producto_alitas_bbq, price = 9.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstop),
                Product(id = 10, nameRes = R.string.producto_combo_wings, price = 12.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstop),
                Product(id = 11, nameRes = R.string.producto_alitas_kfc, price = 7.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstop),
                Product(id = 12, nameRes = R.string.producto_patatas_cajun, price = 4.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstop)
            )
        ),

        // ── RESTAURANTES NUEVOS ──────────────────────────────────────────

        Restaurant(
            id = 4,
            nameRes = R.string.restaurante_nombre_mcdonalds,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 15,
            ratingValue = 4.1,
            ratingCount = 512,
            imageRes = R.drawable.mcdonalds,
            promoTextRes = R.string.promocion_2x1,
            products = listOf(
                Product(id = 13, nameRes = R.string.producto_big_mac, price = 7.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcdonalds),
                Product(id = 14, nameRes = R.string.producto_mcnuggets, price = 6.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcdonalds),
                Product(id = 15, nameRes = R.string.producto_mcflurry, price = 3.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcdonalds),
                Product(id = 16, nameRes = R.string.producto_whopper, price = 8.25,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcdonalds)
            )
        ),

        Restaurant(
            id = 5,
            nameRes = R.string.restaurante_nombre_kfc,
            hasFreeDelivery = false,
            deliveryFee = 1.99,
            deliveryTimeMinutes = 25,
            ratingValue = 4.3,
            ratingCount = 248,
            imageRes = R.drawable.kfc,
            products = listOf(
                Product(id = 17, nameRes = R.string.producto_bucket_kfc, price = 14.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfc),
                Product(id = 18, nameRes = R.string.producto_twister_kfc, price = 6.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfc),
                Product(id = 19, nameRes = R.string.producto_alitas_kfc, price = 8.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfc),
                Product(id = 20, nameRes = R.string.producto_combo_wings, price = 11.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfc)
            )
        ),

        Restaurant(
            id = 6,
            nameRes = R.string.restaurante_nombre_subway,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 20,
            ratingValue = 4.0,
            ratingCount = 189,
            imageRes = R.drawable.subway,
            promoTextRes = R.string.promocion_envio_gratis,
            products = listOf(
                Product(id = 21, nameRes = R.string.producto_sub_italiano, price = 7.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subway),
                Product(id = 22, nameRes = R.string.producto_sub_pollo, price = 8.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subway),
                Product(id = 23, nameRes = R.string.producto_sub_veggie, price = 6.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subway),
                Product(id = 24, nameRes = R.string.producto_club_sandwich, price = 9.25,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subway)
            )
        ),

        Restaurant(
            id = 7,
            nameRes = R.string.restaurante_nombre_telepizza,
            hasFreeDelivery = false,
            deliveryFee = 2.99,
            deliveryTimeMinutes = 30,
            ratingValue = 3.9,
            ratingCount = 410,
            imageRes = R.drawable.telepizza,
            products = listOf(
                Product(id = 25, nameRes = R.string.producto_pizza_barbacoa, price = 11.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizza),
                Product(id = 26, nameRes = R.string.producto_pizza_4quesos, price = 12.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizza),
                Product(id = 27, nameRes = R.string.producto_pizza_carbonara, price = 13.25,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizza),
                Product(id = 28, nameRes = R.string.producto_pasta_carbonara, price = 7.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizza)
            )
        ),

        Restaurant(
            id = 8,
            nameRes = R.string.restaurante_nombre_papajohns,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 28,
            ratingValue = 4.2,
            ratingCount = 155,
            imageRes = R.drawable.papajohns,
            promoTextRes = R.string.promocion_descuento_20,
            products = listOf(
                Product(id = 29, nameRes = R.string.producto_pizza_garden, price = 12.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papajohns),
                Product(id = 30, nameRes = R.string.producto_pizza_hawaiana, price = 11.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papajohns),
                Product(id = 31, nameRes = R.string.producto_papas_ajo, price = 4.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papajohns),
                Product(id = 32, nameRes = R.string.producto_pizza_pepperoni, price = 13.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papajohns)
            )
        ),

        Restaurant(
            id = 9,
            nameRes = R.string.restaurante_nombre_tacobell,
            hasFreeDelivery = false,
            deliveryFee = 1.50,
            deliveryTimeMinutes = 22,
            ratingValue = 4.5,
            ratingCount = 93,
            imageRes = R.drawable.tacobell,
            products = listOf(
                Product(id = 33, nameRes = R.string.producto_crunchwrap, price = 5.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacobell),
                Product(id = 34, nameRes = R.string.producto_nachos_bell, price = 6.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacobell),
                Product(id = 35, nameRes = R.string.producto_quesarito, price = 4.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacobell),
                Product(id = 36, nameRes = R.string.producto_combo_wings, price = 8.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacobell)
            )
        ),

        Restaurant(
            id = 10,
            nameRes = R.string.restaurante_nombre_dunkin,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 18,
            ratingValue = 4.0,
            ratingCount = 207,
            imageRes = R.drawable.dunkin,
            promoTextRes = R.string.promocion_envio_gratis,
            products = listOf(
                Product(id = 37, nameRes = R.string.producto_donut_clasico, price = 2.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkin),
                Product(id = 38, nameRes = R.string.producto_cafe_americano, price = 3.20,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkin),
                Product(id = 39, nameRes = R.string.producto_bagel_jamon, price = 4.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkin),
                Product(id = 40, nameRes = R.string.producto_mcflurry, price = 3.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkin)
            )
        ),

        Restaurant(
            id = 11,
            nameRes = R.string.restaurante_nombre_starbucks,
            hasFreeDelivery = false,
            deliveryFee = 2.50,
            deliveryTimeMinutes = 20,
            ratingValue = 4.6,
            ratingCount = 341,
            imageRes = R.drawable.starbucks,
            products = listOf(
                Product(id = 41, nameRes = R.string.producto_frappuccino, price = 6.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucks),
                Product(id = 42, nameRes = R.string.producto_latte, price = 4.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucks),
                Product(id = 43, nameRes = R.string.producto_croissant, price = 3.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucks),
                Product(id = 44, nameRes = R.string.producto_cafe_americano, price = 3.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucks)
            )
        ),

        Restaurant(
            id = 12,
            nameRes = R.string.restaurante_nombre_fiveguys,
            hasFreeDelivery = false,
            deliveryFee = 3.50,
            deliveryTimeMinutes = 30,
            ratingValue = 4.7,
            ratingCount = 118,
            imageRes = R.drawable.fiveguys,
            promoTextRes = R.string.promocion_2x1,
            products = listOf(
                Product(id = 45, nameRes = R.string.producto_burger_fiveguys, price = 12.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguys),
                Product(id = 46, nameRes = R.string.producto_hotdog_fiveguys, price = 8.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguys),
                Product(id = 47, nameRes = R.string.producto_patatas_fiveguys, price = 5.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguys),
                Product(id = 48, nameRes = R.string.producto_smash_goiko, price = 13.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguys)
            )
        ),

        Restaurant(
            id = 13,
            nameRes = R.string.restaurante_nombre_fosters,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 35,
            ratingValue = 4.3,
            ratingCount = 86,
            imageRes = R.drawable.fosters,
            products = listOf(
                Product(id = 49, nameRes = R.string.producto_costillas_fosters, price = 16.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosters),
                Product(id = 50, nameRes = R.string.producto_burger_fosters, price = 11.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosters),
                Product(id = 51, nameRes = R.string.producto_nachos_fosters, price = 7.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosters),
                Product(id = 52, nameRes = R.string.producto_club_sandwich, price = 9.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosters)
            )
        ),

        Restaurant(
            id = 14,
            nameRes = R.string.restaurante_nombre_ginos,
            hasFreeDelivery = false,
            deliveryFee = 1.99,
            deliveryTimeMinutes = 28,
            ratingValue = 4.1,
            ratingCount = 143,
            imageRes = R.drawable.ginos,
            promoTextRes = R.string.promocion_descuento_20,
            products = listOf(
                Product(id = 53, nameRes = R.string.producto_pizza_ginos, price = 13.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginos),
                Product(id = 54, nameRes = R.string.producto_pasta_ginos, price = 10.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginos),
                Product(id = 55, nameRes = R.string.producto_tiramisú, price = 5.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginos),
                Product(id = 56, nameRes = R.string.producto_pizza_4quesos, price = 12.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginos)
            )
        ),

        Restaurant(
            id = 15,
            nameRes = R.string.restaurante_nombre_vips,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 25,
            ratingValue = 4.2,
            ratingCount = 201,
            imageRes = R.drawable.vips,
            products = listOf(
                Product(id = 57, nameRes = R.string.producto_club_sandwich, price = 10.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vips),
                Product(id = 58, nameRes = R.string.producto_pancakes_vips, price = 8.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vips),
                Product(id = 59, nameRes = R.string.producto_cheesecake_vips, price = 5.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vips),
                Product(id = 60, nameRes = R.string.producto_cafe_americano, price = 3.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vips)
            )
        ),

        Restaurant(
            id = 16,
            nameRes = R.string.restaurante_nombre_montaditos,
            hasFreeDelivery = false,
            deliveryFee = 0.99,
            deliveryTimeMinutes = 20,
            ratingValue = 4.4,
            ratingCount = 377,
            imageRes = R.drawable.montaditos,
            promoTextRes = R.string.promocion_2x1,
            products = listOf(
                Product(id = 61, nameRes = R.string.producto_montadito_lomo, price = 1.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.montaditos),
                Product(id = 62, nameRes = R.string.producto_montadito_tortilla, price = 1.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.montaditos),
                Product(id = 63, nameRes = R.string.producto_combo_montaditos, price = 9.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.montaditos),
                Product(id = 64, nameRes = R.string.producto_papas_ajo, price = 3.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.montaditos)
            )
        ),

        Restaurant(
            id = 17,
            nameRes = R.string.restaurante_nombre_goiko,
            hasFreeDelivery = false,
            deliveryFee = 2.99,
            deliveryTimeMinutes = 30,
            ratingValue = 4.7,
            ratingCount = 132,
            imageRes = R.drawable.goiko,
            products = listOf(
                Product(id = 65, nameRes = R.string.producto_burger_goiko, price = 13.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goiko),
                Product(id = 66, nameRes = R.string.producto_smash_goiko, price = 12.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goiko),
                Product(id = 67, nameRes = R.string.producto_patatas_goiko, price = 5.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goiko),
                Product(id = 68, nameRes = R.string.producto_nachos_fosters, price = 7.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goiko)
            )
        ),

        Restaurant(
            id = 18,
            nameRes = R.string.restaurante_nombre_sushishop,
            hasFreeDelivery = true,
            deliveryFee = null,
            deliveryTimeMinutes = 35,
            ratingValue = 4.6,
            ratingCount = 89,
            imageRes = R.drawable.sushishop,
            promoTextRes = R.string.promocion_envio_gratis,
            products = listOf(
                Product(id = 69, nameRes = R.string.producto_california_roll, price = 9.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushishop),
                Product(id = 70, nameRes = R.string.producto_salmon_nigiri, price = 7.50,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushishop),
                Product(id = 71, nameRes = R.string.producto_combo_sushi, price = 18.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushishop),
                Product(id = 72, nameRes = R.string.producto_sub_veggie, price = 6.95,
                    descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushishop)
            )
        )
    )


    fun getAllRestaurants(): List<Restaurant> = allRestaurants

    fun getRestaurantById(id: Int): Restaurant? = allRestaurants.find { it.id == id }

    fun getFeaturedRestaurants(): List<Restaurant> = allRestaurants.take(5)

    fun getExploreRestaurants(): List<Restaurant> {
        val excludedIds = getFeaturedRestaurants().map { it.id }
        return allRestaurants.filter { it.id !in excludedIds }
    }

    fun getPromoRestaurants(): List<Restaurant> = allRestaurants.filter { it.promoTextRes != null }

    fun searchRestaurants(query: String, resolveName: (Int) -> String): List<Restaurant> {
        if (query.isBlank()) return allRestaurants
        return allRestaurants.filter { restaurant ->
            resolveName(restaurant.nameRes).contains(query, ignoreCase = true)
        }
    }
}