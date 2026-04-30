package com.zesta.app.ui.screens.cart

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*
import com.zesta.app.ui.components.RatingDialog

@Composable
fun OrderSuccessScreen(
    showRatingDialog: Boolean = false,
    onGoHome: () -> Unit
) {
    var showDialog by remember { mutableStateOf(showRatingDialog) }

    // Animación del check
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = VerdeExitoZesta,
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale.value)
            )
            Text(
                text = "¡Pedido realizado!",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tu pedido está en camino. Recibirás una confirmación en breve.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundarioZesta,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryGradientButton(
                text = "Volver al inicio",
                onClick = onGoHome
            )
        }
    }

    // ── Dialog de valoración
    if (showDialog) {
        RatingDialog(
            onDismiss = {
                showDialog = false
                onGoHome()
            },
            onSubmit = {
                showDialog = false
                onGoHome()
            }
        )
    }
}

