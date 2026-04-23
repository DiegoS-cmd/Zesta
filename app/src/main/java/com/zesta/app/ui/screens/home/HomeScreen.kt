package com.zesta.app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.screens.restaurant.Restaurant
import com.zesta.app.data.repository.RestaurantRepository
import com.zesta.app.navigation.AppRoutes
import com.zesta.app.ui.components.AddressBottomSheet
import com.zesta.app.ui.components.ZestaBottomNavBar
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel


data class CategoryItem(
    val name: String,
    val imageRes: Int
)

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRestaurantClick: (Int) -> Unit,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.collectAsState()

    val categories = listOf(
        CategoryItem(stringResource(R.string.categoria_desayuno), R.drawable.desayuno),
        CategoryItem(stringResource(R.string.categoria_pizzas), R.drawable.pizzas),
        CategoryItem(stringResource(R.string.categoria_hamburguesas), R.drawable.hamburguesas),
        CategoryItem(stringResource(R.string.categoria_panaderia), R.drawable.panaderia),
        CategoryItem(stringResource(R.string.categoria_asiatica), R.drawable.china),
        CategoryItem(stringResource(R.string.categoria_mexicana), R.drawable.mexicana)
    )

    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val featuredRestaurants = RestaurantRepository.getFeaturedRestaurants()
    val promoRestaurants = RestaurantRepository.getPromoRestaurants()
    val exploreRestaurants = RestaurantRepository.getExploreRestaurants()
    var showAddressSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {

        if (selectedCategory != null) {
            val filtered = RestaurantRepository.getByCategory(selectedCategory!!)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    FilteredHeader(
                        categoryName = selectedCategory!!,
                        onBack = { selectedCategory = null }
                    )
                }

                item {
                    CategoryRow(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategoryClick = { name ->
                            selectedCategory = if (selectedCategory == name) null else name
                        }
                    )
                }

                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.categoria_sin_resultados),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoResenaZesta
                            )
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { restaurant ->
                        FullWidthRestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.id) }
                        )
                    }
                }
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HomeTopBar(
                        address = authState.currentUser?.direccion,
                        onAddressClick = { showAddressSheet = true }
                    )
                }

                item {
                    CategoryRow(
                        categories = categories,
                        selectedCategory = null,
                        onCategoryClick = { name -> selectedCategory = name }
                    )
                }

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

                val favoritos = authState.favoritos
                val favoritoRestaurants = RestaurantRepository.getAllRestaurants()
                    .filter { favoritos.contains(it.id) }

                if (favoritoRestaurants.isNotEmpty()) {
                    item {
                        SectionHeader(title = stringResource(R.string.inicio_favoritos))
                    }
                    item {
                        RestaurantRow(
                            restaurants = favoritoRestaurants,
                            onRestaurantClick = onRestaurantClick
                        )
                    }
                }

                item { SectionHeader(title = stringResource(R.string.inicio_promociones)) }
                item {
                    RestaurantRow(
                        restaurants = promoRestaurants,
                        onRestaurantClick = onRestaurantClick
                    )
                }


                item { SectionHeader(title = stringResource(R.string.inicio_explorar)) }
                item {
                    ExploreRestaurantGrid(
                        restaurants = exploreRestaurants,
                        onRestaurantClick = onRestaurantClick
                    )
                }
            }
        }

        ZestaBottomNavBar(
            selectedRoute = AppRoutes.Home.route,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            onHomeClick = { selectedCategory = null },
            onSearchClick = onSearchClick,
            onCartClick = onCartClick,
            onProfileClick = onProfileClick
        )
        if (showAddressSheet) {
            AddressBottomSheet(
                currentUser = authState.currentUser,
                authViewModel = authViewModel,
                onDismiss = { showAddressSheet = false }
            )
        }

    }

}

@Composable
private fun HomeTopBar(
    address: String?,
    onAddressClick: () -> Unit
) {
    val hasAddress = !address.isNullOrBlank()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onAddressClick() }
                .background(
                    if (hasAddress) FondoSeleccionNaranjaZesta else FondoTarjetaRestauranteZesta
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = if (hasAddress) NaranjaZesta else TextoSecundarioZesta,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = if (hasAddress) address!! else stringResource(R.string.inicio_direccion),
                style = MaterialTheme.typography.bodyLarge,
                color = if (hasAddress) NaranjaZesta else TextoPrincipalZesta,
                fontWeight = if (hasAddress) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 220.dp)
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = if (hasAddress) NaranjaZesta else TextoSecundarioZesta,
                modifier = Modifier.size(18.dp)
            )
        }

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = stringResource(R.string.accesibilidad_notificaciones),
            tint = NegroZesta,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun FilteredHeader(
    categoryName: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(FondoTarjetaRestauranteZesta)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.accesibilidad_volver),
                tint = NegroZesta,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipalZesta
        )
    }
}

@Composable
private fun FullWidthRestaurantCard(
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
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
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
private fun SectionHeader(title: String, subtitle: String? = null) {
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

@Composable
private fun CategoryRow(
    categories: List<CategoryItem>,
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items(categories) { category ->
            val isSelected = category.name == selectedCategory
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCategoryClick(category.name) }
            ) {
                Image(
                    painter = painterResource(category.imageRes),
                    contentDescription = stringResource(
                        R.string.accesibilidad_imagen_categoria,
                        category.name
                    ),
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) NaranjaZesta else BordeCategoriaZesta,
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) NaranjaZesta else TextoPrincipalZesta,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }

}

