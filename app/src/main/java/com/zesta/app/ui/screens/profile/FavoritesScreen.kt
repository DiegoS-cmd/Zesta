package com.zesta.app.ui.screens.profile

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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.screens.restaurant.Restaurant
import com.zesta.app.data.repository.RestaurantRepository
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel

// Pantalla de favoritos: filtra los restaurantes que el usuario ha marcado
@Composable
fun FavoritesScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onRestaurantClick: (Int) -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val favoritos = authState.favoritos
    // Solo mostramos los restaurantes cuyo id está en la lista de favoritos del usuario
    val favoritoRestaurants = RestaurantRepository.getAllRestaurants()
        .filter { favoritos.contains(it.id) }

    Scaffold(containerColor = FondoZesta) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // TopBar con botón de volver y título
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(FondoCirculoZesta)
                        .border(1.dp, BordeCirculoZesta, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.accesibilidad_volver),
                        tint = NegroZesta,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = stringResource(R.string.perfil_favoritos),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (favoritoRestaurants.isEmpty()) {
                // Estado vacío: icono + mensaje explicativo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = TextoSecundarioZesta,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = stringResource(R.string.favoritos_vacio_titulo),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.favoritos_vacio_descripcion),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundarioZesta,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Lista de restaurantes favoritos
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(items = favoritoRestaurants, key = { it.id }) { restaurant ->
                        FavoriteRestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.id) },
                            onRemove = { authViewModel.toggleFavorito(restaurant.id) }
                        )
                    }
                }
            }
        }
    }
}

// Tarjeta individual de restaurante favorito con botón para quitarlo
@Composable
private fun FavoriteRestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val restaurantName = stringResource(restaurant.nameRes)
    val deliveryText = if (restaurant.hasFreeDelivery)
        stringResource(R.string.restaurante_envio_gratis, restaurant.deliveryTimeMinutes)
    else
        stringResource(R.string.restaurante_envio_pago, restaurant.deliveryFee ?: 0.0, restaurant.deliveryTimeMinutes)
    val ratingText = stringResource(
        R.string.restaurante_valoracion,
        restaurant.ratingValue,
        restaurant.ratingCount
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(FondoPlaceholderZesta)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(restaurant.imageRes),
            contentDescription = restaurantName,
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = restaurantName,
                style = MaterialTheme.typography.bodyLarge,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
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
        Spacer(modifier = Modifier.width(8.dp))
        // Botón para quitar el restaurante de favoritos
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(FondoSeleccionNaranjaZesta)
                .border(1.dp, NaranjaZesta, CircleShape)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = NaranjaZesta,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}