package com.zesta.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zesta.app.ui.screens.cart.CartScreen
import com.zesta.app.ui.screens.home.HomeScreen
import com.zesta.app.ui.screens.login.LoginScreen
import com.zesta.app.ui.screens.profile.ProfileScreen
import com.zesta.app.ui.screens.register.RegisterScreen
import com.zesta.app.ui.screens.restaurant.RestaurantDetailScreen
import com.zesta.app.ui.screens.search.SearchScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Login.route
    ) {
        composable(AppRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                },
                onGoRegister = {
                    navController.navigate(AppRoutes.Register.route)
                }
            )
        }

        composable(AppRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.Home.route) {
            HomeScreen(
                onSearchClick = {
                    navController.navigate(AppRoutes.Search.route)
                },
                onCartClick = {
                    navController.navigate(AppRoutes.Cart.route)
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.Profile.route)
                },
                onRestaurantClick = { restaurantId ->
                    navController.navigate(AppRoutes.RestaurantDetail.createRoute(restaurantId))
                }
            )
        }

        composable(AppRoutes.Search.route) {
            SearchScreen(
                onHomeClick = {
                    navController.navigate(AppRoutes.Home.route)
                },
                onCartClick = {
                    navController.navigate(AppRoutes.Cart.route)
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.Profile.route)
                }
            )
        }


        composable(AppRoutes.Cart.route) {
            CartScreen(
                onHomeClick = {
                    navController.navigate(AppRoutes.Home.route)
                },
                onSearchClick = {
                    navController.navigate(AppRoutes.Search.route)
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.Profile.route)
                },
                onStartShoppingClick = {
                    navController.navigate(AppRoutes.Home.route)
                },
                onTestPurchaseClick = {
                    navController.navigate(AppRoutes.Home.route)
                }
            )
        }


        composable(AppRoutes.Profile.route) {
            ProfileScreen(
                userName = "",
                onHomeClick = {
                    navController.navigate(AppRoutes.Home.route)
                },
                onSearchClick = {
                    navController.navigate(AppRoutes.Search.route)
                },
                onCartClick = {
                    navController.navigate(AppRoutes.Cart.route)
                },
                onFavoritesClick = { },
                onOrderHistoryClick = { },
                onHelpClick = { },
                onPrivacyClick = { },
                onAccessibilityClick = { },
                onManageAccountClick = { },
                onAboutClick = { }
            )
        }


        composable(
            route = AppRoutes.RestaurantDetail.route,
            arguments = listOf(
                navArgument("restaurantId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0

            RestaurantDetailScreen(
                restaurantId = restaurantId,
                onBack = { navController.popBackStack() },
                onGoToCart = { navController.navigate(AppRoutes.Cart.route) }
            )
        }
    }
}
