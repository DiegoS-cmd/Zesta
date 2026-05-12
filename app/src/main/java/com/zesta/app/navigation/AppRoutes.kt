package com.zesta.app.navigation

sealed class AppRoutes(val route: String) {
    object Splash : AppRoutes("splash")
    object Login : AppRoutes("login")
    object Register : AppRoutes("register")
    object Home : AppRoutes("home")
    object Search : AppRoutes("search")
    object Cart : AppRoutes("cart")
    object Profile : AppRoutes("profile")
    object ManageAccount : AppRoutes("manage_account")
    object Favorites : AppRoutes("favorites")
    object OrderHistory : AppRoutes("order_history")

    object CartDetail : AppRoutes("cart_detail/{restaurantId}") {
        fun createRoute(restaurantId: Int) = "cart_detail/$restaurantId"
    }
    object RestaurantDetail : AppRoutes("restaurant_detail/{restaurantId}") {
        fun createRoute(restaurantId: Int) = "restaurant_detail/$restaurantId"
    }
    object OrderSummary : AppRoutes("order_summary/{restaurantId}") {
        fun createRoute(restaurantId: Int) = "order_summary/$restaurantId"
    }
    object OrderSuccess : AppRoutes("order_success/{showRating}") {
        fun createRoute(showRating: Boolean) = "order_success/$showRating"
    }

    // Los String se codifican con Uri.encode() para soportar tildes y espacios en las direcciones
    object DeliveryTracking : AppRoutes(
        "delivery_tracking/{restaurantId}/{totalMinutes}/{restaurantName}/{restaurantStreet}/{userStreet}"
    ) {
        fun createRoute(
            restaurantId: Int,
            totalMinutes: Int,
            restaurantName: String,
            restaurantStreet: String,
            userStreet: String
        ) = "delivery_tracking/$restaurantId/$totalMinutes/${android.net.Uri.encode(restaurantName)}/${android.net.Uri.encode(restaurantStreet)}/${android.net.Uri.encode(userStreet)}"
    }
}