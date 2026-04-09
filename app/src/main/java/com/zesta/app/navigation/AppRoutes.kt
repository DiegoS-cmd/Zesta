package com.zesta.app.navigation

sealed class AppRoutes(val route: String) {
    object Splash : AppRoutes("splash")
    object Login : AppRoutes("login")
    object Register : AppRoutes("register")
    object Home : AppRoutes("home")
    object Search : AppRoutes("search")
    object Cart : AppRoutes("cart")
    object CartDetail : AppRoutes("cart_detail/{restaurantId}") {
        fun createRoute(restaurantId: Int) = "cart_detail/$restaurantId"

    }
    object Profile : AppRoutes("profile")
    object ManageAccount : AppRoutes("manage_account")
    object RestaurantDetail : AppRoutes("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: Int) = "restaurant_detail/$restaurantId"
    }
    object Favorites : AppRoutes("favorites")
    object OrderSummary : AppRoutes("order_summary/{restaurantId}") {
        fun createRoute(restaurantId: Int) = "order_summary/$restaurantId"
    }
    object OrderSuccess : AppRoutes("order_success")
    }


