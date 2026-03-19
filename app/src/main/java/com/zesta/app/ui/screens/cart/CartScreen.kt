package com.zesta.app.ui.screens.cart

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.theme.AzulFinGradienteZesta
import com.zesta.app.ui.theme.AzulInicioGradienteZesta
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.BordeBotonZesta
import com.zesta.app.ui.theme.BordeCirculoZesta
import com.zesta.app.ui.theme.BordeClaroZesta
import com.zesta.app.ui.theme.BordeIconoZesta
import com.zesta.app.ui.theme.BordeIlustracionCarritoZesta
import com.zesta.app.ui.theme.FondoBarraInferiorZesta
import com.zesta.app.ui.theme.FondoCirculoZesta
import com.zesta.app.ui.theme.FondoIlustracionCarritoZesta
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta
import com.zesta.app.ui.theme.TextoSecundarioZesta

@Composable
fun CartScreen(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStartShoppingClick: () -> Unit,
    onTestPurchaseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.carrito_titulo),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.Normal
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyCartContent(
                    onStartShoppingClick = onStartShoppingClick,
                    onTestPurchaseClick = onTestPurchaseClick
                )
            }
        }

        CartBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun EmptyCartContent(
    onStartShoppingClick: () -> Unit,
    onTestPurchaseClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(FondoIlustracionCarritoZesta)
                .border(1.dp, BordeIlustracionCarritoZesta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = stringResource(R.string.accesibilidad_ilustracion_carrito_vacio),
                tint = NegroZesta,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = stringResource(R.string.carrito_vacio_titulo),
            style = MaterialTheme.typography.headlineSmall,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.carrito_vacio_descripcion),
            style = MaterialTheme.typography.bodyLarge,
            color = TextoSecundarioZesta
        )

        Spacer(modifier = Modifier.height(34.dp))

        BlueActionButton(
            text = stringResource(R.string.carrito_empezar_comprar),
            onClick = onStartShoppingClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        BlueActionButton(
            text = stringResource(R.string.carrito_prueba_compra),
            onClick = onTestPurchaseClick
        )
    }
}

@Composable
private fun BlueActionButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
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
            color = BlancoZesta,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CartBottomBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(FondoBarraInferiorZesta)
            .border(1.dp, BordeClaroZesta, RoundedCornerShape(34.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(BlancoZesta)
                .border(1.dp, BordeIconoZesta, CircleShape)
                .clickable { onHomeClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = stringResource(R.string.accesibilidad_ir_inicio),
                tint = NegroZesta,
                modifier = Modifier.size(30.dp)
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(FondoPlaceholderZesta)
                .clickable { onSearchClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.accesibilidad_ir_buscar),
                tint = NegroZesta,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.navegacion_buscar),
                style = MaterialTheme.typography.bodyLarge,
                color = TextoPrincipalZesta
            )
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(BlancoZesta)
                .border(1.dp, BordeIconoZesta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = stringResource(R.string.accesibilidad_ir_carrito),
                tint = NegroZesta,
                modifier = Modifier.size(28.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(BlancoZesta)
                .border(1.dp, BordeIconoZesta, CircleShape)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(R.string.accesibilidad_ir_perfil),
                tint = NegroZesta,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
