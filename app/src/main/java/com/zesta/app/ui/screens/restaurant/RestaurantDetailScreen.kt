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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.restaurant.Product
import com.zesta.app.data.restaurant.Restaurant
import com.zesta.app.data.restaurant.RestaurantRepository
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.BordeCirculoZesta
import com.zesta.app.ui.theme.BordeDoradoZesta
import com.zesta.app.ui.theme.BordeIconoZesta
import com.zesta.app.ui.theme.FondoBotonMasZesta
import com.zesta.app.ui.theme.FondoCirculoZesta
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta
import com.zesta.app.ui.theme.TextoResenaZesta
import com.zesta.app.ui.theme.AzulFinGradienteZesta
import com.zesta.app.ui.theme.NegroZesta as InicioGradiente
import com.zesta.app.ui.theme.ColorUbicacionZesta

@Composable
fun RestaurantDetailScreen(
    restaurantId: Int,
    onBack: () -> Unit,
    onGoToCart: () -> Unit
) {
    val restaurant = RestaurantRepository.getRestaurantById(restaurantId)
        ?: RestaurantRepository.getAllRestaurants().first()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp)
        ) {
            item { RestaurantTopBar(onBack) }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                RestaurantHeader(restaurant)
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.restaurante_promociones),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta
                )
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    restaurant.products.take(2).forEach { product ->
                        ProductPromoCard(
                            product = product,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.restaurante_explorar_menu),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta
                )
            }
        }

        ViewCartButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp),
            onClick = onGoToCart
        )
    }
}

@Composable
private fun RestaurantTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            icon = Icons.Outlined.ArrowBack,
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
private fun ProductPromoCard(
    product: Product,
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
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(FondoBotonMasZesta)
                    .border(1.dp, BordeCirculoZesta, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.producto_simbolo_mas),
                    color = NegroZesta,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = productName,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta
        )

        Text(
            text = productPrice,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta
        )

        Text(
            text = productDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoResenaZesta
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
