package com.zesta.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zesta.app.data.repository.AuthRepository
import com.zesta.app.data.repository.UserPreferencesRepository
import com.zesta.app.ui.screens.cart.CartScreen
import com.zesta.app.ui.screens.home.HomeScreen
import com.zesta.app.ui.screens.login.LoginScreen
import com.zesta.app.ui.screens.profile.ManageAccountScreen
import com.zesta.app.ui.screens.profile.ProfileScreen
import com.zesta.app.ui.screens.register.RegisterScreen
import com.zesta.app.ui.screens.restaurant.RestaurantDetailScreen
import com.zesta.app.ui.screens.search.SearchScreen
import com.zesta.app.ui.screens.cart.CartDetailScreen
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.AuthViewModelFactory

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            authRepository = AuthRepository(),
            preferencesRepository = UserPreferencesRepository(context)
        )
    )

    val uiState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash.route
    ) {
        composable(AppRoutes.Splash.route) {
            LaunchedEffect(uiState.isLoggedIn, uiState.isGuest) {
                val destination = if (uiState.isLoggedIn || uiState.isGuest) {
                    AppRoutes.Home.route
                } else {
                    AppRoutes.Login.route
                }

                navController.navigate(destination) {
                    popUpTo(AppRoutes.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FondoZesta),
                contentAlignment = Alignment.Center
            ){}
        }



            composable(AppRoutes.Login.route) {
            LoginScreen(
                email = uiState.email,
                password = uiState.password,
                errorMessage = uiState.errorMessage,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onLoginClick = {
                    authViewModel.login {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onGoRegister = {
                    navController.navigate(AppRoutes.Register.route)
                },
                onContinueAsGuestClick = {
                    authViewModel.continueAsGuest {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(AppRoutes.Register.route) {
            RegisterScreen(
                fullName = uiState.fullName,
                email = uiState.email,
                password = uiState.password,
                phone = uiState.phone,
                address = uiState.address,
                errorMessage = uiState.errorMessage,
                onFullNameChange = authViewModel::onFullNameChange,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onPhoneChange = authViewModel::onPhoneChange,
                onAddressChange = authViewModel::onAddressChange,
                onRegisterClick = {
                    authViewModel.register {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Register.route) { inclusive = true }
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
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
                onCartClick = {
                    navController.navigate(AppRoutes.Cart.route)
                },
                onCartDetailClick = {
                    navController.navigate(AppRoutes.CartDetail.route)
                },
                onStartShoppingClick = {
                    navController.navigate(AppRoutes.Home.route)
                }
            )
        }

        composable(AppRoutes.Profile.route) {
            ProfileScreen(
                userName = uiState.userName,
                isGuest = uiState.isGuest,
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
                onManageAccountClick = {
                    navController.navigate(AppRoutes.ManageAccount.route)
                },
                onAboutClick = { },
                onLoginClick = {
                    navController.navigate(AppRoutes.Login.route)
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.Register.route)
                }
            )
        }

        composable(AppRoutes.ManageAccount.route) {
            ManageAccountScreen(
                isGuest = uiState.isGuest,
                userName = uiState.userName,
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate(AppRoutes.Login.route)
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.Register.route)
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
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
        composable(AppRoutes.Cart.route) {
            CartScreen(
                onHomeClick = { navController.navigate(AppRoutes.Home.route) },
                onSearchClick = { navController.navigate(AppRoutes.Search.route) },
                onProfileClick = { navController.navigate(AppRoutes.Profile.route) },
                onCartClick = { navController.navigate(AppRoutes.Cart.route) },
                onStartShoppingClick = { navController.navigate(AppRoutes.Home.route) },
                onCartDetailClick = { restaurantId ->
                    navController.navigate(AppRoutes.CartDetail.createRoute(restaurantId))
                }
            )
        }

        composable(
            route = AppRoutes.CartDetail.route,
            arguments = listOf(
                navArgument("restaurantId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0
            CartDetailScreen(
                restaurantId = restaurantId,
                onBack = { navController.popBackStack() }
            )
        }
    }

}
