package com.zesta.app.ui.screens.cart

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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zesta.app.R
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.repository.AuthRepository
import com.zesta.app.data.repository.CartRepository
import com.zesta.app.data.repository.UserPreferencesRepository
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.CartViewModel
import com.zesta.app.viewmodel.CartViewModelFactory

@Composable
fun CartDetailScreen(
    restaurantId: Int,
    onBack: () -> Unit,
    onNavigateToManageAccount: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onGoToOrderSummary: () -> Unit,
    authViewModel: AuthViewModel
) {

    var showIncompleteDialog by remember { mutableStateOf(false) }
    var incompleteDialogIsGuest by remember { mutableStateOf(false) }

    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(repository = CartRepository())
    )

    val uiState by cartViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    val cartGroup = uiState.carts.firstOrNull { it.cart.restaurantId == restaurantId }
    val items = cartGroup?.items ?: emptyList()
    val totalPrice = items.sumOf { it.precio * it.cantidad }
    val restaurantName = cartGroup?.cart?.restaurantName ?: ""

    if (showIncompleteDialog) {
        ProfileIncompleteDialog(
            isGuest = incompleteDialogIsGuest,
            onDismiss = { showIncompleteDialog = false },
            onConfirm = {
                showIncompleteDialog = false
                if (incompleteDialogIsGuest) onNavigateToProfile()
                else onNavigateToManageAccount()
            }
        )
    }

    Scaffold(containerColor = FondoZesta) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                        contentDescription = stringResource(R.string.accesibilidad_volver),
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

            // Lista de productos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = items, key = { it.productId }) { item ->
                    CartDetailItemCard(
                        restaurantId = restaurantId,
                        item = item,
                        onIncrease = { cartViewModel.increaseQuantity(restaurantId, item) },
                        onDecrease = {
                            if (item.cantidad == 1) cartViewModel.removeItem(restaurantId, item)
                            else cartViewModel.decreaseQuantity(restaurantId, item)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.carrito_total),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = stringResource(R.string.carrito_precio_formato, totalPrice),
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
                    .clickable {
                        when {
                            authState.isGuest -> {
                                incompleteDialogIsGuest = true
                                showIncompleteDialog = true
                            }
                            !authViewModel.hasCompleteProfile() -> {
                                incompleteDialogIsGuest = false
                                showIncompleteDialog = true
                            }
                            else -> onGoToOrderSummary()
                        }
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.carrito_pagar),
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
private fun ProfileIncompleteDialog(
    isGuest: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlancoZesta,
        shape = RoundedCornerShape(24.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icono en círculo naranja
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(FondoSeleccionNaranjaZesta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = NaranjaZesta,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = if (isGuest)
                        stringResource(R.string.carrito_invitado_titulo)
                    else
                        stringResource(R.string.carrito_datos_incompletos_titulo),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoPrincipalZesta,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isGuest)
                        stringResource(R.string.carrito_invitado_descripcion)
                    else
                        stringResource(R.string.carrito_datos_incompletos_descripcion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundarioZesta,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Botón principal — gradiente azul
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)
                            )
                        )
                        .border(1.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                        .clickable { onConfirm() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isGuest)
                            stringResource(R.string.carrito_iniciar_sesion)
                        else
                            stringResource(R.string.carrito_ir_gestionar),
                        color = BlancoZesta,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Botón cancelar
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.carrito_cancelar),
                        color = TextoSecundarioZesta,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun CartDetailItemCard(
    restaurantId: Int,
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(item.imageKey) {
        if (item.imageKey.isBlank()) return@remember null
        val id = context.resources.getIdentifier(item.imageKey, "drawable", context.packageName)
        if (id != 0) id else null
    }

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
            if (imageResId != null) {
                Image(
                    painter = painterResource(imageResId),
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
                text = stringResource(R.string.carrito_precio_formato, item.precio),
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundarioZesta
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

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
                    Text("-", color = NegroZesta, style = MaterialTheme.typography.bodyLarge)
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
                Text("+", color = NegroZesta, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

}
