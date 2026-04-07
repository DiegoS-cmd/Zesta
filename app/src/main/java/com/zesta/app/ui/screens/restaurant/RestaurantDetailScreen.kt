package com.zesta.app.ui.screens.restaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zesta.app.R
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.repository.CartRepository
import com.zesta.app.data.restaurant.Product
import com.zesta.app.data.restaurant.Restaurant
import com.zesta.app.data.restaurant.RestaurantRepository
import com.zesta.app.ui.theme.AzulFinGradienteZesta
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.BordeCirculoZesta
import com.zesta.app.ui.theme.BordeDoradoZesta
import com.zesta.app.ui.theme.BordeIconoZesta
import com.zesta.app.ui.theme.ColorUbicacionZesta
import com.zesta.app.ui.theme.FondoBotonMasZesta
import com.zesta.app.ui.theme.FondoCirculoZesta
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta
import com.zesta.app.ui.theme.TextoResenaZesta
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.ui.theme.NegroZesta as InicioGradiente
import com.zesta.app.viewmodel.CartViewModel
import com.zesta.app.viewmodel.CartViewModelFactory

@Composable
fun RestaurantDetailScreen(
    restaurantId: Int,
    onBack: () -> Unit,
    onGoToCart: () -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(repository = CartRepository())
    )

    val uiState by cartViewModel.uiState.collectAsState()

    val restaurant = RestaurantRepository.getRestaurantById(restaurantId)
        ?: RestaurantRepository.getAllRestaurants().first()

    val restaurantName = context.getString(restaurant.nameRes)
    val restaurantImageName = context.resources.getResourceEntryName(restaurant.imageRes)

    val currentRestaurantCart = uiState.carts.firstOrNull { it.cart.restaurantId == restaurant.id }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { RestaurantTopBar(onBack = onBack) }

            item { RestaurantHeader(restaurant = restaurant) }

            item {
                Text(
                    text = stringResource(R.string.restaurante_promociones),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    restaurant.products.take(2).forEach { product ->
                        val cartItem = currentRestaurantCart
                            ?.items
                            ?.firstOrNull { it.productId == product.id.toString() }

                        ProductPromoCard(
                            product = product,
                            quantity = cartItem?.cantidad ?: 0,
                            modifier = Modifier.weight(1f),
                            onAddToCart = {
                                cartViewModel.addItem(
                                    restaurantId = restaurant.id,
                                    restaurantName = restaurantName,
                                    restaurantImageResName = restaurantImageName,
                                    item = product.toCartItem(
                                        restaurantId = restaurant.id,
                                        productName = context.getString(product.nameRes)
                                    )
                                )
                            },
                            onDecrease = {
                                cartItem?.let {
                                    if (it.cantidad == 1) cartViewModel.removeItem(restaurant.id, it)
                                    else cartViewModel.decreaseQuantity(restaurant.id, it)
                                }
                            }
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.restaurante_explorar_menu),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta
                )
            }

            items(
                items = restaurant.products.drop(2),
                key = { product -> product.id }
            ) { product ->
                val cartItem = currentRestaurantCart
                    ?.items
                    ?.firstOrNull { it.productId == product.id.toString() }

                MenuProductCard(
                    product = product,
                    quantity = cartItem?.cantidad ?: 0,
                    onAddToCart = {
                        cartViewModel.addItem(
                            restaurantId = restaurant.id,
                            restaurantName = restaurantName,
                            restaurantImageResName = restaurantImageName,
                            item = product.toCartItem(
                                restaurantId = restaurant.id,
                                productName = context.getString(product.nameRes)
                            )
                        )
                    },
                    onDecrease = {
                        cartItem?.let {
                            if (it.cantidad == 1) cartViewModel.removeItem(restaurant.id, it)
                            else cartViewModel.decreaseQuantity(restaurant.id, it)
                        }
                    }
                )
            }
        }

        ViewCartButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onClick = onGoToCart
        )
    }
}

private fun Product.toCartItem(
    restaurantId: Int,
    productName: String
): CartItem {
    return CartItem(
        productId = id.toString(),
        restaurantId = restaurantId,
        nombre = productName,
        precio = price,
        cantidad = 1,
        imageRes = imageRes
    )
}

@Composable
private fun RestaurantTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = stringResource(R.string.accesibilidad_volver),
            onClick = onBack
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircleIconButton(
                icon = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.accesibilidad_buscar_accion),
                onClick = { }
            )
            CircleIconButton(
                icon = Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(R.string.accesibilidad_favorito_accion),
                onClick = { }
            )
        }
    }
}

@Composable
private fun RestaurantHeader(restaurant: Restaurant) {
    val restaurantName = stringResource(restaurant.nameRes)
    val ratingText = stringResource(
        R.string.restaurante_valoracion,
        restaurant.ratingValue,
        restaurant.ratingCount
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(restaurant.imageRes),
            contentDescription = restaurantName,
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(restaurant.imageRes),
            contentDescription = restaurantName,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(2.dp, BordeIconoZesta, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = restaurantName,
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = ratingText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoResenaZesta
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = stringResource(R.string.restaurante_ubicacion),
                tint = ColorUbicacionZesta,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.restaurante_ubicacion),
                style = MaterialTheme.typography.bodyLarge,
                color = TextoPrincipalZesta
            )
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onAdd: () -> Unit,
    onDecrease: () -> Unit,
    size: Int = 34
) {
    if (quantity == 0) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(FondoBotonMasZesta)
                .border(1.dp, BordeCirculoZesta, CircleShape)
                .clickable { onAdd() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.producto_simbolo_mas),
                color = NegroZesta,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(FondoBotonMasZesta)
                .border(1.dp, BordeCirculoZesta, RoundedCornerShape(30.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { onDecrease() },
                contentAlignment = Alignment.Center
            ) {
                if (quantity == 1) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = NegroZesta,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = "-",
                        color = NegroZesta,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Text(
                text = quantity.toString(),
                color = NegroZesta,
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { onAdd() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.producto_simbolo_mas),
                    color = NegroZesta,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun ProductPromoCard(
    product: Product,
    quantity: Int,
    onAddToCart: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productName = stringResource(product.nameRes)
    val productPrice = stringResource(R.string.producto_precio, product.price)
    val productDescription = stringResource(product.descriptionRes)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(FondoPlaceholderZesta)
            .padding(10.dp)
    ) {
        Box {
            Image(
                painter = painterResource(product.imageRes),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp, y = 10.dp)
            ) {
                QuantitySelector(
                    quantity = quantity,
                    onAdd = onAddToCart,
                    onDecrease = onDecrease,
                    size = 34
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = productName, style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta)
        Text(text = productPrice, style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta)
        Text(text = productDescription, style = MaterialTheme.typography.bodyMedium, color = TextoResenaZesta)
    }
}

@Composable
private fun MenuProductCard(
    product: Product,
    quantity: Int,
    onAddToCart: () -> Unit,
    onDecrease: () -> Unit
) {
    val productName = stringResource(product.nameRes)
    val productPrice = stringResource(R.string.producto_precio, product.price)
    val productDescription = stringResource(product.descriptionRes)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FondoPlaceholderZesta)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(product.imageRes),
            contentDescription = productName,
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = productName, style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = productPrice, style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = productDescription, style = MaterialTheme.typography.bodyMedium, color = TextoResenaZesta)
        }
        Spacer(modifier = Modifier.width(10.dp))
        QuantitySelector(
            quantity = quantity,
            onAdd = onAddToCart,
            onDecrease = onDecrease,
            size = 38
        )
    }
}

@Composable
private fun ViewCartButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(InicioGradiente, AzulFinGradienteZesta)
                )
            )
            .border(2.dp, BordeDoradoZesta, RoundedCornerShape(30.dp))
            .clickable { onClick() }
            .padding(horizontal = 26.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = stringResource(R.string.restaurante_ver_carrito),
                tint = BlancoZesta,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.restaurante_ver_carrito),
                style = MaterialTheme.typography.bodyLarge,
                color = BlancoZesta
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(FondoCirculoZesta)
            .border(1.dp, BordeCirculoZesta, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = NegroZesta,
            modifier = Modifier.size(28.dp)
        )
    }
}
