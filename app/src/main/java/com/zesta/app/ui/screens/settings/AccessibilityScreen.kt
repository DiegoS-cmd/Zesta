package com.zesta.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R

// Estado local de la pantalla.
// No aplica cambios reales al sistema, pero sí modifica la vista previa
// para que el usuario vea un efecto inmediato dentro de Zesta.
private data class AccessibilityState(
    val largeText: Boolean = false,
    val highContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val largerButtons: Boolean = false
)

/**
 * Pantalla de accesibilidad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityScreen(onBack: () -> Unit) {

    var state by remember { mutableStateOf(AccessibilityState()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.accessibility_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Tarjeta superior con una pequeña explicación
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.accessibility_intro_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Vista previa interactiva:
            // aquí sí se ve el efecto de los toggles
            item {
                AccessibilityPreviewCard(state = state)
            }

            // Sección de texto
            item { SectionHeader(stringResource(R.string.accessibility_sec_text)) }
            item {
                SettingToggle(
                    title = stringResource(R.string.accessibility_large_text_title),
                    description = stringResource(R.string.accessibility_large_text_desc),
                    checked = state.largeText,
                    onToggle = { state = state.copy(largeText = it) }
                )
            }

            // Sección visual
            item { SectionHeader(stringResource(R.string.accessibility_sec_visual)) }
            item {
                SettingToggle(
                    title = stringResource(R.string.accessibility_high_contrast_title),
                    description = stringResource(R.string.accessibility_high_contrast_desc),
                    checked = state.highContrast,
                    onToggle = { state = state.copy(highContrast = it) }
                )
            }
            item {
                SettingToggle(
                    title = stringResource(R.string.accessibility_reduce_motion_title),
                    description = stringResource(R.string.accessibility_reduce_motion_desc),
                    checked = state.reduceMotion,
                    onToggle = { state = state.copy(reduceMotion = it) }
                )
            }

            // Sección de interacción
            item { SectionHeader(stringResource(R.string.accessibility_sec_interaction)) }
            item {
                SettingToggle(
                    title = stringResource(R.string.accessibility_larger_buttons_title),
                    description = stringResource(R.string.accessibility_larger_buttons_desc),
                    checked = state.largerButtons,
                    onToggle = { state = state.copy(largerButtons = it) }
                )
            }

            // Bloque informativo inferior
            item {
                AccessibilityTipsCard(state = state)
            }

            // Nota final para dejar claro que los ajustes globales del sistema
            // siguen dependiendo de Android
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.accessibility_system_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

/**
 * Vista previa que cambia según los ajustes activados.
 *
 * Esto hace que la pantalla no se quede vacía ni sea solo decorativa.
 */
@Composable
private fun AccessibilityPreviewCard(state: AccessibilityState) {

    // Si el usuario activa texto grande, usamos estilos más grandes
    val titleStyle = if (state.largeText) {
        MaterialTheme.typography.headlineSmall
    } else {
        MaterialTheme.typography.titleMedium
    }

    val bodyStyle = if (state.largeText) {
        MaterialTheme.typography.bodyLarge
    } else {
        MaterialTheme.typography.bodyMedium
    }

    // Si activa alto contraste, invertimos el aspecto de la tarjeta
    val containerColor = if (state.highContrast) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (state.highContrast) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Si activa botones grandes, aumentamos altura y espaciado
    val buttonHeight = if (state.largerButtons) 56.dp else 42.dp
    val spacing = if (state.largerButtons) 16.dp else 8.dp

    // Reducir animaciones aquí lo representamos visualmente
    // haciendo la tarjeta más sobria
    val previewAlpha = if (state.reduceMotion) 0.96f else 1f

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(previewAlpha),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            Text(
                text = stringResource(R.string.accessibility_preview_label),
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.85f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = contentColor
                )

                Text(
                    text = stringResource(R.string.accessibility_preview_title),
                    style = titleStyle,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            Text(
                text = stringResource(R.string.accessibility_preview_body),
                style = bodyStyle,
                color = contentColor
            )

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                shape = RoundedCornerShape(if (state.largerButtons) 18.dp else 12.dp)
            ) {
                Text(text = stringResource(R.string.accessibility_preview_button))
            }

            Text(
                text = when {
                    state.largeText && state.highContrast && state.largerButtons ->
                        stringResource(R.string.accessibility_preview_status_all)

                    state.largeText ->
                        stringResource(R.string.accessibility_preview_status_large_text)

                    state.highContrast ->
                        stringResource(R.string.accessibility_preview_status_high_contrast)

                    state.reduceMotion ->
                        stringResource(R.string.accessibility_preview_status_reduce_motion)

                    state.largerButtons ->
                        stringResource(R.string.accessibility_preview_status_larger_buttons)

                    else ->
                        stringResource(R.string.accessibility_preview_status_default)
                },
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.85f)
            )
        }
    }
}

/**
 * Tarjeta con mensajes cortos según el estado actual.
 * Da sensación de pantalla útil y no solo estética.
 */
@Composable
private fun AccessibilityTipsCard(state: AccessibilityState) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.accessibility_tips_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            TipRow(
                text = if (state.largeText)
                    stringResource(R.string.accessibility_tip_large_text_on)
                else
                    stringResource(R.string.accessibility_tip_large_text_off)
            )

            TipRow(
                text = if (state.highContrast)
                    stringResource(R.string.accessibility_tip_high_contrast_on)
                else
                    stringResource(R.string.accessibility_tip_high_contrast_off)
            )

            TipRow(
                text = if (state.largerButtons)
                    stringResource(R.string.accessibility_tip_larger_buttons_on)
                else
                    stringResource(R.string.accessibility_tip_larger_buttons_off)
            )

            TipRow(
                text = if (state.reduceMotion)
                    stringResource(R.string.accessibility_tip_reduce_motion_on)
                else
                    stringResource(R.string.accessibility_tip_reduce_motion_off)
            )
        }
    }
}

// Línea simple de ayuda
@Composable
private fun TipRow(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// Cabecera de cada bloque
@Composable
private fun SectionHeader(title: String) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

// Fila reutilizable con texto + switch
@Composable
private fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onToggle
        )
    }
}