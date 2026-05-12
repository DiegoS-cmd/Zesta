package com.zesta.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zesta.app.R
import com.zesta.app.ui.theme.BordeCirculoZesta
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.NaranjaZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta
import com.zesta.app.ui.theme.TextoSecundarioZesta

@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    var selectedStars by remember { mutableStateOf(0) }

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
            // Icono
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
                text = stringResource(R.string.rating_titulo),
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.rating_subtitulo),
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundarioZesta,
                textAlign = TextAlign.Center
            )

            // Estrellas
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
                        imageVector = if (star <= selectedStars)
                            Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "$star estrellas",
                        tint = if (star <= selectedStars) NaranjaZesta else BordeCirculoZesta,
                        modifier = Modifier
                            .size(40.dp)
                            .scale(starScale.value)
                            .clickable { selectedStars = star }
                    )
                }
            }

            // Texto según estrellas
            if (selectedStars > 0) {
                Text(
                    text = when (selectedStars) {
                        1 -> stringResource(R.string.rating_texto_1)
                        2 -> stringResource(R.string.rating_texto_2)
                        3 -> stringResource(R.string.rating_texto_3)
                        4 -> stringResource(R.string.rating_texto_4)
                        5 -> stringResource(R.string.rating_texto_5)
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
            PrimaryGradientButton(
                text = if (selectedStars > 0) stringResource(R.string.rating_enviar)
                else stringResource(R.string.rating_ahora_no),
                onClick = {
                    if (selectedStars > 0) onSubmit(selectedStars) else onDismiss()
                }
            )

            // Cancelar
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.rating_cancelar),
                    color = TextoSecundarioZesta,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}