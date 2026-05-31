package com.zesta.app.ui.screens.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.repository.RestaurantCartWithItems
import com.zesta.app.ui.components.ZestaBottomNavBar
import com.zesta.app.ui.theme.AzulFinGradienteZesta
import com.zesta.app.ui.theme.AzulInicioGradienteZesta
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.BordeBotonZesta
import com.zesta.app.ui.theme.BordeCirculoZesta
import com.zesta.app.ui.theme.BordeIlustracionCarritoZesta
import com.zesta.app.ui.theme.FondoIlustracionCarritoZesta
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta
import com.zesta.app.ui.theme.TextoSecundarioZesta
import com.zesta.app.navigation.AppRoutes
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.CartViewModel

/**
 * Pantalla principal del carrito.
 * Si tienes cosas de varios restaurantes salen agrupadas; si está vacío te manda a comprar.
 */
@Composable
fun CartScreen(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    cartViewModel: CartViewModel,
    onStartShoppingClick: () -> Unit,
    onCartDetailClick: (Int) -> Unit,
    authViewModel: AuthViewModel
) {
    val uiState by cartViewModel.uiState.collectAsState()
    // Al entrar recargo el carrito por si cambió en otra pantalla
    LaunchedEffect(Unit) {
        cartViewModel.loadCart()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.carrito_titulo),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    // Mientras llega Firestore
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.carrito_cargando),
                            color = TextoSecundarioZesta,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                uiState.carts.isEmpty() -> {
                    // Nada en el carrito — ilustración y botón para ir al home
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyCartContent(onStartShoppingClick = onStartShoppingClick)
                    }
                }

                else -> {
                    // Una tarjeta por restaurante; al pulsar vas al detalle de ese carrito
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = uiState.carts,
                            key = { it.cart.restaurantId }
                        ) { cartGroup ->
                            RestaurantCartSummaryCard(
                                cartGroup = cartGroup,
                                onClick = { onCartDetailClick(cartGroup.cart.restaurantId) }
                            )
                        }

                        item {
                            // Espacio extra para que no tape la bottom bar
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        ZestaBottomNavBar(
            selectedRoute = AppRoutes.Cart.route,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onCartClick = onCartClick,
            onProfileClick = onProfileClick
        )
    }
}

// Resumen rápido: foto del local, cuántos items y precio total
@Composable
private fun RestaurantCartSummaryCard(
    cartGroup: RestaurantCartWithItems,
    onClick: () -> Unit
) {
    val totalItems = cartGroup.items.sumOf { it.cantidad }
    val totalPrice = cartGroup.items.sumOf { it.precio * it.cantidad }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FondoPlaceholderZesta)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen del restaurante
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(FondoIlustracionCarritoZesta)
                .border(1.dp, BordeCirculoZesta, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            val imageResName = cartGroup.cart.restaurantImageResName
            val context = androidx.compose.ui.platform.LocalContext.current
            // En Firestore guardamos el nombre del drawable, no el id — lo busco a mano
            val imageResId = remember(imageResName) {
                if (imageResName.isNotBlank()) {
                    context.resources.getIdentifier(imageResName, "drawable", context.packageName)
                } else 0
            }

            if (imageResId != 0) {
                Image(
                    painter = painterResource(imageResId),
                    contentDescription = cartGroup.cart.restaurantName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Si no encuentro la imagen pongo el icono del carrito
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    tint = NegroZesta,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cartGroup.cart.restaurantName,
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$totalItems ${stringResource(if (totalItems == 1) R.string.carrito_articulo_singular else R.string.carrito_articulo_plural)}  •  ${stringResource(R.string.carrito_precio_formato, totalPrice)}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundarioZesta
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = TextoSecundarioZesta,
            modifier = Modifier.size(22.dp)
        )
    }
}

// Vista cuando no hay nada — te anima a ir a mirar restaurantes
@Composable
private fun EmptyCartContent(onStartShoppingClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(FondoIlustracionCarritoZesta)
                .border(1.dp, BordeIlustracionCarritoZesta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = stringResource(R.string.accesibilidad_ilustracion_carrito_vacio),
                tint = NegroZesta,
                modifier = Modifier.size(42.dp)
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = stringResource(R.string.carrito_vacio_titulo),
            style = MaterialTheme.typography.headlineSmall,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.carrito_vacio_descripcion),
            style = MaterialTheme.typography.bodyLarge,
            color = TextoSecundarioZesta
        )
        Spacer(modifier = Modifier.height(34.dp))
        BlueActionButton(
            text = stringResource(R.string.carrito_empezar_comprar),
            onClick = onStartShoppingClick
        )
    }
}

// Botón azul con gradiente que usamos en el carrito vacío
@Composable
private fun BlueActionButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)
                )
            )
            .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = BlancoZesta,
            fontWeight = FontWeight.SemiBold
        )
    }
}
