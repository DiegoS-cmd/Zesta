package com.zesta.app.ui.screens.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.restaurant.Restaurant
import com.zesta.app.data.restaurant.RestaurantRepository
import com.zesta.app.navigation.AppRoutes
import com.zesta.app.ui.components.ZestaBottomNavBar
import com.zesta.app.ui.theme.BordeCategoriaZesta
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.FondoOfertaZesta
import com.zesta.app.ui.theme.FondoTarjetaRestauranteZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.NaranjaZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta
import com.zesta.app.ui.theme.TextoResenaZesta

data class CategoryItem(
    val name: String,
    val imageRes: Int
)

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRestaurantClick: (Int) -> Unit
) {
    val categories = listOf(
        CategoryItem(stringResource(R.string.categoria_desayuno), R.drawable.desayuno),
        CategoryItem(stringResource(R.string.categoria_pizzas), R.drawable.pizzas),
        CategoryItem(stringResource(R.string.categoria_hamburguesas), R.drawable.hamburguesas),
        CategoryItem(stringResource(R.string.categoria_panaderia), R.drawable.panaderia),
        CategoryItem(stringResource(R.string.categoria_asiatica), R.drawable.china),
        CategoryItem(stringResource(R.string.categoria_mexicana), R.drawable.mexicana)
    )

    val featuredRestaurants = RestaurantRepository.getFeaturedRestaurants()
    val promoRestaurants = RestaurantRepository.getPromoRestaurants()
    val exploreRestaurants = RestaurantRepository.getExploreRestaurants()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HomeTopBar() }
            item { CategoryRow(categories) }

            item {
                SectionHeader(
                    title = stringResource(R.string.inicio_destacado),
                    subtitle = stringResource(R.string.inicio_patrocinado)
                )
            }

            item {
                RestaurantRow(
                    restaurants = featuredRestaurants,
                    onRestaurantClick = onRestaurantClick
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.inicio_promociones))
            }

            item {
                RestaurantRow(
                    restaurants = promoRestaurants,
                    onRestaurantClick = onRestaurantClick
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.inicio_explorar))
            }

            item {
                ExploreRestaurantGrid(
                    restaurants = exploreRestaurants,
                    onRestaurantClick = onRestaurantClick
                )
            }
        }

        ZestaBottomNavBar(
            selectedRoute = AppRoutes.Home.route,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            onHomeClick = { },
            onSearchClick = onSearchClick,
            onCartClick = onCartClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.inicio_direccion),
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipalZesta
        )

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = stringResource(R.string.accesibilidad_notificaciones),
            tint = NegroZesta,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun CategoryRow(categories: List<CategoryItem>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items(categories) { category ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(category.imageRes),
                    contentDescription = stringResource(
                        R.string.accesibilidad_imagen_categoria,
                        category.name
                    ),
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(1.dp, BordeCategoriaZesta, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoPrincipalZesta
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipalZesta
        )

        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoResenaZesta
            )
        }
    }
}

@Composable
private fun RestaurantRow(
    restaurants: List<Restaurant>,
    onRestaurantClick: (Int) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(restaurants, key = { it.id }) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) }
            )
        }
    }
}

@Composable
private fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    val restaurantName = stringResource(restaurant.nameRes)
    val deliveryText = if (restaurant.hasFreeDelivery) {
        stringResource(R.string.restaurante_envio_gratis, restaurant.deliveryTimeMinutes)
    } else {
        stringResource(
            R.string.restaurante_envio_pago,
            restaurant.deliveryFee ?: 0.0,
            restaurant.deliveryTimeMinutes
        )
    }
    val ratingText = stringResource(
        R.string.restaurante_valoracion,
        restaurant.ratingValue,
        restaurant.ratingCount
    )

    Column(
        modifier = Modifier
            .width(185.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(FondoTarjetaRestauranteZesta)
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(restaurant.imageRes),
                contentDescription = restaurantName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop
            )

            restaurant.promoTextRes?.let { promoRes ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp, start = 6.dp, end = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FondoOfertaZesta)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(promoRes),
                        color = BlancoZesta,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = restaurantName,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.Normal
        )

        Text(
            text = deliveryText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoPrincipalZesta
        )

        Text(
            text = ratingText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoResenaZesta
        )
    }
}

@Composable
private fun ExploreRestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    val restaurantName = stringResource(restaurant.nameRes)
    val deliveryText = if (restaurant.hasFreeDelivery) {
        stringResource(R.string.restaurante_envio_gratis, restaurant.deliveryTimeMinutes)
    } else {
        stringResource(
            R.string.restaurante_envio_pago,
            restaurant.deliveryFee ?: 0.0,
            restaurant.deliveryTimeMinutes
        )
    }
    val ratingText = stringResource(
        R.string.restaurante_valoracion,
        restaurant.ratingValue,
        restaurant.ratingCount
    )

    Column(
        modifier = Modifier
            .width(190.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(FondoTarjetaRestauranteZesta)
        ) {
            Image(
                painter = painterResource(restaurant.imageRes),
                contentDescription = restaurantName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            restaurant.promoTextRes?.let { promoRes ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NaranjaZesta)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(promoRes),
                        color = BlancoZesta,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = restaurantName,
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipalZesta
        )

        Text(
            text = deliveryText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoPrincipalZesta
        )

        Text(
            text = ratingText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoResenaZesta
        )
    }
}

@Composable
private fun ExploreRestaurantGrid(
    restaurants: List<Restaurant>,
    onRestaurantClick: (Int) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items(restaurants, key = { it.id }) { restaurant ->
            ExploreRestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) }
            )
        }
    }
}
