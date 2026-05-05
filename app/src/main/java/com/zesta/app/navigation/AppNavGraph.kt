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
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.CartViewModel
import com.zesta.app.viewmodel.CartViewModelFactory

/**
 * Navegación principal de Zesta.
 *
 * Este composable es el punto de entrada de toda la navegación de la app.
 * Se encarga de:
 * - Crear el [NavController] y el [NavHost] con todas las rutas registradas.
 * - Instanciar [AuthViewModel] y [CartViewModel] a nivel raíz para que sean
 *   compartidos por todas las pantallas que los necesitan.
 * - Configurar el cliente de Google Sign-In y su launcher de resultado.
 * - Gestionar el flujo inicial (Splash → Login o Home) en función del estado
 *   de sesión devuelto por [AuthViewModel].
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModel de autenticación creado a nivel raíz del grafo para que
    // todas las pantallas compartan la misma instancia y el mismo estado.
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.factory(
            authRepository = AuthRepository(),
            preferencesRepository = UserPreferencesRepository(context)
        )
    )

    // Se observa el estado de la UI para que el Splash y otras pantallas
    // reaccionen automáticamente a los cambios de sesión.
    val uiState by authViewModel.uiState.collectAsState()

    // CartRepository se envuelve en remember para no recrearlo en cada recomposición.
    val cartRepository = remember { CartRepository() }
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(repository = cartRepository)
    )

    // Cliente de Google Sign-In envuelto en remember para evitar recreaciones.
    // El Web Client ID debe coincidir con el registrado en Firebase Console.
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

    // Launcher que recibe el resultado del intent de Google Sign-In.
    // Si el token es válido, delega en authViewModel.loginWithGoogle().
    // Los errores se registran en Logcat con el tag GOOGLE_LOGIN.
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

        // SPLASH
        // Pantalla de carga inicial. No muestra contenido visible; solo espera a
        // que isSessionChecked = true antes de decidir el destino.
        // Este flag solo se activa cuando restoreSessionIfNeeded() ha terminado
        // de comprobar si la sesión es válida y si la cuenta no está deshabilitada,
        // evitando el flash de login cuando hay sesión guardada.
        composable(AppRoutes.Splash.route) {
            LaunchedEffect(uiState.isSessionChecked) {
                if (!uiState.isSessionChecked) return@LaunchedEffect
                val destination = if (uiState.isLoggedIn || uiState.isGuest) {
                    AppRoutes.Home.route
                } else {
                    AppRoutes.Login.route
                }
                navController.navigate(destination) {
                    // Elimina el Splash del backstack para que el botón atrás
                    // no vuelva a esta pantalla.
                    popUpTo(AppRoutes.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FondoZesta),
                contentAlignment = Alignment.Center
            ) {}
        }

        // LOGIN
        // Pantalla de inicio de sesión. Incluye login con email/contraseña,
        // Google Sign-In, acceso como invitado y reactivación de cuenta
        // deshabilitada. El error de cuenta deshabilitada activa el botón
        // onReactivateAccount en LoginScreen.
        composable(AppRoutes.Login.route) {
            LoginScreen(
                email = uiState.email,
                password = uiState.password,
                errorMessage = uiState.errorMessage,
                onEmailChange = authViewModel::onEmailChange,
                onGoogleSignIn = {
                    // Se fuerza signOut antes de lanzar el selector de cuenta
                    // para que el usuario pueda elegir cuenta cada vez.
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
                },
                onReactivateAccount = {
                    // El resultado (éxito o error) se gestiona a través de
                    // uiState.errorMessage, que LoginScreen muestra directamente.
                    authViewModel.reactivateAccount(
                        email = uiState.email,
                        password = uiState.password,
                        onSuccess = {},
                        onError = {}
                    )
                }
            )
        }

        // CONTACTO EMPRESA
        // Pantalla informativa para empresas que quieran unirse a Zesta.
        // TODO: mover ruta literal a AppRoutes.
        composable("business_contact") {
            BusinessContactScreen(onBack = { navController.popBackStack() })
        }

        // RECUPERAR CONTRASEÑA
        // Pantalla de recuperación de contraseña vía email de Firebase Auth.
        // TODO: mover ruta literal a AppRoutes.
        composable("forgot_password") {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        // REGISTRO
        // Tras un registro exitoso se eliminan tanto Register como Login del
        // backstack para que el usuario no pueda volver atrás con el botón back.
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

        // HOME
        // Pantalla principal con el listado de restaurantes disponibles.
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

        // BÚSQUEDA
        // Buscador de restaurantes por nombre o categoría.
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

        // CARRITO
        // Lista de restaurantes con artículos en el carrito. Desde aquí se
        // puede acceder al detalle del carrito de cada restaurante.
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

        // RESUMEN DEL PEDIDO
        // Pantalla de confirmación antes de realizar el pedido.
        // Recibe restaurantId como argumento Int.
        // Al confirmar el pedido navega a DeliveryTracking con los datos del envío.
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
                onOrderPlaced = { rId, totalMinutes, rName, rStreet, uStreet ->
                    navController.navigate(
                        AppRoutes.DeliveryTracking.createRoute(
                            rId, totalMinutes, rName, rStreet, uStreet
                        )
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // PEDIDO COMPLETADO
        // Pantalla de confirmación final del pedido.
        // showRating controla si se muestra el diálogo de valoración de la app.
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

        // SEGUIMIENTO DEL PEDIDO
        // Muestra el estado del pedido en tiempo real con las fases del proceso,
        // tiempo estimado e información del restaurante y dirección del usuario.
        // Los argumentos String se codifican con Uri.encode() en createRoute()
        // para manejar correctamente caracteres especiales en las direcciones.
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
            val restaurantName = backStackEntry.arguments?.getString("restaurantName").orEmpty()
            val restaurantStreet = backStackEntry.arguments?.getString("restaurantStreet").orEmpty()
            val userStreet = backStackEntry.arguments?.getString("userStreet").orEmpty()

            DeliveryTrackingScreen(
                restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0,
                totalMinutes = backStackEntry.arguments?.getInt("totalMinutes") ?: 30,
                restaurantName = restaurantName,
                restaurantStreet = restaurantStreet,
                userStreet = userStreet,
                onGoHome = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onFinished = {
                    // Se usa el mismo comportamiento que onGoHome para garantizar
                    // que no queda ninguna pantalla del flujo de pedido en el backstack.
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // PERFIL
        // Pantalla de perfil del usuario. Si es invitado muestra opciones de
        // login/registro. Los callbacks de ayuda, privacidad, accesibilidad
        // y "sobre nosotros" están pendientes de implementar.
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
                onHelpClick = { },           // TODO: implementar pantalla de ayuda
                onPrivacyClick = { },        // TODO: implementar pantalla de privacidad
                onAccessibilityClick = { },  // TODO: implementar pantalla de accesibilidad
                onManageAccountClick = { navController.navigate(AppRoutes.ManageAccount.route) },
                onAboutClick = { },          // TODO: implementar pantalla "sobre nosotros"
                onLoginClick = { navController.navigate(AppRoutes.Login.route) },
                onRegisterClick = { navController.navigate(AppRoutes.Register.route) }
            )
        }

        // GESTIONAR CUENTA
        // Permite al usuario ver y editar sus datos de entrega, cambiar contraseña,
        // cerrar sesión y deshabilitar la cuenta (soft delete).
        // Tanto onLogoutClick como onDeleteAccountSuccess limpian todo el backstack
        // hasta startDestinationId para evitar que el usuario pueda volver atrás
        // con el botón back tras salir de su sesión.
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

        // FAVORITOS
        // Lista de restaurantes marcados como favoritos por el usuario.
        composable(AppRoutes.Favorites.route) {
            FavoritesScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onRestaurantClick = { restaurantId ->
                    navController.navigate(AppRoutes.RestaurantDetail.createRoute(restaurantId))
                }
            )
        }

        // HISTORIAL DE PEDIDOS
        // Lista de pedidos anteriores del usuario.
        // TODO: pasar authViewModel cuando se implemente la carga de pedidos reales.
        composable(AppRoutes.OrderHistory.route) {
            OrderHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // DETALLES DE RESTAURANTE
        // Muestra el menú y la información del restaurante seleccionado.
        // Recibe restaurantId como argumento Int.
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

        // DETALLE DEL CARRITO
        // Muestra los artículos del carrito para un restaurante concreto.
        // Desde aquí se puede ir al resumen del pedido para confirmar la compra.
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