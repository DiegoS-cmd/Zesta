package com.zesta.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.ui.theme.AzulFinGradienteZesta
import com.zesta.app.ui.theme.AzulInicioGradienteZesta
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.BordeBotonZesta

@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    colorTexto: Color = BlancoZesta
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)
                )
            )
            .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = colorTexto,
            fontWeight = FontWeight.SemiBold
        )
    }
}
