package com.zesta.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zesta.app.ui.theme.*

@Composable
fun RestaurantRatingDialog(
    restaurantName: String,
    initialStars: Int = 0,          // 0 = no ha valorado antes
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    var selectedStars by remember { mutableStateOf(initialStars) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(FondoPlaceholderZesta)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icono estrella
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(NaranjaZesta.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = NaranjaZesta,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = if (initialStars > 0) "Editar valoración" else "Valorar restaurante",
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = restaurantName,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundarioZesta,
                textAlign = TextAlign.Center
            )

            // Estrellas con animación
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..5).forEach { star ->
                    val starScale = remember { Animatable(1f) }
                    LaunchedEffect(selectedStars) {
                        if (star == selectedStars) {
                            starScale.animateTo(1.3f, spring(Spring.DampingRatioMediumBouncy))
                            starScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy))
                        }
                    }
                    Icon(
                        imageVector = if (star <= selectedStars) Icons.Filled.Star
                        else Icons.Outlined.StarOutline,
                        contentDescription = "$star estrellas",
                        tint = if (star <= selectedStars) NaranjaZesta else BordeCirculoZesta,
                        modifier = Modifier
                            .size(40.dp)
                            .scale(starScale.value)
                            .clickable { selectedStars = star }
                    )
                }
            }

            // Texto reactivo
            if (selectedStars > 0) {
                Text(
                    text = when (selectedStars) {
                        1 -> "😞 Muy mala experiencia"
                        2 -> "😕 Podría mejorar"
                        3 -> "😊 Bastante bien"
                        4 -> "😄 Muy bueno"
                        5 -> "🤩 ¡Excelente!"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = NaranjaZesta,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón enviar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        if (selectedStars > 0) NaranjaZesta
                        else BordeCirculoZesta
                    )
                    .clickable(enabled = selectedStars > 0 && !isLoading) {
                        onSubmit(selectedStars)
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = BlancoZesta,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (initialStars > 0) "Guardar cambios" else "Enviar valoración",
                        color = BlancoZesta,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = TextoSecundarioZesta,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}