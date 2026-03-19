package com.zesta.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ZestaColorScheme = lightColorScheme(
    background = FondoZesta,
    surface = FondoZesta,
    primary = AzulInicioGradienteZesta,
    onPrimary = BlancoZesta,
    onBackground = TextoPrincipalZesta,
    onSurface = TextoPrincipalZesta
)

@Composable
fun ZestaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ZestaColorScheme,
        typography = Typography,
        shapes = ZestaShapes,
        content = content
    )
}
