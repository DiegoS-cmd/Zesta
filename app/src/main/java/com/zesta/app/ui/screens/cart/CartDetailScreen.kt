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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.repository.CartRepository
import com.zesta.app.ui.theme.AzulFinGradienteZesta
import com.zesta.app.ui.theme.AzulInicioGradienteZesta
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.BordeBotonZesta
import com.zesta.app.ui.theme.BordeCirculoZesta
import com.zesta.app.ui.theme.FondoCirculoZesta
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta
import com.zesta.app.ui.theme.TextoSecundarioZesta
import com.zesta.app.viewmodel.CartViewModel
import com.zesta.app.viewmodel.CartViewModelFactory

@Composable
fun CartDetailScreen(
    restaurantId: Int,
    onBack: () -> Unit
) {
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(repository = CartRepository())
    )
    val uiState by cartViewModel.uiState.collectAsState()

    val cartGroup = uiState.carts.firstOrNull { it.cart.restaurantId == restaurantId }
    val items = cartGroup?.items ?: emptyList()
    val totalPrice = items.sumOf { it.precio * it.cantidad }
    val restaurantName = cartGroup?.cart?.restaurantName ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // TopBar
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
                        contentDescription = "Volver",
                        tint = NegroZesta,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = restaurantName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de items
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = items,
                    key = { it.productId }
                ) { item ->
                    CartDetailItemCard(
                        restaurantId = restaurantId,
                        item = item,
                        onIncrease = { cartViewModel.increaseQuantity(restaurantId, item) },
                        onDecrease = {
                            if (item.cantidad == 1) {
                                cartViewModel.removeItem(restaurantId, item)
                            } else {
                                cartViewModel.decreaseQuantity(restaurantId, item)
                            }
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            // Total y botón pagar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "%.2f €".format(totalPrice),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón pagar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)
                        )
                    )
                    .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                    .clickable { /* lógica de pago */ }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pagar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BlancoZesta,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CartDetailItemCard(
    restaurantId: Int,
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FondoPlaceholderZesta)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BlancoZesta),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageRes != 0) {
                Image(
                    painter = painterResource(item.imageRes),
                    contentDescription = item.nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipalZesta
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%.2f €".format(item.precio),
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundarioZesta
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Selector cantidad
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(FondoCirculoZesta)
                    .border(1.dp, BordeCirculoZesta, CircleShape)
                    .clickable { onDecrease() },
                contentAlignment = Alignment.Center
            ) {
                if (item.cantidad == 1) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = NegroZesta,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "-",
                        color = NegroZesta,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = item.cantidad.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(FondoCirculoZesta)
                    .border(1.dp, BordeCirculoZesta, CircleShape)
                    .clickable { onIncrease() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = NegroZesta,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
