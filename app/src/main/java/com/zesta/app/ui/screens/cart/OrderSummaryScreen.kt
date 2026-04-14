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
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zesta.app.R
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.model.Order
import com.zesta.app.data.repository.CartRepository
import com.zesta.app.data.repository.OrderRepository
import com.zesta.app.data.repository.PROMO_CODES
import com.zesta.app.data.repository.RestaurantRepository
import com.zesta.app.ui.screens.restaurant.PromoType
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.CartViewModel
import com.zesta.app.viewmodel.CartViewModelFactory
import kotlinx.coroutines.launch
import kotlin.compareTo

@Composable
fun OrderSummaryScreen(
    restaurantId: Int,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit,
    authViewModel: AuthViewModel
) {
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(repository = CartRepository())
    )
    val orderRepository = remember { OrderRepository() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val uiState by cartViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    val cartGroup = uiState.carts.firstOrNull { it.cart.restaurantId == restaurantId }
    val items = cartGroup?.items ?: emptyList()
    val restaurantName = cartGroup?.cart?.restaurantName ?: ""
    val restaurantImageName = cartGroup?.cart?.restaurantImageResName ?: ""

    // Restaurante para leer descuentos y promos por producto
    val restaurant = remember(restaurantId) {
        RestaurantRepository.getRestaurantById(restaurantId)
    }

    // Precios con promo de producto aplicada
    val subtotal = remember(items, restaurant) {
        items.sumOf { cartItem ->
            val product = restaurant?.products?.firstOrNull { it.id.toString() == cartItem.productId }
            calcularPrecioConPromo(cartItem, product?.promoType ?: PromoType.NONE)
        }
    }

    // Costes fijos
    val deliveryFee = if (restaurant?.hasFreeDelivery == true) 0.0 else (restaurant?.deliveryFee ?: 0.0)
    val serviceFee = 2.50

    // Promo manual
    var promoApplied by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf(0.0) }
    var promoError by remember { mutableStateOf(false) }
    var showPromoDialog by remember { mutableStateOf(false) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val discountAmount = subtotal * discount
    val total = subtotal + deliveryFee + serviceFee - discountAmount

    val address = authState.currentUser?.direccion.orEmpty()

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
                    text = stringResource(R.string.pedido_resumen_titulo),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Header restaurante
                item {
                    RestaurantSummaryHeader(
                        restaurantName = restaurantName,
                        restaurantImageName = restaurantImageName
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.pedido_tus_productos),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoPrincipalZesta,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Items con promo por producto
                items(items = items, key = { it.productId }) { cartItem ->
                    val product = restaurant?.products?.firstOrNull {
                        it.id.toString() == cartItem.productId
                    }
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
                                text = "${cartItem.cantidad}x ${cartItem.nombre}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoPrincipalZesta,
                                modifier = Modifier.weight(1f)
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                // Precio tachado si hay promo
                                if (ahorro > 0.0){
                                    Text(
                                        text = stringResource(R.string.carrito_precio_formato, precioNormal),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextoSecundarioZesta,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.carrito_precio_formato, precioFinal),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (ahorro > 0.0) NaranjaZesta else TextoPrincipalZesta,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        // Badge de ahorro
                        if (ahorro > 0.0) {
                            val promoLabel = when (promoType) {
                                PromoType.DOS_POR_UNO -> "2x1 · "
                                PromoType.DESCUENTO_20 -> "-20% · "
                                PromoType.DESCUENTO_10 -> "-10% · "
                                PromoType.NONE -> ""
                            }
                            Text(
                                text = "${promoLabel}Ahorras ${String.format("%.2f", ahorro)} €",
                                style = MaterialTheme.typography.bodySmall,
                                color = NaranjaZesta,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Dirección
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
                            text = stringResource(R.string.pedido_direccion_entrega),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundarioZesta
                        )
                        Text(
                            text = address.ifBlank { stringResource(R.string.pedido_sin_direccion) },
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Código promocional
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(FondoPlaceholderZesta)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocalOffer,
                                    contentDescription = null,
                                    tint = NaranjaZesta,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = stringResource(R.string.pedido_codigo_promo),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextoPrincipalZesta,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

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
                                            .background(NaranjaZesta)
                                            .clickable {
                                                promoApplied = ""
                                                discount = 0.0
                                                promoError = false
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "$applied · -${(discount * 100).toInt()}%",
                                            color = BlancoZesta,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Icon(
                                            imageVector = Icons.Outlined.CheckCircle,
                                            contentDescription = null,
                                            tint = BlancoZesta,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(FondoCirculoZesta)
                                            .border(1.dp, BordeCirculoZesta, RoundedCornerShape(20.dp))
                                            .clickable { showPromoDialog = true }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.pedido_codigo_añadir),
                                            color = TextoPrincipalZesta,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Chips de códigos disponibles
                        AnimatedVisibility(visible = promoApplied.isBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Códigos disponibles:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundarioZesta
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    PROMO_CODES.forEach { (code, pct) ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(FondoCirculoZesta)
                                                .border(1.dp, NaranjaZesta.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                                .clickable {
                                                    discount = pct
                                                    promoApplied = code
                                                    promoError = false
                                                }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "$code · -${(pct * 100).toInt()}%",
                                                color = NaranjaZesta,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Desglose de precios
                item {
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
                            value = stringResource(R.string.carrito_precio_formato, subtotal)
                        )

                        // Gastos de envío
                        if (deliveryFee == 0.0) {
                            PriceRow(
                                label = "Gastos de envío",
                                value = "Gratis",
                                valueColor = NaranjaZesta
                            )
                        } else {
                            PriceRow(
                                label = "Gastos de envío",
                                value = stringResource(R.string.carrito_precio_formato, deliveryFee)
                            )
                        }

                        // Precio de servicio
                        PriceRow(
                            label = "Precio de servicio",
                            value = stringResource(R.string.carrito_precio_formato, serviceFee)
                        )

                        // Descuento código promo
                        AnimatedVisibility(visible = discountAmount > 0) {
                            PriceRow(
                                label = stringResource(
                                    R.string.pedido_descuento,
                                    (discount * 100).toInt()
                                ),
                                value = "- ${stringResource(R.string.carrito_precio_formato, discountAmount)}",
                                valueColor = VerdeExitoZesta
                            )
                        }

                        HorizontalDivider(color = BordeCirculoZesta)

                        // Total final
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.carrito_total),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipalZesta,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.carrito_precio_formato, total),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipalZesta,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }

            // Botón confirmar pedido
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
                    .clickable(enabled = !isPlacingOrder) {
                        scope.launch {
                            isPlacingOrder = true
                            val order = Order(
                                restaurantId = restaurantId,
                                restaurantName = restaurantName,
                                restaurantImageResName = restaurantImageName,
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
                                onOrderPlaced()
                            }
                            isPlacingOrder = false
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
                        text = stringResource(R.string.pedido_confirmar),
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

// Composables

@Composable
private fun RestaurantSummaryHeader(
    restaurantName: String,
    restaurantImageName: String
) {
    val context = LocalContext.current
    val imageResId = remember(restaurantImageName) {
        if (restaurantImageName.isBlank()) return@remember null
        val id = context.resources.getIdentifier(restaurantImageName, "drawable", context.packageName)
        if (id != 0) id else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FondoPlaceholderZesta)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageResId != null) {
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
            text = restaurantName,
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    valueColor: Color = TextoPrincipalZesta
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoSecundarioZesta
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Lógica de promos por producto

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

// código promo

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FondoSeleccionNaranjaZesta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = NaranjaZesta,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.pedido_codigo_promo),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        input = it.uppercase()
                        onErrorReset()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.pedido_codigo_placeholder),
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
                        .clickable {
                            keyboardController?.hide()
                            onApply(input)
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.pedido_aplicar),
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