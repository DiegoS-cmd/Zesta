package com.zesta.app.data.restaurant

import com.zesta.app.R

object RestaurantRepository {

    private val allRestaurants = listOf(
        Restaurant(
            id = 1,
            name = "Burger King",
            deliveryInfo = "Sin gastos de envío - 20 min",
            rating = "4.4 (170+)",
            imageRes = R.drawable.bk,
            products = listOf(
                ProductItem(1, "Whopper", "8,25€", "Descripcion", R.drawable.whopper),
                ProductItem(2, "Chili Cheese", "10,50€", "Descripcion", R.drawable.chilicheese)
            )
        ),
        Restaurant(
            id = 2,
            name = "Domino's Pizza",
            deliveryInfo = "Sin gastos de envío - 25 min",
            rating = "4.2 (320+)",
            imageRes = R.drawable.dominos,
            products = listOf(
                ProductItem(3, "Pizza Carbonara", "12,95€", "Descripcion", R.drawable.dominos),
                ProductItem(4, "Pizza Pepperoni", "13,50€", "Descripcion", R.drawable.dominos)
            )
        ),
        Restaurant(
            id = 3,
            name = "Wingstop",
            deliveryInfo = "Gastos de envío a 2,50 - 20 min",
            rating = "4.8 (70+)",
            imageRes = R.drawable.wingstop,
            promoText = "Compra 1 y consigue 1 gratis",
            products = listOf(
                ProductItem(5, "Alitas BBQ", "9,95€", "Descripcion", R.drawable.wingstop),
                ProductItem(6, "Combo Wings", "12,50€", "Descripcion", R.drawable.wingstop)
            )
        ),
        Restaurant(
            id = 4,
            name = "Pans & Company",
            deliveryInfo = "Gastos de envío a 2,90 - 20 min",
            rating = "3.9 (270+)",
            imageRes = R.drawable.pans,
            promoText = "Compra 1 y consigue 1 gratis",
            products = listOf(
                ProductItem(7, "Bocadillo Pollo", "7,95€", "Descripcion", R.drawable.pans),
                ProductItem(8, "Menu Pans", "10,20€", "Descripcion", R.drawable.pans)
            )
        ),
        Restaurant(
            id = 5,
            name = "McDonald's",
            deliveryInfo = "Sin gastos de envío - 18 min",
            rating = "4.3 (410+)",
            imageRes = R.drawable.mcdonalds,
            products = listOf(
                ProductItem(9, "Big Mac", "8,95€", "Descripcion", R.drawable.mcdonalds),
                ProductItem(10, "McChicken", "8,50€", "Descripcion", R.drawable.mcdonalds)
            )
        ),
        Restaurant(
            id = 6,
            name = "KFC",
            deliveryInfo = "Gastos de envío a 1,99 - 22 min",
            rating = "4.1 (220+)",
            imageRes = R.drawable.kfc,
            products = listOf(
                ProductItem(11, "Bucket 6 piezas", "11,95€", "Descripcion", R.drawable.kfc),
                ProductItem(12, "Twister", "7,95€", "Descripcion", R.drawable.kfc)
            )
        ),
        Restaurant(
            id = 7,
            name = "Taco Bell",
            deliveryInfo = "Sin gastos de envío - 19 min",
            rating = "4.0 (180+)",
            imageRes = R.drawable.tacobell,
            products = listOf(
                ProductItem(13, "Crunchy Taco", "6,95€", "Descripcion", R.drawable.tacobell),
                ProductItem(14, "Quesadilla", "7,80€", "Descripcion", R.drawable.tacobell)
            )
        ),
        Restaurant(
            id = 8,
            name = "Five Guys",
            deliveryInfo = "Gastos de envío a 2,49 - 24 min",
            rating = "4.7 (150+)",
            imageRes = R.drawable.fiveguys,
            products = listOf(
                ProductItem(15, "Cheeseburger", "11,95€", "Descripcion", R.drawable.fiveguys),
                ProductItem(16, "Bacon Burger", "12,95€", "Descripcion", R.drawable.fiveguys)
            )
        ),
        Restaurant(
            id = 9,
            name = "Goiko",
            deliveryInfo = "Gastos de envío a 2,99 - 26 min",
            rating = "4.6 (290+)",
            imageRes = R.drawable.goiko,
            products = listOf(
                ProductItem(17, "Kevin Bacon", "13,90€", "Descripcion", R.drawable.goiko),
                ProductItem(18, "Yankee", "12,90€", "Descripcion", R.drawable.goiko)
            )
        ),
        Restaurant(
            id = 10,
            name = "Vips",
            deliveryInfo = "Sin gastos de envío - 28 min",
            rating = "4.1 (140+)",
            imageRes = R.drawable.vips,
            products = listOf(
                ProductItem(19, "Club Sandwich", "10,95€", "Descripcion", R.drawable.vips),
                ProductItem(20, "Hamburguesa Vips", "11,50€", "Descripcion", R.drawable.vips)
            )
        ),
        Restaurant(
            id = 11,
            name = "Foster's Hollywood",
            deliveryInfo = "Gastos de envío a 2,95 - 30 min",
            rating = "4.2 (260+)",
            imageRes = R.drawable.fosters,
            products = listOf(
                ProductItem(21, "Cheese Bacon Fries", "9,95€", "Descripcion", R.drawable.fosters),
                ProductItem(22, "Director's Choice", "13,95€", "Descripcion", R.drawable.fosters)
            )
        ),
        Restaurant(
            id = 12,
            name = "Telepizza",
            deliveryInfo = "Sin gastos de envío - 21 min",
            rating = "4.0 (340+)",
            imageRes = R.drawable.telepizza,
            products = listOf(
                ProductItem(23, "Barbacoa", "12,50€", "Descripcion", R.drawable.telepizza),
                ProductItem(24, "4 Quesos", "11,95€", "Descripcion", R.drawable.telepizza)
            )
        ),
        Restaurant(
            id = 13,
            name = "Papa Johns",
            deliveryInfo = "Gastos de envío a 1,99 - 23 min",
            rating = "4.3 (210+)",
            imageRes = R.drawable.papajohns,
            products = listOf(
                ProductItem(25, "Pepperoni Pizza", "13,25€", "Descripcion", R.drawable.papajohns),
                ProductItem(26, "Chicken BBQ Pizza", "13,95€", "Descripcion", R.drawable.papajohns)
            )
        ),
        Restaurant(
            id = 14,
            name = "Subway",
            deliveryInfo = "Sin gastos de envío - 17 min",
            rating = "4.1 (190+)",
            imageRes = R.drawable.subway,
            products = listOf(
                ProductItem(27, "B.M.T.", "7,50€", "Descripcion", R.drawable.subway),
                ProductItem(28, "Pollo Teriyaki", "7,95€", "Descripcion", R.drawable.subway)
            )
        ),
        Restaurant(
            id = 15,
            name = "Starbucks",
            deliveryInfo = "Gastos de envío a 2,20 - 16 min",
            rating = "4.5 (300+)",
            imageRes = R.drawable.starbucks,
            products = listOf(
                ProductItem(29, "Frappuccino", "5,95€", "Descripcion", R.drawable.starbucks),
                ProductItem(30, "Caramel Macchiato", "4,95€", "Descripcion", R.drawable.starbucks)
            )
        ),
        Restaurant(
            id = 16,
            name = "Dunkin'",
            deliveryInfo = "Sin gastos de envío - 15 min",
            rating = "4.2 (230+)",
            imageRes = R.drawable.dunkin,
            products = listOf(
                ProductItem(31, "Pack 6 donuts", "8,95€", "Descripcion", R.drawable.dunkin),
                ProductItem(32, "Iced Coffee", "3,95€", "Descripcion", R.drawable.dunkin)
            )
        ),
        Restaurant(
            id = 17,
            name = "100 Montaditos",
            deliveryInfo = "Gastos de envío a 1,50 - 20 min",
            rating = "3.8 (260+)",
            imageRes = R.drawable.montaditos,
            products = listOf(
                ProductItem(33, "Montadito Jamón", "2,20€", "Descripcion", R.drawable.montaditos),
                ProductItem(34, "Montadito Bacon", "2,50€", "Descripcion", R.drawable.montaditos)
            )
        ),
        Restaurant(
            id = 18,
            name = "Ginos",
            deliveryInfo = "Gastos de envío a 2,80 - 29 min",
            rating = "4.0 (170+)",
            imageRes = R.drawable.ginos,
            products = listOf(
                ProductItem(35, "Pizza Prosciutto", "12,95€", "Descripcion", R.drawable.ginos),
                ProductItem(36, "Spaghetti Carbonara", "11,95€", "Descripcion", R.drawable.ginos)
            )
        ),
        Restaurant(
            id = 19,
            name = "UDON",
            deliveryInfo = "Sin gastos de envío - 27 min",
            rating = "4.4 (120+)",
            imageRes = R.drawable.udon,
            products = listOf(
                ProductItem(37, "Yakisoba", "11,90€", "Descripcion", R.drawable.udon),
                ProductItem(38, "Ramen", "12,90€", "Descripcion", R.drawable.udon)
            )
        ),
        Restaurant(
            id = 20,
            name = "Sushi Shop",
            deliveryInfo = "Gastos de envío a 3,20 - 31 min",
            rating = "4.5 (160+)",
            imageRes = R.drawable.sushishop,
            products = listOf(
                ProductItem(39, "Sushi Mix", "14,95€", "Descripcion", R.drawable.sushishop),
                ProductItem(40, "California Roll", "10,95€", "Descripcion", R.drawable.sushishop)
            )
        )
    )

    fun getExploreRestaurants(): List<Restaurant> {
        val excludedIds = getFeaturedRestaurants().map { it.id } + getPromoRestaurants().map { it.id }
        return allRestaurants.filter { it.id !in excludedIds }
    }

    fun getAllRestaurants(): List<Restaurant> = allRestaurants

    fun getRestaurantById(id: Int): Restaurant? {
        return allRestaurants.find { it.id == id }
    }

    fun getFeaturedRestaurants(): List<Restaurant> {
        return allRestaurants.take(6)
    }

    fun getPromoRestaurants(): List<Restaurant> {
        return allRestaurants.filter { it.promoText != null }
    }

    fun searchRestaurants(query: String): List<Restaurant> {
        if (query.isBlank()) return allRestaurants

        return allRestaurants.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }
}
