package com.zesta.app.ui.screens.cart

import androidx.compose.animation.AnimatedVisibility
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
import com.zesta.app.data.model.Order
import com.zesta.app.data.repository.CartRepository
import com.zesta.app.data.repository.OrderRepository
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import com.zesta.app.viewmodel.CartViewModel
import com.zesta.app.viewmodel.CartViewModelFactory
import kotlinx.coroutines.launch

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

    val subtotal = items.sumOf { it.precio * it.cantidad }

    var promoInput by remember { mutableStateOf("") }
    var promoApplied by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf(0.0) }
    var promoError by remember { mutableStateOf(false) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val discountAmount = subtotal * discount
    val total = subtotal - discountAmount

    val address = authState.currentUser?.direccion.orEmpty()

    fun applyPromo() {
        keyboardController?.hide()
        val result = orderRepository.validatePromoCode(promoInput)
        if (result != null) {
            discount = result
            promoApplied = promoInput.uppercase().trim()
            promoError = false
        } else {
            promoError = true
            discount = 0.0
            promoApplied = ""
        }
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
                // Restaurante header
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

                // Items
                items(items = items, key = { it.productId }) { item ->
                    OrderSummaryItemRow(item = item)
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocalOffer,
                                contentDescription = null,
                                tint = NaranjaZesta,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.pedido_codigo_promo),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoPrincipalZesta,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promoInput,
                                onValueChange = {
                                    promoInput = it.uppercase()
                                    promoError = false
                                },
                                modifier = Modifier.weight(1f),
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.pedido_codigo_placeholder),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextoSecundarioZesta
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                isError = promoError,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { applyPromo() }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BlancoZesta,
                                    unfocusedContainerColor = BlancoZesta,
                                    focusedBorderColor = if (promoApplied.isNotBlank()) VerdeExitoZesta else NaranjaZesta,
                                    unfocusedBorderColor = BordeCirculoZesta,
                                    errorBorderColor = MaterialTheme.colorScheme.error,
                                    cursorColor = TextoPrincipalZesta
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NaranjaZesta)
                                    .clickable { applyPromo() }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.pedido_aplicar),
                                    color = BlancoZesta,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        AnimatedVisibility(visible = promoError) {
                            Text(
                                text = stringResource(R.string.pedido_codigo_invalido),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        AnimatedVisibility(visible = promoApplied.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = VerdeExitoZesta,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(
                                        R.string.pedido_codigo_aplicado,
                                        promoApplied,
                                        (discount * 100).toInt()
                                    ),
                                    color = VerdeExitoZesta,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
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
                                discount = discountAmount,
                                total = total,
                                promoCode = promoApplied,
                                address = address
                            )
                            val result = orderRepository.placeOrder(order)
                            if (result.isSuccess) {
                                cartViewModel.clearCart()
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
private fun OrderSummaryItemRow(item: com.zesta.app.data.model.CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${item.cantidad}x ${item.nombre}",
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(
                R.string.carrito_precio_formato,
                item.precio * item.cantidad
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = TextoPrincipalZesta
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