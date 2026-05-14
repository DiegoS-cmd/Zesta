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
import com.zesta.app.ui.screens.settings.AboutScreen
import com.zesta.app.ui.screens.settings.AccessibilityScreen
import com.zesta.app.ui.screens.settings.HelpScreen
import com.zesta.app.ui.screens.settings.PrivacyScreen
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.CartViewModel
import com.zesta.app.viewmodel.CartViewModelFactory

// Punto de entrada de toda la navegación de la app.
// AuthViewModel y CartViewModel se crean aquí para compartirlos entre pantallas.
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

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

    // remember evita recrear el cliente en cada recomposición
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
            if (token != null) {
                authViewModel.loginWithGoogle(
                    idToken = token,
                    onSuccess = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onError = {}
                )
            }
        } catch (e: com.google.android.gms.common.api.ApiException) {

        } catch (e: Exception) { }
    }

    NavHost(navController = navController, startDestination = AppRoutes.Splash.route) {

        // SPLASH — espera a isSessionChecked para evitar el flash de login
        composable(AppRoutes.Splash.route) {
            LaunchedEffect(uiState.isSessionChecked) {
                if (!uiState.isSessionChecked) return@LaunchedEffect
                val destination = if (uiState.isLoggedIn || uiState.isGuest)
                    AppRoutes.Home.route else AppRoutes.Login.route
                navController.navigate(destination) {
                    popUpTo(AppRoutes.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            Box(Modifier.fillMaxSize().background(FondoZesta), Alignment.Center) {}
        }

        // LOGIN
        composable(AppRoutes.Login.route) {
            LoginScreen(
                email = uiState.email,
                password = uiState.password,
                errorMessage = uiState.errorMessage,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onGoogleSignIn = {
                    // signOut fuerza el selector de cuenta en cada intento
                    googleSignInClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleSignInClient.signInIntent)
                    }
                },
                onForgotPassword = { navController.navigate("forgot_password") },
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
                },
                onReactivateAccount = {
                    authViewModel.reactivateAccount(
                        email = uiState.email,
                        password = uiState.password,
                        onSuccess = {},
                        onError = {}
                    )
                }
            )
        }

        // TODO: mover rutas literales a AppRoutes
        composable("business_contact") {
            BusinessContactScreen(onBack = { navController.popBackStack() })
        }

        composable("forgot_password") {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        // REGISTRO — al completarse limpia Register y Login del backstack
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
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onOrderPlaced = { rId, totalMinutes, rName, rStreet, uStreet ->
                    navController.navigate(
                        AppRoutes.DeliveryTracking.createRoute(rId, totalMinutes, rName, rStreet, uStreet)
                    ) { launchSingleTop = true }
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

        // Los String se codifican con Uri.encode() en createRoute() para soportar caracteres especiales
        composable(
            route = AppRoutes.DeliveryTracking.route,
            arguments = listOf(
                navArgument("restaurantId") { type = NavType.IntType },
                navArgument("totalMinutes") { type = NavType.IntType },
                navArgument("restaurantName") { type = NavType.StringType },
                navArgument("restaurantStreet") { type = NavType.StringType },
                navArgument("userStreet") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            DeliveryTrackingScreen(
                restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0,
                totalMinutes = backStackEntry.arguments?.getInt("totalMinutes") ?: 30,
                restaurantName = backStackEntry.arguments?.getString("restaurantName").orEmpty(),
                restaurantStreet = backStackEntry.arguments?.getString("restaurantStreet").orEmpty(),
                userStreet = backStackEntry.arguments?.getString("userStreet").orEmpty(),
                onGoHome = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
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
                onHelpClick = { navController.navigate(AppRoutes.Help.route) },
                onPrivacyClick = { navController.navigate(AppRoutes.Privacy.route) },
                onAccessibilityClick = { navController.navigate(AppRoutes.Accessibility.route) },
                onManageAccountClick = { navController.navigate(AppRoutes.ManageAccount.route) },
                onAboutClick = { navController.navigate(AppRoutes.About.route) },
                onLoginClick = { navController.navigate(AppRoutes.Login.route) },
                onRegisterClick = { navController.navigate(AppRoutes.Register.route) }
            )
        }

        composable(AppRoutes.Help.route) {
            HelpScreen(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.Privacy.route) {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.Accessibility.route) {
            AccessibilityScreen(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        // Al cerrar sesión o eliminar cuenta se limpia todo
        composable(AppRoutes.ManageAccount.route) {
            ManageAccountScreen(
                authViewModel = authViewModel,
                isGuest = uiState.isGuest,
                userName = uiState.userName,
                onBackClick = { navController.popBackStack() },
                onLoginClick = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = { navController.navigate(AppRoutes.Register.route) },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onDeleteAccountSuccess = {
                    authViewModel.logout()
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
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

        // conectar con pedidos reales de Firestore
        composable(AppRoutes.OrderHistory.route) {
            OrderHistoryScreen(onBack = { navController.popBackStack() })
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
                onNavigateToManageAccount = { navController.navigate(AppRoutes.ManageAccount.route) },
                onNavigateToProfile = { navController.navigate(AppRoutes.Profile.route) },
                onGoToOrderSummary = {
                    navController.navigate(AppRoutes.OrderSummary.createRoute(restaurantId))
                }
            )
        }



    }
}