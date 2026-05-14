package com.zesta.app.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.model.Order
import com.zesta.app.data.repository.RatingRepository
import com.zesta.app.R
import com.zesta.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

// Pantalla de historial: carga los pedidos del usuario desde Firestore al entrar
@Composable
fun OrderHistoryScreen(onBack: () -> Unit) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("orders")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                orders = snapshot.documents.mapNotNull { doc ->
                    try {
                        val data = doc.data ?: return@mapNotNull null
                        // El timestamp puede venir como Timestamp o como Long según cómo se guardó
                        val ts = when (val raw = data["timestamp"]) {
                            is Timestamp -> raw
                            is Long -> Timestamp(raw / 1000, 0)
                            else -> Timestamp.now()
                        }
                        @Suppress("UNCHECKED_CAST")
                        val rawItems = data["items"] as? List<Map<String, Any>> ?: emptyList()
                        val cartItems = rawItems.map { map ->
                            CartItem(
                                productId = map["productId"] as? String ?: "",
                                restaurantId = (map["restaurantId"] as? Long)?.toInt() ?: 0,
                                nombre = map["nombre"] as? String ?: "",
                                precio = (map["precio"] as? Double)
                                    ?: (map["precio"] as? Long)?.toDouble() ?: 0.0,
                                cantidad = (map["cantidad"] as? Long)?.toInt() ?: 1,
                                imageKey = map["imageKey"] as? String ?: ""
                            )
                        }
                        Order(
                            orderId = doc.id,
                            restaurantId = (data["restaurantId"] as? Long)?.toInt() ?: 0,
                            restaurantName = data["restaurantName"] as? String ?: "",
                            restaurantImageResName = data["restaurantImageResName"] as? String ?: "",
                            items = cartItems,
                            subtotal = (data["subtotal"] as? Double)
                                ?: (data["subtotal"] as? Long)?.toDouble() ?: 0.0,
                            deliveryFee = (data["deliveryFee"] as? Double)
                                ?: (data["deliveryFee"] as? Long)?.toDouble() ?: 0.0,
                            serviceFee = (data["serviceFee"] as? Double)
                                ?: (data["serviceFee"] as? Long)?.toDouble() ?: 2.50,
                            discount = (data["discount"] as? Double)
                                ?: (data["discount"] as? Long)?.toDouble() ?: 0.0,
                            total = (data["total"] as? Double)
                                ?: (data["total"] as? Long)?.toDouble() ?: 0.0,
                            promoCode = data["promoCode"] as? String,
                            address = data["address"] as? String ?: "",
                            timestamp = ts,
                            status = data["status"] as? String ?: "confirmed"
                        )
                    } catch (_: Exception) { null }
                }
            } catch (_: Exception) {}
        }
        isLoading = false
    }

    Scaffold(containerColor = FondoZesta) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // TopBar con botón de volver
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
                    text = stringResource(R.string.historial_titulo),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tres estados: cargando, lista vacía o lista con pedidos
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NaranjaZesta)
                    }
                }
                orders.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = TextoSecundarioZesta,
                                modifier = Modifier.size(52.dp)
                            )
                            Text(
                                text = stringResource(R.string.historial_vacio_titulo),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipalZesta,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.historial_vacio_descripcion),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundarioZesta,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(orders) { order ->
                            OrderHistoryCard(order = order)
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

// Tarjeta de un pedido: muestra items, desglose de precios, descuentos y valoración
@Composable
private fun OrderHistoryCard(order: Order) {
    val locale = Locale("es", "ES")
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy · HH:mm", locale) }
    val dateString = remember(order.timestamp) { dateFormat.format(order.timestamp.toDate()) }
    val ratingRepository = remember { RatingRepository() }
    var userRating by remember { mutableStateOf<Int?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var ratingLoading by remember { mutableStateOf(false) }

    // Strings para usar dentro de lambdas
    val strVerMenos = stringResource(R.string.historial_ver_menos)
    val strSubtotal = stringResource(R.string.historial_subtotal)
    val strGastosEnvio = stringResource(R.string.historial_gastos_envio)
    val strEnvioGratis = stringResource(R.string.historial_envio_gratis)
    val strTarifaServicio = stringResource(R.string.historial_tarifa_servicio)
    val strOfertasEspeciales = stringResource(R.string.historial_ofertas_especiales)
    val strCodigoPromo = stringResource(R.string.historial_codigo_promo)
    val strTotal = stringResource(R.string.historial_total)
    val strValorar = stringResource(R.string.historial_valorar)

    LaunchedEffect(order.orderId) {
        userRating = ratingRepository.getUserRating(order.restaurantId)
    }

    // Subtotal real  (sin promos)
    val subtotalSinPromo = order.items.sumOf { it.precio * it.cantidad }

    // Ahorro por promos de producto = diferencia entre subtotal sin promo y el guardado
    val ahorroPromos = subtotalSinPromo - order.subtotal

    // Margen de 0.01 para evitar falsos positivos por errores de algun float
    val hasPromoDescuento = ahorroPromos > 0.01
    val hasCodigoPromo = order.discount > 0.0
    val hasDiscount = hasPromoDescuento || hasCodigoPromo

    val precioOriginal = if (hasDiscount) order.total + order.discount + ahorroPromos else null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FondoPlaceholderZesta)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Cabecera: nombre del restaurante y fecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = order.restaurantName,
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = dateString,
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundarioZesta
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = BordeCirculoZesta)
        Spacer(modifier = Modifier.height(10.dp))

        // Mostramos los 2 primeros items siempre; el resto se expande
        val itemsPreview = order.items.take(2)
        val itemsRest = order.items.drop(2)

        itemsPreview.forEach { item ->
            OrderItemRow(item = item, locale = locale)
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (itemsRest.isNotEmpty()) {
            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column {
                    itemsRest.forEach { item ->
                        OrderItemRow(item = item, locale = locale)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = NaranjaZesta,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (expanded) strVerMenos
                    else stringResource(R.string.historial_ver_mas, itemsRest.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = NaranjaZesta,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = BordeCirculoZesta)
        Spacer(modifier = Modifier.height(10.dp))

        // Desglose de precios
        PriceRow(
            label = strSubtotal,
            value = String.format(locale, "%.2f €", subtotalSinPromo),
            labelColor = TextoSecundarioZesta,
            valueColor = TextoSecundarioZesta
        )
        Spacer(modifier = Modifier.height(4.dp))

        PriceRow(
            label = strGastosEnvio,
            value = if (order.deliveryFee == 0.0) strEnvioGratis
            else String.format(locale, "%.2f €", order.deliveryFee),
            labelColor = TextoSecundarioZesta,
            valueColor = if (order.deliveryFee == 0.0) VerdeExitoZesta else TextoSecundarioZesta
        )
        Spacer(modifier = Modifier.height(4.dp))

        PriceRow(
            label = strTarifaServicio,
            value = String.format(locale, "%.2f €", order.serviceFee),
            labelColor = TextoSecundarioZesta,
            valueColor = TextoSecundarioZesta
        )

        // Descuento por ofertas de producto
        if (hasPromoDescuento) {
            Spacer(modifier = Modifier.height(10.dp))
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
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = NaranjaZesta,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strOfertasEspeciales,
                        style = MaterialTheme.typography.labelMedium,
                        color = NaranjaZesta,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "- ${String.format(locale, "%.2f €", ahorroPromos)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NaranjaZesta,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Descuento por código promocional
        if (hasCodigoPromo) {
            Spacer(modifier = Modifier.height(8.dp))
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
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = AzulInicioGradienteZesta,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = strCodigoPromo,
                            style = MaterialTheme.typography.labelMedium,
                            color = AzulInicioGradienteZesta,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!order.promoCode.isNullOrBlank()) {
                            Text(
                                text = order.promoCode,
                                style = MaterialTheme.typography.labelSmall,
                                color = AzulInicioGradienteZesta.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
                Text(
                    text = "- ${String.format(locale, "%.2f €", order.discount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AzulInicioGradienteZesta,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = BordeCirculoZesta)
        Spacer(modifier = Modifier.height(10.dp))

        // Total: si hubo descuentos se muestra el precio original tachado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strTotal,
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (precioOriginal != null) {
                    Text(
                        text = String.format(locale, "%.2f €", precioOriginal),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundarioZesta,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                Text(
                    text = String.format(locale, "%.2f €", order.total),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = BordeCirculoZesta)
        Spacer(modifier = Modifier.height(8.dp))

        // Botón de valoración: naranja si ya valoró, gris si no
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (userRating != null) FondoSeleccionNaranjaZesta else FondoCirculoZesta
                    )
                    .border(
                        1.dp,
                        if (userRating != null) NaranjaZesta else BordeCirculoZesta,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { showRatingDialog = true }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (userRating != null) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = NaranjaZesta,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = if (userRating != null)
                        stringResource(R.string.historial_tu_valoracion, userRating!!)
                    else strValorar,
                    style = MaterialTheme.typography.bodySmall,
                    color = NaranjaZesta,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (showRatingDialog) {
        com.zesta.app.ui.components.RestaurantRatingDialog(
            restaurantName = order.restaurantName,
            initialStars = userRating ?: 0,
            isLoading = ratingLoading,
            onDismiss = { showRatingDialog = false },
            onSubmit = { stars ->
                scope.launch {
                    ratingLoading = true
                    ratingRepository.saveRating(order.restaurantId, stars)
                    userRating = stars
                    ratingLoading = false
                    showRatingDialog = false
                }
            }
        )
    }
}

// Fila de un producto del pedido: burbuja con cantidad, nombre y precio total del item
@Composable
private fun OrderItemRow(item: CartItem, locale: Locale) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(NaranjaZesta.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${item.cantidad}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NaranjaZesta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.nombre,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPrincipalZesta,
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = String.format(locale, "%.2f €", item.precio * item.cantidad),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundarioZesta,
            fontWeight = FontWeight.Medium
        )
    }
}

// Fila genérica de precio: etiqueta a la izquierda, valor a la derecha
@Composable
private fun PriceRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = labelColor)
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = valueColor, fontWeight = FontWeight.Medium)
    }
}