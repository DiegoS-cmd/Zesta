package com.zesta.app.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.zesta.app.data.repository.CartRepository
import com.zesta.app.data.repository.RestaurantRepository
import com.zesta.app.data.repository.UserPreferencesRepository
import com.zesta.app.ui.screens.cart.CartDetailScreen
import com.zesta.app.ui.screens.cart.CartScreen
import com.zesta.app.ui.screens.cart.DeliveryTrackingScreen
import com.zesta.app.ui.screens.cart.OrderSuccessScreen
import com.zesta.app.ui.screens.cart.OrderSummaryScreen
import com.zesta.app.ui.screens.home.HomeScreen
import com.zesta.app.ui.screens.login.BusinessContactScreen
import com.zesta.app.ui.screens.login.ForgotPasswordScreen
import com.zesta.app.ui.screens.login.LoginScreen
import com.zesta.app.ui.screens.profile.FavoritesScreen
import com.zesta.app.ui.screens.profile.ManageAccountScreen
import com.zesta.app.ui.screens.profile.OrderHistoryScreen
import com.zesta.app.ui.screens.profile.ProfileScreen
import com.zesta.app.ui.screens.register.RegisterScreen
import com.zesta.app.ui.screens.restaurant.RestaurantDetailScreen
import com.zesta.app.ui.screens.search.SearchScreen
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.utils.calcularTiempoEntregaMinutos
import com.zesta.app.utils.geocodificarDireccionMadrid
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.CartViewModel
import com.zesta.app.viewmodel.CartViewModelFactory
import java.net.URLDecoder

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val preferencesRepository = remember { UserPreferencesRepository(context) }
    val scope = rememberCoroutineScope()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.factory(
            authRepository = AuthRepository(),
            preferencesRepository = UserPreferencesRepository(context)
        )
    )

    val uiState by authViewModel.uiState.collectAsState()
    val cartRepository = remember { CartRepository() }
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(repository = cartRepository)
    )

    val googleSignInClient = remember {
        com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(
            context,
            com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
            )
                .requestIdToken("216901028902-fgmtdm40qf737kqcrc8u6r7unt40ffaq.apps.googleusercontent.com")
                .requestEmail()
                .build()
        )
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = com.google.android.gms.auth.api.signin.GoogleSignIn
            .getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            val token = account.idToken
            android.util.Log.d("GOOGLE_LOGIN", "idToken: $token")
            if (token != null) {
                authViewModel.loginWithGoogle(
                    idToken = token,
                    onSuccess = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onError = { error ->
                        android.util.Log.e("GOOGLE_LOGIN", "onError: $error")
                    }
                )
            } else {
                android.util.Log.e("GOOGLE_LOGIN", "idToken es null")
            }
        } catch (e: com.google.android.gms.common.api.ApiException) {
            android.util.Log.e("GOOGLE_LOGIN", "ApiException código: ${e.statusCode} - ${e.message}")
        } catch (e: Exception) {
            android.util.Log.e("GOOGLE_LOGIN", "Excepción: ${e.message}")
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash.route
    ) {
        composable(AppRoutes.Splash.route) {
            LaunchedEffect(uiState.isSessionChecked) {
                if (!uiState.isSessionChecked) return@LaunchedEffect
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
                modifier = Modifier.fillMaxSize().background(FondoZesta),
                contentAlignment = Alignment.Center
            ) {}
        }

        composable(AppRoutes.Login.route) {
            LoginScreen(
                email = uiState.email,
                password = uiState.password,
                errorMessage = uiState.errorMessage,
                onEmailChange = authViewModel::onEmailChange,
                onGoogleSignIn = {
                    googleSignInClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleSignInClient.signInIntent)
                    }
                },
                onForgotPassword = { navController.navigate("forgot_password") },
                onPasswordChange = authViewModel::onPasswordChange,
                onLoginClick = {
                    authViewModel.login {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onEresEmpresa = { navController.navigate("business_contact") },
                onGoRegister = { navController.navigate(AppRoutes.Register.route) },
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

        composable("business_contact") {
            BusinessContactScreen(onBack = { navController.popBackStack() })
        }

        composable("forgot_password") {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
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
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.Home.route) {
            HomeScreen(
                authViewModel = authViewModel,
                onSearchClick = { navController.navigate(AppRoutes.Search.route) },
                onCartClick = { navController.navigate(AppRoutes.Cart.route) },
                onProfileClick = { navController.navigate(AppRoutes.Profile.route) },
                onRestaurantClick = { restaurantId ->
                    navController.navigate(AppRoutes.RestaurantDetail.createRoute(restaurantId))
                }
            )
        }

        composable(AppRoutes.Search.route) {
            SearchScreen(
                authViewModel = authViewModel,
                onHomeClick = { navController.navigate(AppRoutes.Home.route) },
                onCartClick = { navController.navigate(AppRoutes.Cart.route) },
                onProfileClick = { navController.navigate(AppRoutes.Profile.route) },
                onRestaurantClick = { id ->
                    navController.navigate(AppRoutes.RestaurantDetail.createRoute(id))
                }
            )
        }

        composable(AppRoutes.Cart.route) {
            CartScreen(
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
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
            route = AppRoutes.OrderSummary.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0
            OrderSummaryScreen(
                restaurantId = restaurantId,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                cartViewModel = cartViewModel,
                onOrderPlaced = { rId, totalMinutes, rName ->
                    navController.navigate(
                        AppRoutes.DeliveryTracking.createRoute(rId, totalMinutes, rName)
                    ) {
                        popUpTo(AppRoutes.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = AppRoutes.OrderSuccess.route,
            arguments = listOf(navArgument("showRating") { type = NavType.BoolType })
        ) { backStackEntry ->
            val showRating = backStackEntry.arguments?.getBoolean("showRating") ?: false
            OrderSuccessScreen(
                showRatingDialog = showRating,
                onGoHome = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.OrderSuccess.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = AppRoutes.DeliveryTracking.route,
            arguments = listOf(
                navArgument("restaurantId") { type = NavType.IntType },
                navArgument("totalMinutes") { type = NavType.IntType },
                navArgument("restaurantName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: return@composable
            val totalMinutes = backStackEntry.arguments?.getInt("totalMinutes") ?: 30
            // ← Decode necesario porque el nombre puede tener espacios/tildes
            val restaurantName = URLDecoder.decode(
                backStackEntry.arguments?.getString("restaurantName") ?: "",
                "UTF-8"
            )

            val restaurant = RestaurantRepository.getRestaurantById(restaurantId)
            val authState by authViewModel.uiState.collectAsState()
            val direccion = authState.currentUser?.direccion.orEmpty()
            val (userLat, userLon) = remember(direccion) {
                geocodificarDireccionMadrid(direccion)
            }

            DeliveryTrackingScreen(
                restaurantId = restaurantId,
                totalMinutes = totalMinutes,
                restaurantName = restaurantName,
                restaurantLat = restaurant?.latitude ?: 40.4168,
                restaurantLon = restaurant?.longitude ?: -3.7038,
                userLat = userLat,
                userLon = userLon,
                onBack = {
                    navController.popBackStack()
                },
                onFinished = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                userName = uiState.userName,
                isGuest = uiState.isGuest,
                onHomeClick = { navController.navigate(AppRoutes.Home.route) },
                onSearchClick = { navController.navigate(AppRoutes.Search.route) },
                onCartClick = { navController.navigate(AppRoutes.Cart.route) },
                onFavoritesClick = { navController.navigate(AppRoutes.Favorites.route) },
                onOrderHistoryClick = { navController.navigate(AppRoutes.OrderHistory.route) },
                onHelpClick = { },
                onPrivacyClick = { },
                onAccessibilityClick = { },
                onManageAccountClick = { navController.navigate(AppRoutes.ManageAccount.route) },
                onAboutClick = { },
                onLoginClick = { navController.navigate(AppRoutes.Login.route) },
                onRegisterClick = { navController.navigate(AppRoutes.Register.route) }
            )
        }

        composable(AppRoutes.ManageAccount.route) {
            ManageAccountScreen(
                authViewModel = authViewModel,
                isGuest = uiState.isGuest,
                userName = uiState.userName,
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate(AppRoutes.Login.route) },
                onRegisterClick = { navController.navigate(AppRoutes.Register.route) },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.Favorites.route) {
            FavoritesScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onRestaurantClick = { restaurantId ->
                    navController.navigate(AppRoutes.RestaurantDetail.createRoute(restaurantId))
                }
            )
        }

        composable(AppRoutes.OrderHistory.route) {
            OrderHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoutes.RestaurantDetail.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0
            RestaurantDetailScreen(
                authViewModel = authViewModel,
                restaurantId = restaurantId,
                onBack = { navController.popBackStack() },
                onGoToCart = { navController.navigate(AppRoutes.Cart.route) },
                onNavigateToLogin = { navController.navigate(AppRoutes.Login.route) }
            )
        }

        composable(
            route = AppRoutes.CartDetail.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0
            CartDetailScreen(
                cartViewModel = cartViewModel,
                authViewModel = authViewModel,
                restaurantId = restaurantId,
                onBack = { navController.popBackStack() },
                onNavigateToManageAccount = {
                    navController.navigate(AppRoutes.ManageAccount.route)
                },
                onNavigateToProfile = {
                    navController.navigate(AppRoutes.Profile.route)
                },
                onGoToOrderSummary = {
                    navController.navigate(AppRoutes.OrderSummary.createRoute(restaurantId))
                }
            )
        }
    }
}