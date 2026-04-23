package com.zesta.app.data.repository

import com.zesta.app.R
import com.zesta.app.ui.screens.restaurant.Product
import com.zesta.app.ui.screens.restaurant.PromoType
import com.zesta.app.ui.screens.restaurant.Restaurant

object RestaurantRepository {

    private val allRestaurants = listOf(
        Restaurant(
            id = 1, nameRes = R.string.restaurante_nombre_burger_king,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 20,
            ratingValue = 4.4, ratingCount = 170, imageRes = R.drawable.bk,
            categories = listOf("Hamburguesas"),
            latitude = 40.4200, longitude = -3.7056, // BK Gran Vía
            products = listOf(
                Product(id = 1, nameRes = R.string.producto_whopper, price = 8.25, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.whopper, imageKey = "whopper"),
                Product(id = 2, nameRes = R.string.producto_chili_cheese, price = 10.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.chilicheese, imageKey = "chilicheese"),
                Product(id = 3, nameRes = R.string.producto_big_mac, price = 7.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.crazybacon, imageKey = "crazybacon"),
                Product(id = 4, nameRes = R.string.producto_combo_wings, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.burgerkingicono, imageKey = "burgerkingicono")
            )
        ),
        Restaurant(
            id = 2, nameRes = R.string.restaurante_nombre_dominos,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 25,
            ratingValue = 4.2, ratingCount = 320, imageRes = R.drawable.dominos,
            categories = listOf("Pizzas"),
            latitude = 40.4153, longitude = -3.6941, // Domino's Lavapiés
            products = listOf(
                Product(id = 5, nameRes = R.string.producto_pizza_carbonara, price = 12.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dominos, imageKey = "dominos"),
                Product(id = 6, nameRes = R.string.producto_pizza_pepperoni, price = 13.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dominos, imageKey = "dominos"),
                Product(id = 7, nameRes = R.string.producto_pizza_4quesos, price = 11.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dominos, imageKey = "dominos"),
                Product(id = 8, nameRes = R.string.producto_pasta_carbonara, price = 8.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dominos, imageKey = "dominos")
            )
        ),
        Restaurant(
            id = 3, nameRes = R.string.restaurante_nombre_wingstop,
            hasFreeDelivery = false, deliveryFee = 2.50, deliveryTimeMinutes = 20,
            ratingValue = 4.8, ratingCount = 70, imageRes = R.drawable.wingstop,
            promoTextRes = R.string.promocion_compra_una_llevate_otra,
            categories = listOf("Hamburguesas", "Americana"),
            latitude = 40.4225, longitude = -3.7015, // Wingstop Gran Vía
            products = listOf(
                Product(id = 9, nameRes = R.string.producto_alitas_bbq, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstop, imageKey = "wingstop", promoType = PromoType.DOS_POR_UNO),
                Product(id = 10, nameRes = R.string.producto_combo_wings, price = 12.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstop, imageKey = "wingstop", promoType = PromoType.DOS_POR_UNO),
                Product(id = 11, nameRes = R.string.producto_alitas_kfc, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstop, imageKey = "wingstop"),
                Product(id = 12, nameRes = R.string.producto_patatas_cajun, price = 4.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.wingstop, imageKey = "wingstop")
            )
        ),
        Restaurant(
            id = 4, nameRes = R.string.restaurante_nombre_mcdonalds,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 15,
            ratingValue = 4.1, ratingCount = 512, imageRes = R.drawable.mcdonalds,
            promoTextRes = R.string.promocion_2x1,
            categories = listOf("Hamburguesas"),
            latitude = 40.4172, longitude = -3.7040, // McD Puerta del Sol
            products = listOf(
                Product(id = 13, nameRes = R.string.producto_big_mac, price = 7.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcdonalds, imageKey = "mcdonalds", promoType = PromoType.DOS_POR_UNO),
                Product(id = 14, nameRes = R.string.producto_mcnuggets, price = 6.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcdonalds, imageKey = "mcdonalds", promoType = PromoType.DOS_POR_UNO),
                Product(id = 15, nameRes = R.string.producto_mcflurry, price = 3.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcdonalds, imageKey = "mcdonalds"),
                Product(id = 16, nameRes = R.string.producto_whopper, price = 8.25, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.mcdonalds, imageKey = "mcdonalds")
            )
        ),
        Restaurant(
            id = 5, nameRes = R.string.restaurante_nombre_kfc,
            hasFreeDelivery = false, deliveryFee = 1.99, deliveryTimeMinutes = 25,
            ratingValue = 4.3, ratingCount = 248, imageRes = R.drawable.kfc,
            categories = listOf("Hamburguesas", "Americana"),
            latitude = 40.4191, longitude = -3.7072, // KFC Callao
            products = listOf(
                Product(id = 17, nameRes = R.string.producto_bucket_kfc, price = 14.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfc, imageKey = "kfc"),
                Product(id = 18, nameRes = R.string.producto_twister_kfc, price = 6.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfc, imageKey = "kfc"),
                Product(id = 19, nameRes = R.string.producto_alitas_kfc, price = 8.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfc, imageKey = "kfc"),
                Product(id = 20, nameRes = R.string.producto_combo_wings, price = 11.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.kfc, imageKey = "kfc")
            )
        ),
        Restaurant(
            id = 6, nameRes = R.string.restaurante_nombre_subway,
            hasFreeDelivery = true, deliveryFee = 2.99, deliveryTimeMinutes = 20,
            ratingValue = 4.0, ratingCount = 189, imageRes = R.drawable.subway,
            promoTextRes = R.string.promocion_envio_gratis,
            categories = listOf("Panadería"),
            latitude = 40.4165, longitude = -3.7026, // Subway Sol
            products = listOf(
                Product(id = 21, nameRes = R.string.producto_sub_italiano, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subway, imageKey = "subway"),
                Product(id = 22, nameRes = R.string.producto_sub_pollo, price = 8.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subway, imageKey = "subway"),
                Product(id = 23, nameRes = R.string.producto_sub_veggie, price = 6.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subway, imageKey = "subway"),
                Product(id = 24, nameRes = R.string.producto_club_sandwich, price = 9.25, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.subway, imageKey = "subway")
            )
        ),
        Restaurant(
            id = 7, nameRes = R.string.restaurante_nombre_telepizza,
            hasFreeDelivery = false, deliveryFee = 2.99, deliveryTimeMinutes = 30,
            ratingValue = 3.9, ratingCount = 410, imageRes = R.drawable.telepizza,
            categories = listOf("Pizzas"),
            latitude = 40.4089, longitude = -3.6920, // Telepizza Atocha
            products = listOf(
                Product(id = 25, nameRes = R.string.producto_pizza_barbacoa, price = 11.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizza, imageKey = "telepizza"),
                Product(id = 26, nameRes = R.string.producto_pizza_4quesos, price = 12.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizza, imageKey = "telepizza"),
                Product(id = 27, nameRes = R.string.producto_pizza_carbonara, price = 13.25, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizza, imageKey = "telepizza"),
                Product(id = 28, nameRes = R.string.producto_pasta_carbonara, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.telepizza, imageKey = "telepizza")
            )
        ),
        Restaurant(
            id = 8, nameRes = R.string.restaurante_nombre_papajohns,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 28,
            ratingValue = 4.2, ratingCount = 155, imageRes = R.drawable.papajohns,
            promoTextRes = R.string.promocion_descuento_20,
            categories = listOf("Pizzas"),
            latitude = 40.4230, longitude = -3.6890, // Papa Johns Salamanca
            products = listOf(
                Product(id = 29, nameRes = R.string.producto_pizza_garden, price = 12.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papajohns, imageKey = "papajohns", promoType = PromoType.DESCUENTO_20),
                Product(id = 30, nameRes = R.string.producto_pizza_hawaiana, price = 11.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papajohns, imageKey = "papajohns", promoType = PromoType.DESCUENTO_20),
                Product(id = 31, nameRes = R.string.producto_papas_ajo, price = 4.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papajohns, imageKey = "papajohns"),
                Product(id = 32, nameRes = R.string.producto_pizza_pepperoni, price = 13.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.papajohns, imageKey = "papajohns")
            )
        ),
        Restaurant(
            id = 9, nameRes = R.string.restaurante_nombre_tacobell,
            hasFreeDelivery = false, deliveryFee = 1.50, deliveryTimeMinutes = 22,
            ratingValue = 4.5, ratingCount = 93, imageRes = R.drawable.tacobell,
            categories = listOf("Mexicana"),
            latitude = 40.4183, longitude = -3.7059, // Taco Bell Gran Vía
            products = listOf(
                Product(id = 33, nameRes = R.string.producto_crunchwrap, price = 5.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacobell, imageKey = "tacobell"),
                Product(id = 34, nameRes = R.string.producto_nachos_bell, price = 6.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacobell, imageKey = "tacobell"),
                Product(id = 35, nameRes = R.string.producto_quesarito, price = 4.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacobell, imageKey = "tacobell"),
                Product(id = 36, nameRes = R.string.producto_combo_wings, price = 8.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.tacobell, imageKey = "tacobell")
            )
        ),
        Restaurant(
            id = 10, nameRes = R.string.restaurante_nombre_dunkin,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 18,
            ratingValue = 4.0, ratingCount = 207, imageRes = R.drawable.dunkin,
            promoTextRes = R.string.promocion_envio_gratis,
            categories = listOf("Desayuno", "Panadería"),
            latitude = 40.4175, longitude = -3.7010, // Dunkin Sol
            products = listOf(
                Product(id = 37, nameRes = R.string.producto_donut_clasico, price = 2.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkin, imageKey = "dunkin"),
                Product(id = 38, nameRes = R.string.producto_cafe_americano, price = 3.20, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkin, imageKey = "dunkin"),
                Product(id = 39, nameRes = R.string.producto_bagel_jamon, price = 4.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkin, imageKey = "dunkin"),
                Product(id = 40, nameRes = R.string.producto_mcflurry, price = 3.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.dunkin, imageKey = "dunkin")
            )
        ),
        Restaurant(
            id = 11, nameRes = R.string.restaurante_nombre_starbucks,
            hasFreeDelivery = false, deliveryFee = 2.50, deliveryTimeMinutes = 20,
            ratingValue = 4.6, ratingCount = 341, imageRes = R.drawable.starbucks,
            categories = listOf("Desayuno", "Panadería"),
            latitude = 40.4195, longitude = -3.6883, // Starbucks Recoletos
            products = listOf(
                Product(id = 41, nameRes = R.string.producto_frappuccino, price = 6.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucks, imageKey = "starbucks"),
                Product(id = 42, nameRes = R.string.producto_latte, price = 4.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucks, imageKey = "starbucks"),
                Product(id = 43, nameRes = R.string.producto_croissant, price = 3.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucks, imageKey = "starbucks"),
                Product(id = 44, nameRes = R.string.producto_cafe_americano, price = 3.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.starbucks, imageKey = "starbucks")
            )
        ),
        Restaurant(
            id = 12, nameRes = R.string.restaurante_nombre_fiveguys,
            hasFreeDelivery = false, deliveryFee = 3.50, deliveryTimeMinutes = 30,
            ratingValue = 4.7, ratingCount = 118, imageRes = R.drawable.fiveguys,
            promoTextRes = R.string.promocion_2x1,
            categories = listOf("Hamburguesas"),
            latitude = 40.4198, longitude = -3.6915, // Five Guys Serrano
            products = listOf(
                Product(id = 45, nameRes = R.string.producto_burger_fiveguys, price = 12.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguys, imageKey = "fiveguys", promoType = PromoType.DOS_POR_UNO),
                Product(id = 46, nameRes = R.string.producto_hotdog_fiveguys, price = 8.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguys, imageKey = "fiveguys", promoType = PromoType.DOS_POR_UNO),
                Product(id = 47, nameRes = R.string.producto_patatas_fiveguys, price = 5.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguys, imageKey = "fiveguys"),
                Product(id = 48, nameRes = R.string.producto_smash_goiko, price = 13.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fiveguys, imageKey = "fiveguys")
            )
        ),
        Restaurant(
            id = 13, nameRes = R.string.restaurante_nombre_fosters,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 35,
            ratingValue = 4.3, ratingCount = 86, imageRes = R.drawable.fosters,
            categories = listOf("Hamburguesas", "Americana"),
            latitude = 40.4143, longitude = -3.7008, // Foster's Hollywood Huertas
            products = listOf(
                Product(id = 49, nameRes = R.string.producto_costillas_fosters, price = 16.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosters, imageKey = "fosters"),
                Product(id = 50, nameRes = R.string.producto_burger_fosters, price = 11.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosters, imageKey = "fosters"),
                Product(id = 51, nameRes = R.string.producto_nachos_fosters, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosters, imageKey = "fosters"),
                Product(id = 52, nameRes = R.string.producto_club_sandwich, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.fosters, imageKey = "fosters")
            )
        ),
        Restaurant(
            id = 14, nameRes = R.string.restaurante_nombre_ginos,
            hasFreeDelivery = false, deliveryFee = 1.99, deliveryTimeMinutes = 28,
            ratingValue = 4.1, ratingCount = 143, imageRes = R.drawable.ginos,
            promoTextRes = R.string.promocion_descuento_20,
            categories = listOf("Pizzas"),
            latitude = 40.4160, longitude = -3.7070, // Ginos Callao
            products = listOf(
                Product(id = 53, nameRes = R.string.producto_pizza_ginos, price = 13.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginos, imageKey = "ginos", promoType = PromoType.DESCUENTO_20),
                Product(id = 54, nameRes = R.string.producto_pasta_ginos, price = 10.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginos, imageKey = "ginos", promoType = PromoType.DESCUENTO_20),
                Product(id = 55, nameRes = R.string.producto_tiramisu, price = 5.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginos, imageKey = "ginos"),
                Product(id = 56, nameRes = R.string.producto_pizza_4quesos, price = 12.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.ginos, imageKey = "ginos")
            )
        ),
        Restaurant(
            id = 15, nameRes = R.string.restaurante_nombre_vips,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 25,
            ratingValue = 4.2, ratingCount = 201, imageRes = R.drawable.vips,
            categories = listOf("Desayuno", "Americana"),
            latitude = 40.4210, longitude = -3.6855, // VIPS Goya
            products = listOf(
                Product(id = 57, nameRes = R.string.producto_club_sandwich, price = 10.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vips, imageKey = "vips"),
                Product(id = 58, nameRes = R.string.producto_pancakes_vips, price = 8.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vips, imageKey = "vips"),
                Product(id = 59, nameRes = R.string.producto_cheesecake_vips, price = 5.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vips, imageKey = "vips"),
                Product(id = 60, nameRes = R.string.producto_cafe_americano, price = 3.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.vips, imageKey = "vips")
            )
        ),
        Restaurant(
            id = 16, nameRes = R.string.restaurante_nombre_montaditos,
            hasFreeDelivery = false, deliveryFee = 0.99, deliveryTimeMinutes = 20,
            ratingValue = 4.4, ratingCount = 377, imageRes = R.drawable.montaditos,
            promoTextRes = R.string.promocion_2x1,
            categories = listOf("Panadería"),
            latitude = 40.4130, longitude = -3.7053, // Montaditos Huertas
            products = listOf(
                Product(id = 61, nameRes = R.string.producto_montadito_lomo, price = 1.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.montaditos, imageKey = "montaditos", promoType = PromoType.DOS_POR_UNO),
                Product(id = 62, nameRes = R.string.producto_montadito_tortilla, price = 1.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.montaditos, imageKey = "montaditos", promoType = PromoType.DOS_POR_UNO),
                Product(id = 63, nameRes = R.string.producto_combo_montaditos, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.montaditos, imageKey = "montaditos"),
                Product(id = 64, nameRes = R.string.producto_papas_ajo, price = 3.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.montaditos, imageKey = "montaditos")
            )
        ),
        Restaurant(
            id = 17, nameRes = R.string.restaurante_nombre_goiko,
            hasFreeDelivery = false, deliveryFee = 2.99, deliveryTimeMinutes = 30,
            ratingValue = 4.7, ratingCount = 132, imageRes = R.drawable.goiko,
            categories = listOf("Hamburguesas"),
            latitude = 40.4255, longitude = -3.6897, // Goiko Grill Velázquez
            products = listOf(
                Product(id = 65, nameRes = R.string.producto_burger_goiko, price = 13.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goiko, imageKey = "goiko"),
                Product(id = 66, nameRes = R.string.producto_smash_goiko, price = 12.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goiko, imageKey = "goiko"),
                Product(id = 67, nameRes = R.string.producto_patatas_goiko, price = 5.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goiko, imageKey = "goiko"),
                Product(id = 68, nameRes = R.string.producto_nachos_fosters, price = 7.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.goiko, imageKey = "goiko")
            )
        ),
        Restaurant(
            id = 18, nameRes = R.string.restaurante_nombre_sushishop,
            hasFreeDelivery = true, deliveryFee = null, deliveryTimeMinutes = 35,
            ratingValue = 4.6, ratingCount = 89, imageRes = R.drawable.sushishop,
            promoTextRes = R.string.promocion_envio_gratis,
            categories = listOf("Asiática"),
            latitude = 40.4220, longitude = -3.6930, // Sushi Shop Salamanca
            products = listOf(
                Product(id = 69, nameRes = R.string.producto_california_roll, price = 9.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushishop, imageKey = "sushishop"),
                Product(id = 70, nameRes = R.string.producto_salmon_nigiri, price = 7.50, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushishop, imageKey = "sushishop"),
                Product(id = 71, nameRes = R.string.producto_combo_sushi, price = 18.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushishop, imageKey = "sushishop"),
                Product(id = 72, nameRes = R.string.producto_sub_veggie, price = 6.95, descriptionRes = R.string.producto_descripcion_generica, imageRes = R.drawable.sushishop, imageKey = "sushishop")
            )
        )
    )

    fun getByCategory(category: String): List<Restaurant> =
        allRestaurants.filter { it.categories.any { cat -> cat.equals(category, ignoreCase = true) } }
    fun getExploreRestaurants(): List<Restaurant> {
        val excludedIds = getFeaturedRestaurants().map { it.id }
        return allRestaurants.filter { it.id !in excludedIds }
    }
    fun getAllRestaurants(): List<Restaurant> = allRestaurants
    fun getRestaurantById(id: Int): Restaurant? = allRestaurants.find { it.id == id }
    fun getFeaturedRestaurants(): List<Restaurant> = allRestaurants.take(6)
    fun getPromoRestaurants(): List<Restaurant> = allRestaurants.filter { it.promoTextRes != null }
    fun searchRestaurants(query: String, resolveName: (Int) -> String): List<Restaurant> {
        if (query.isBlank()) return allRestaurants
        return allRestaurants.filter { resolveName(it.nameRes).contains(query, ignoreCase = true) }
    }
}