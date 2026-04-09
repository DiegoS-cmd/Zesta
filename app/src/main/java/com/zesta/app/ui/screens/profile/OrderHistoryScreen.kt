package com.zesta.app.ui.screens.profile

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
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zesta.app.data.model.CartItem
import com.zesta.app.data.model.Order
import com.zesta.app.ui.theme.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

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
                    text = "Historial de pedidos",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                                text = "Aún no tienes pedidos",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipalZesta,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Cuando realices tu primer pedido aparecerá aquí",
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

@Composable
private fun OrderHistoryCard(order: Order) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy · HH:mm", Locale("es", "ES")) }
    val dateString = remember(order.timestamp) {
        dateFormat.format(order.timestamp.toDate())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FondoPlaceholderZesta)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.restaurantName,
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = dateString,
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundarioZesta
            )
        }

        HorizontalDivider(color = BordeCirculoZesta)

        order.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.cantidad}x ${item.nombre}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoPrincipalZesta
                )
                Text(
                    text = String.format(Locale("es", "ES"), "%.2f €", item.precio * item.cantidad),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundarioZesta
                )
            }
        }

        HorizontalDivider(color = BordeCirculoZesta)

        if (!order.promoCode.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Código: ${order.promoCode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = VerdeExitoZesta
                )
                Text(
                    text = "- ${String.format(Locale("es", "ES"), "%.2f €", order.discount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = VerdeExitoZesta
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = String.format(Locale("es", "ES"), "%.2f €", order.total),
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}