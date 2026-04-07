package com.zesta.app.ui.screens.search

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.restaurant.Restaurant
import com.zesta.app.data.restaurant.RestaurantRepository
import com.zesta.app.navigation.AppRoutes
import com.zesta.app.ui.components.ZestaBottomNavBar
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel

data class SearchCategoryItemUi(
    @StringRes val titleRes: Int,
    @DrawableRes val imageRes: Int
)

@Composable
fun SearchScreen(
    onHomeClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRestaurantClick: (Int) -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }

    // Historial dinámico — máximo 3 últimas búsquedas
    var recentSearches by remember { mutableStateOf<List<String>>(emptyList()) }

    val categories = listOf(
        SearchCategoryItemUi(R.string.categoria_desayuno, R.drawable.desayuno),
        SearchCategoryItemUi(R.string.categoria_pizzas, R.drawable.pizzas),
        SearchCategoryItemUi(R.string.categoria_hamburguesas, R.drawable.hamburguesas),
        SearchCategoryItemUi(R.string.categoria_panaderia, R.drawable.panaderia),
        SearchCategoryItemUi(R.string.categoria_asiatica, R.drawable.china),
        SearchCategoryItemUi(R.string.categoria_mexicana, R.drawable.mexicana)
    )

    val searchResults: List<Restaurant> = remember(query) {
        if (query.isBlank()) emptyList()
        else {
            val q = query.trim()
            RestaurantRepository.getAllRestaurants().filter { restaurant ->
                val nameMatch = context.getString(restaurant.nameRes)
                    .contains(q, ignoreCase = true)
                val categoryMatch = restaurant.categories.any { cat ->
                    cat.contains(q, ignoreCase = true)
                }
                nameMatch || categoryMatch
            }
        }
    }

    // Guarda en recientes al escribir (solo cuando hay texto y no está ya)
    LaunchedEffect(query) {
        if (query.isBlank()) return@LaunchedEffect
        recentSearches = (listOf(query) + recentSearches)
            .distinct()
            .take(3)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 118.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SearchField(
                    query = query,
                    onQueryChange = { query = it },
                    onClear = { query = "" }  // ← botón X
                )
            }

            if (query.isNotBlank()) {
                if (searchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
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
                    item {
                        Text(
                            text = stringResource(R.string.busqueda_resultados, searchResults.size),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    items(searchResults, key = { it.id }) { restaurant ->
                        SearchRestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.id) }
                        )
                    }
                }
            } else {
                // Recientes — solo si hay alguno guardado
                if (recentSearches.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.busqueda_titulo_recientes),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    item {
                        RecentSearchRow(
                            searches = recentSearches,
                            onChipClick = { query = it }
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.busqueda_titulo_categorias_principales),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipalZesta,
                        fontWeight = FontWeight.Normal
                    )
                }
                items(categories) { category ->
                    val categoryName = stringResource(category.titleRes)
                    SearchCategoryRow(
                        title = categoryName,
                        imageRes = category.imageRes,
                        onClick = { query = categoryName }
                    )
                }
            }
        }

        ZestaBottomNavBar(
            selectedRoute = AppRoutes.Search.route,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onHomeClick = onHomeClick,
            onSearchClick = { },
            onCartClick = onCartClick,
            onProfileClick = onProfileClick
        )
    }
}


// ── Tarjeta de resultado

@Composable
private fun SearchRestaurantCard(
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

    androidx.compose.foundation.layout.Column(
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
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(30.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.busqueda_placeholder),
                color = TextoSecundarioZesta
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.accesibilidad_ir_buscar),
                tint = NegroZesta
            )
        },
        // Botón X — solo aparece cuando hay texto
        trailingIcon = {
            if (query.isNotBlank()) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.accesibilidad_limpiar_busqueda),
                    tint = NegroZesta,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onClear() }
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FondoTarjetaRestauranteZesta,
            unfocusedContainerColor = FondoTarjetaRestauranteZesta,
            focusedBorderColor = FondoTarjetaRestauranteZesta,
            unfocusedBorderColor = FondoTarjetaRestauranteZesta,
            cursorColor = NegroZesta
        )
    )
}

@Composable
private fun RecentSearchRow(
    searches: List<String>,
    onChipClick: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        searches.forEach { text ->
            SearchChip(text = text, onClick = { onChipClick(text) })
        }
    }
}


@Composable
private fun SearchChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(FondoTarjetaRestauranteZesta)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta
        )
    }
}

@Composable
private fun SearchCategoryRow(
    title: String,
    @DrawableRes imageRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = stringResource(R.string.accesibilidad_imagen_categoria, title),
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .border(2.dp, BordeCategoriaZesta, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipalZesta
        )
    }
}
