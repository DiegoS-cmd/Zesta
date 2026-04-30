package com.zesta.app.ui.screens.cart

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.theme.*
import kotlinx.coroutines.delay

private const val TOTAL_DEMO_SECONDS = 30
private const val PREP_SECONDS = 10

@Composable
fun DeliveryTrackingScreen(
    restaurantName: String,
    restaurantStreet: String,
    userStreet: String,
    onFinished: () -> Unit,
    onGoHome: () -> Unit,
    restaurantId: Int,
    totalMinutes: Int
) {
    var segundosRestantes by remember { mutableIntStateOf(TOTAL_DEMO_SECONDS) }
    val totalSegundos = TOTAL_DEMO_SECONDS

    val fase by remember(segundosRestantes) {
        derivedStateOf {
            when {
                segundosRestantes > (totalSegundos - PREP_SECONDS) -> 0
                segundosRestantes > 0 -> 1
                else -> 2
            }
        }
    }

    val progresoGeneral by remember(segundosRestantes) {
        derivedStateOf { 1f - (segundosRestantes.toFloat() / totalSegundos.toFloat()) }
    }

    val progresoAnimado by animateFloatAsState(
        targetValue = progresoGeneral,
        animationSpec = tween(durationMillis = 700),
        label = "progreso_tracking"
    )

    LaunchedEffect(Unit) {
        while (segundosRestantes > 0) {
            delay(1000L)
            segundosRestantes--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (fase < 2) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HeaderTracking(
                    restaurantName = restaurantName,
                    segundosRestantes = segundosRestantes,
                    fase = fase
                )

                TrackingAddressesCard(
                    restaurantName = restaurantName,
                    restaurantStreet = restaurantStreet,
                    userStreet = userStreet,
                    progreso = progresoAnimado
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(BlancoZesta)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progresoAnimado },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = NaranjaZesta,
                        trackColor = FondoPlaceholderZesta
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FaseItem(
                            icon = Icons.Outlined.Restaurant,
                            label = stringResource(R.string.tracking_fase_preparando),
                            activa = fase >= 0,
                            completada = fase >= 1
                        )
                        FaseItem(
                            icon = Icons.Outlined.DeliveryDining,
                            label = stringResource(R.string.tracking_fase_en_camino),
                            activa = fase >= 1,
                            completada = fase >= 2
                        )
                        FaseItem(
                            icon = Icons.Outlined.Home,
                            label = stringResource(R.string.tracking_fase_entregado),
                            activa = fase >= 2,
                            completada = fase >= 2
                        )
                    }
                }
            }
        } else {
            EntregadoHeader(onFinished = onFinished)
        }

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
                .clickable { onGoHome() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.tracking_inicio),
                style = MaterialTheme.typography.bodyLarge,
                color = BlancoZesta,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HeaderTracking(
    restaurantName: String,
    segundosRestantes: Int,
    fase: Int
) {
    val minutosRestantes = segundosRestantes / 60
    val segsRestantes = segundosRestantes % 60

    val tiempoTexto = if (minutosRestantes > 0) {
        stringResource(
            R.string.tracking_tiempo_min_seg,
            minutosRestantes,
            segsRestantes
        )
    } else {
        stringResource(
            R.string.tracking_tiempo_seg,
            segsRestantes
        )
    }

    val estadoTexto = when (fase) {
        0 -> stringResource(R.string.tracking_estado_preparando, restaurantName)
        1 -> stringResource(R.string.tracking_estado_en_camino)
        else -> stringResource(R.string.tracking_fase_entregado)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = tiempoTexto,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipalZesta
        )
        Text(
            text = estadoTexto,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundarioZesta,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TrackingAddressesCard(
    restaurantName: String,
    restaurantStreet: String,
    userStreet: String,
    progreso: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(BlancoZesta)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = stringResource(R.string.tracking_seguimiento),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipalZesta
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddressPoint(
                title = restaurantName,
                subtitle = restaurantStreet,
                icon = Icons.Outlined.Restaurant,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.size(12.dp))

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .weight(1.2f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = NaranjaZesta,
                trackColor = FondoPlaceholderZesta
            )

            Spacer(modifier = Modifier.size(12.dp))

            AddressPoint(
                title = stringResource(R.string.tracking_casa),
                subtitle = userStreet,
                icon = Icons.Outlined.Home,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AddressPoint(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(FondoSeleccionNaranjaZesta)
                .border(1.5.dp, NaranjaZesta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = NaranjaZesta,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = subtitle.ifBlank { "Dirección no disponible" },
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundarioZesta,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FaseItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    activa: Boolean,
    completada: Boolean
) {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = if (activa && !completada) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    when {
                        completada -> NaranjaZesta
                        activa -> FondoSeleccionNaranjaZesta
                        else -> FondoPlaceholderZesta
                    }
                )
                .border(
                    1.5.dp,
                    if (activa) NaranjaZesta else BordeCirculoZesta,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (completada) Icons.Outlined.CheckCircle else icon,
                contentDescription = label,
                tint = when {
                    completada -> BlancoZesta
                    activa -> NaranjaZesta
                    else -> TextoSecundarioZesta
                },
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (activa) TextoPrincipalZesta else TextoSecundarioZesta,
            fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun EntregadoHeader(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500L)
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(NaranjaZesta),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = BlancoZesta,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.tracking_entregado_titulo),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipalZesta
        )

        Text(
            text = stringResource(R.string.tracking_entregado_subtitulo),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundarioZesta,
            textAlign = TextAlign.Center
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun DeliveryTrackingScreenPreview() {
    DeliveryTrackingScreen(
        restaurantName = "Burger Zesta",
        restaurantStreet = "Calle Gran Vía 45",
        userStreet = "Calle de la Paz 12",
        onFinished = {},
        onGoHome = {},
        restaurantId = 1,
        totalMinutes = 30
    )
}