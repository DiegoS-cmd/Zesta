package com.zesta.app.ui.screens.cart

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import com.zesta.app.R
import com.zesta.app.ui.theme.*
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline as OsmPolyline

private const val TOTAL_DEMO_SECONDS = 30
private const val PREP_SECONDS = 10

@Composable
fun DeliveryTrackingScreen(
    restaurantId: Int,
    totalMinutes: Int,
    restaurantName: String,
    restaurantLat: Double,
    restaurantLon: Double,
    userLat: Double,
    userLon: Double,
    onBack: () -> Unit,
    onFinished: () -> Unit
) {
    BackHandler { onBack() }

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

    val progreso by remember(segundosRestantes) {
        derivedStateOf { 1f - (segundosRestantes.toFloat() / totalSegundos.toFloat()) }
    }

    LaunchedEffect(Unit) {
        while (segundosRestantes > 0) {
            delay(1000L)
            segundosRestantes--
        }
    }

    val repartidorFraction by remember(segundosRestantes) {
        derivedStateOf {
            val viajeTotal = (totalSegundos - PREP_SECONDS).toFloat()
            val viajeTranscurrido =
                (totalSegundos - PREP_SECONDS - segundosRestantes.coerceAtMost(totalSegundos - PREP_SECONDS)).toFloat()
            if (fase >= 1 && viajeTotal > 0f) {
                (viajeTranscurrido / viajeTotal).coerceIn(0f, 1f)
            } else 0f
        }
    }

    val repartidorLat = restaurantLat + (userLat - restaurantLat) * repartidorFraction
    val repartidorLon = restaurantLon + (userLon - restaurantLon) * repartidorFraction

    Column(modifier = Modifier.fillMaxSize().background(FondoZesta)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.52f)
        ) {
            OsmMapView(
                restaurantLat = restaurantLat,
                restaurantLon = restaurantLon,
                userLat = userLat,
                userLon = userLon,
                repartidorLat = repartidorLat,
                repartidorLon = repartidorLon,
                fase = fase,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(BlancoZesta)
                    .border(1.dp, BordeCirculoZesta, CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.accesibilidad_volver),
                    tint = TextoPrincipalZesta
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(FondoZesta)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (fase < 2) {
                val minutosRestantes = segundosRestantes / 60
                val segsRestantes = segundosRestantes % 60
                val tiempoTexto = if (minutosRestantes > 0)
                    stringResource(R.string.tracking_tiempo_min_seg, minutosRestantes, segsRestantes)
                else
                    stringResource(R.string.tracking_tiempo_seg, segsRestantes)

                val estadoTexto = when (fase) {
                    0 -> stringResource(R.string.tracking_estado_preparando, restaurantName)
                    else -> stringResource(R.string.tracking_estado_en_camino)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
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
            } else {
                EntregadoHeader(onFinished = onFinished)
            }

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
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
}

@Composable
private fun OsmMapView(
    restaurantLat: Double,
    restaurantLon: Double,
    userLat: Double,
    userLon: Double,
    repartidorLat: Double,
    repartidorLon: Double,
    fase: Int,
    modifier: Modifier = Modifier
) {
    val labelRestaurante = stringResource(R.string.tracking_marker_restaurante)
    val snippetRestaurante = stringResource(R.string.tracking_marker_restaurante_snippet)
    val labelUsuario = stringResource(R.string.tracking_marker_usuario)
    val labelRepartidor = stringResource(R.string.tracking_marker_repartidor)

    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().userAgentValue = ctx.packageName
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(14.0)
                val midLat = (restaurantLat + userLat) / 2
                val midLon = (restaurantLon + userLon) / 2
                controller.setCenter(GeoPoint(midLat, midLon))
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            val linea = OsmPolyline().apply {
                addPoint(GeoPoint(restaurantLat, restaurantLon))
                addPoint(GeoPoint(userLat, userLon))
                outlinePaint.color = android.graphics.Color.parseColor("#FF6600")
                outlinePaint.strokeWidth = 10f
            }
            mapView.overlays.add(linea)

            val markerRestaurante = Marker(mapView).apply {
                position = GeoPoint(restaurantLat, restaurantLon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = labelRestaurante
                snippet = snippetRestaurante
            }
            mapView.overlays.add(markerRestaurante)

            val markerUsuario = Marker(mapView).apply {
                position = GeoPoint(userLat, userLon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = labelUsuario
            }
            mapView.overlays.add(markerUsuario)

            if (fase == 1) {
                val markerRepartidor = Marker(mapView).apply {
                    position = GeoPoint(repartidorLat, repartidorLon)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = labelRepartidor
                }
                mapView.overlays.add(markerRepartidor)
            }

            mapView.invalidate()
        },
        modifier = modifier
    )
}

@Composable
private fun FaseItem(
    icon: ImageVector,
    label: String,
    activa: Boolean,
    completada: Boolean
) {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = if (activa && !completada) 1.15f else 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
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
        delay(3000L)
        onFinished()
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(NaranjaZesta),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = BlancoZesta,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = stringResource(R.string.tracking_entregado_titulo),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipalZesta
        )
        Text(
            text = stringResource(R.string.tracking_entregado_subtitulo),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundarioZesta
        )
    }
}