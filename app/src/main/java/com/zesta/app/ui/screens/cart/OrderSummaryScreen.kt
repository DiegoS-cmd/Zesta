package com.zesta.app.ui.screens.cart

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.model.Order
import com.zesta.app.data.repository.OrderRepository
import com.zesta.app.data.repository.PROMO_CODES
import com.zesta.app.data.repository.RestaurantRepository
import com.zesta.app.data.model.PromoType
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.CartViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun OrderSummaryScreen(
    restaurantId: Int,
    onBack: () -> Unit,
    cartViewModel: CartViewModel,
    onOrderPlaced: (
        restaurantId: Int,
        totalMinutes: Int,
        restaurantName: String,
        restaurantStreet: String,
        userStreet: String
    ) -> Unit,
    authViewModel: AuthViewModel
) {
    val errorCarritoVacio = stringResource(R.string.pedido_error_carrito_vacio)
    val errorPedidoFallo = stringResource(R.string.pedido_error_fallo)
    val context = LocalContext.current
    val orderRepository = remember { OrderRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by cartViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    val cartGroup = uiState.carts.firstOrNull { it.cart.restaurantId == restaurantId }
    val items = cartGroup?.items ?: emptyList()
    val restaurantName = cartGroup?.cart?.restaurantName ?: ""
    val restaurantImageResName = cartGroup?.cart?.restaurantImageResName ?: ""


    val restaurant = remember(restaurantId) {
        RestaurantRepository.getRestaurantById(restaurantId)
    }

    val resolvedRestaurantName = restaurant?.let {
        stringResource(it.nameRes) } ?: restaurantName

    val subtotal = remember(items, restaurant) {
        items.sumOf { cartItem ->
            val product = restaurant?.products?.firstOrNull { it.id.toString() == cartItem.productId }
            calcularPrecioConPromo(cartItem, product?.promoType ?: PromoType.NONE)
        }
    }

    val deliveryFee = if (restaurant?.hasFreeDelivery == true) 0.0 else (restaurant?.deliveryFee ?: 0.0)
    val serviceFee = 2.50

    var promoApplied by remember { mutableStateOf("") }
    var discount by remember { mutableDoubleStateOf(0.0) }
    var promoError by remember { mutableStateOf(false) }
    var showPromoDialog by remember { mutableStateOf(false) }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }

    val discountAmount = subtotal * discount
    val total = subtotal + deliveryFee + serviceFee - discountAmount
    val address = authState.currentUser?.direccion.orEmpty()
    val canPlaceOrder = items.isNotEmpty() && address.isNotBlank() && !isPlacingOrder



    if (showPromoDialog) {
        PromoCodeDialog(
            onDismiss = { showPromoDialog = false },
            onApply = { code ->
                val result = orderRepository.validatePromoCode(code)
                if (result != null) {
                    discount = result
                    promoApplied = code.uppercase().trim()
                    promoError = false
                    showPromoDialog = false
                } else {
                    promoError = true
                }
            },
            hasError = promoError,
            onErrorReset = { promoError = false }
        )
    }

    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            containerColor = BlancoZesta,
            shape = RoundedCornerShape(24.dp),
            title = null,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(FondoSeleccionNaranjaZesta),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Warning, null, tint = NaranjaZesta, modifier = Modifier.size(32.dp))
                    }
                    Text(
                        stringResource(R.string.pedido_direccion_sin_datos),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoPrincipalZesta,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        stringResource(R.string.pedido_direccion_falta_descripcion),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundarioZesta,
                        textAlign = TextAlign.Center
                    )
                    TextButton(
                        onClick = { showAddressDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.carrito_cancelar),
                            color = TextoSecundarioZesta,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        containerColor = FondoZesta,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

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
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.accesibilidad_volver),
                        tint = NegroZesta,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    stringResource(R.string.pedido_resumen_titulo),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.pedido_error_carrito_vacio),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextoSecundarioZesta
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        RestaurantSummaryHeader(
                            restaurantName = restaurantName,
                            restaurantImageResName = restaurantImageResName
                        )
                    }

                    item {
                        Text(
                            stringResource(R.string.pedido_tus_productos),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(items = items, key = { it.productId }) { cartItem ->
                        val product = restaurant?.products?.firstOrNull { it.id.toString() == cartItem.productId }
                        val promoType = product?.promoType ?: PromoType.NONE
                        val precioNormal = cartItem.precio * cartItem.cantidad
                        val precioFinal = calcularPrecioConPromo(cartItem, promoType)
                        val ahorro = precioNormal - precioFinal

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${cartItem.cantidad}x ${cartItem.nombre}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextoPrincipalZesta,
                                    modifier = Modifier.weight(1f)
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    if (ahorro > 0.0) {
                                        Text(
                                            stringResource(R.string.carrito_precio_formato, precioNormal),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextoSecundarioZesta,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    }
                                    Text(
                                        stringResource(R.string.carrito_precio_formato, precioFinal),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (ahorro > 0.0) NaranjaZesta else TextoPrincipalZesta,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            if (ahorro > 0.0) {
                                val promoLabel = when (promoType) {
                                    PromoType.DOS_POR_UNO -> stringResource(R.string.promo_label_2x1)
                                    PromoType.DESCUENTO_20 -> stringResource(R.string.promo_label_20)
                                    PromoType.DESCUENTO_10 -> stringResource(R.string.promo_label_10)
                                    PromoType.NONE -> ""
                                }
                                Text(
                                    stringResource(R.string.pedido_ahorro_promo, promoLabel, String.format(Locale.getDefault(), "%.2f", ahorro)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NaranjaZesta,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // ── Dirección
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(FondoPlaceholderZesta)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                stringResource(R.string.pedido_direccion_entrega),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundarioZesta
                            )
                            Text(
                                text = address.ifBlank { stringResource(R.string.pedido_sin_direccion) },
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (address.isBlank()) NaranjaZesta else TextoPrincipalZesta,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // ── Códigos promocionales
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(FondoPlaceholderZesta)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(FondoSeleccionNaranjaZesta),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.LocalOffer, null, tint = NaranjaZesta, modifier = Modifier.size(17.dp))
                                    }
                                    Text(
                                        stringResource(R.string.pedido_codigo_promo),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextoPrincipalZesta,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                AnimatedContent(
                                    targetState = promoApplied,
                                    transitionSpec = {
                                        (slideInVertically { it } + fadeIn()).togetherWith(
                                            slideOutVertically { -it } + fadeOut()
                                        )
                                    },
                                    label = "promo_chip"
                                ) { applied ->
                                    if (applied.isNotBlank()) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(FondoSeleccionNaranjaZesta)
                                                .border(1.5.dp, NaranjaZesta, RoundedCornerShape(20.dp))
                                                .clickable { promoApplied = ""; discount = 0.0; promoError = false }
                                                .padding(horizontal = 12.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Outlined.CheckCircle, null, tint = NaranjaZesta, modifier = Modifier.size(15.dp))
                                            Text(applied, color = NaranjaZesta, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                            Text(
                                                stringResource(R.string.pedido_descuento_pct, (discount * 100).toInt()),
                                                color = NaranjaZesta,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Icon(Icons.Outlined.Close, null, tint = NaranjaZesta.copy(alpha = 0.7f), modifier = Modifier.size(13.dp))
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(FondoCirculoZesta)
                                                .border(1.dp, BordeCirculoZesta, RoundedCornerShape(20.dp))
                                                .clickable { showPromoDialog = true }
                                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                        ) {
                                            Text(
                                                stringResource(R.string.pedido_codigo_añadir),
                                                color = TextoPrincipalZesta,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            AnimatedVisibility(visible = promoApplied.isBlank()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        stringResource(R.string.pedido_codigos_disponibles),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextoSecundarioZesta
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        PROMO_CODES.entries.chunked(2).forEach { rowItems ->
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                rowItems.forEach { (code, pct) ->
                                                    Row(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(20.dp))
                                                            .background(FondoSeleccionNaranjaZesta)
                                                            .border(1.dp, NaranjaZesta.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                                            .clickable { discount = pct; promoApplied = code; promoError = false }
                                                            .padding(horizontal = 12.dp, vertical = 7.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                    ) {
                                                        Icon(Icons.Outlined.LocalOffer, null, tint = NaranjaZesta, modifier = Modifier.size(13.dp))
                                                        Text(code, color = NaranjaZesta, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                                        Text(
                                                            stringResource(R.string.pedido_descuento_pct, (pct * 100).toInt()),
                                                            color = NaranjaZesta.copy(alpha = 0.8f),
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            AnimatedVisibility(visible = promoApplied.isNotBlank()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(FondoSeleccionNaranjaZesta)
                                        .border(1.dp, NaranjaZesta.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Outlined.CheckCircle, null, tint = NaranjaZesta, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text(
                                            stringResource(R.string.pedido_promo_aplicada, promoApplied, (discount * 100).toInt()),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = NaranjaZesta,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            stringResource(R.string.pedido_promo_quitar_hint),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NaranjaZesta.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Resumen de precios
                    item {
                        val subtotalSinPromo = items.sumOf { it.precio * it.cantidad }
                        val ahorroPromos = subtotalSinPromo - subtotal
                        val hasPromoDescuento = ahorroPromos > 0.01

                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(FondoPlaceholderZesta)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PriceRow(
                                label = stringResource(R.string.pedido_subtotal),
                                value = stringResource(R.string.carrito_precio_formato, subtotalSinPromo)
                            )

                            if (deliveryFee == 0.0) {
                                PriceRow(
                                    label = stringResource(R.string.pedido_gastos_envio),
                                    value = stringResource(R.string.pedido_gratis),
                                    valueColor = NaranjaZesta
                                )
                            } else {
                                PriceRow(
                                    label = stringResource(R.string.pedido_gastos_envio),
                                    value = stringResource(R.string.carrito_precio_formato, deliveryFee)
                                )
                            }

                            PriceRow(
                                label = stringResource(R.string.pedido_precio_servicio),
                                value = stringResource(R.string.carrito_precio_formato, serviceFee)
                            )

                            AnimatedVisibility(visible = hasPromoDescuento) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NaranjaZesta.copy(alpha = 0.08f))
                                        .border(1.dp, NaranjaZesta.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.LocalOffer, null, tint = NaranjaZesta, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            stringResource(R.string.pedido_ofertas_especiales),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = NaranjaZesta,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        stringResource(R.string.pedido_ahorro_importe, String.format(Locale.getDefault(), "%.2f", ahorroPromos)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NaranjaZesta,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AnimatedVisibility(visible = discountAmount > 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AzulInicioGradienteZesta.copy(alpha = 0.08f))
                                        .border(1.dp, AzulInicioGradienteZesta.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.LocalOffer, null, tint = AzulInicioGradienteZesta, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                stringResource(R.string.pedido_codigo_promocional),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = AzulInicioGradienteZesta,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            if (promoApplied.isNotBlank()) {
                                                Text(
                                                    promoApplied,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = AzulInicioGradienteZesta.copy(alpha = 0.75f)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        stringResource(R.string.pedido_ahorro_importe, String.format(Locale.getDefault(), "%.2f", discountAmount)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AzulInicioGradienteZesta,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            HorizontalDivider(color = BordeCirculoZesta)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    stringResource(R.string.carrito_total),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextoPrincipalZesta,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    stringResource(R.string.carrito_precio_formato, total),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextoPrincipalZesta,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }

            // ── Botón confirmar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (canPlaceOrder)
                                listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)
                            else
                                listOf(Color.LightGray, Color.Gray)
                        )
                    )
                    .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                    .clickable(enabled = !isPlacingOrder) {
                        when {
                            items.isEmpty() -> scope.launch {
                                snackbarHostState.showSnackbar(errorCarritoVacio)
                            }
                            address.isBlank() -> showAddressDialog = true
                            else -> {
                                scope.launch {
                                    isPlacingOrder = true
                                    val order = Order(
                                        restaurantId = restaurantId,
                                        restaurantName = restaurantName,
                                        restaurantImageResName = restaurantImageResName,
                                        items = items,
                                        subtotal = subtotal,
                                        deliveryFee = deliveryFee,
                                        serviceFee = serviceFee,
                                        discount = discountAmount,
                                        total = total,
                                        promoCode = promoApplied.ifBlank { null },
                                        address = address
                                    )
                                    val result = orderRepository.placeOrder(order)
                                    if (result.isSuccess) {
                                        cartViewModel.clearCartByRestaurant(restaurantId)

                                        val totalMinutes = 30
                                        val restaurantStreet = restaurant?.let { context.getString(it.addressRes) }.orEmpty()

                                        onOrderPlaced(
                                            restaurantId,
                                            totalMinutes,
                                            resolvedRestaurantName,
                                            restaurantStreet,
                                            address
                                        )
                                    } else {
                                        snackbarHostState.showSnackbar(
                                            result.exceptionOrNull()?.message ?: errorPedidoFallo
                                        )
                                    }
                                    isPlacingOrder = false
                                }
                            }
                        }
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = BlancoZesta,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        stringResource(R.string.pedido_confirmar),
                        style = MaterialTheme.typography.bodyLarge,
                        color = BlancoZesta,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RestaurantSummaryHeader(
    restaurantName: String,
    restaurantImageResName: String
) {
    val context = LocalContext.current
    val imageResId = remember(restaurantImageResName) {
        if (restaurantImageResName.isBlank()) return@remember 0
        context.resources.getIdentifier(restaurantImageResName, "drawable", context.packageName)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FondoPlaceholderZesta)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageResId != 0) {
            Image(
                painter = painterResource(imageResId),
                contentDescription = restaurantName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BlancoZesta)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            restaurantName,
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.SemiBold
        )

    }
}

@Composable
private fun PriceRow(label: String, value: String, valueColor: Color = TextoPrincipalZesta) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextoSecundarioZesta)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = valueColor, fontWeight = FontWeight.SemiBold)
    }
}

fun calcularPrecioConPromo(item: CartItem, promoType: PromoType): Double {
    return when (promoType) {
        PromoType.DOS_POR_UNO -> {
            val pagadas = (item.cantidad / 2) + (item.cantidad % 2)
            pagadas * item.precio
        }
        PromoType.DESCUENTO_20 -> item.precio * item.cantidad * 0.80
        PromoType.DESCUENTO_10 -> item.precio * item.cantidad * 0.90
        PromoType.NONE -> item.precio * item.cantidad
    }
}

@Composable
private fun PromoCodeDialog(
    onDismiss: () -> Unit,
    onApply: (String) -> Unit,
    hasError: Boolean,
    onErrorReset: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BlancoZesta,
        shape = RoundedCornerShape(24.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(FondoSeleccionNaranjaZesta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocalOffer, null, tint = NaranjaZesta, modifier = Modifier.size(28.dp))
                }
                Text(
                    stringResource(R.string.pedido_codigo_promo),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.uppercase(); onErrorReset() },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.pedido_codigo_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundarioZesta
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    isError = hasError,
                    supportingText = if (hasError) {
                        { Text(stringResource(R.string.pedido_codigo_invalido)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        onApply(input)
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BlancoZesta,
                        unfocusedContainerColor = BlancoZesta,
                        focusedBorderColor = NaranjaZesta,
                        unfocusedBorderColor = BordeCirculoZesta,
                        cursorColor = TextoPrincipalZesta
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(NaranjaZesta)
                        .clickable { keyboardController?.hide(); onApply(input) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.pedido_aplicar),
                        color = BlancoZesta,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.carrito_cancelar),
                        color = TextoSecundarioZesta,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {}
    )
}