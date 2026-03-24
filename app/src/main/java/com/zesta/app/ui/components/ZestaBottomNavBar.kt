package com.zesta.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import androidx.compose.ui.res.stringResource
import com.zesta.app.navigation.AppRoutes
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.BordeClaroZesta
import com.zesta.app.ui.theme.BordeIconoZesta
import com.zesta.app.ui.theme.FondoBarraInferiorZesta
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoSeleccionNaranjaZesta
import com.zesta.app.ui.theme.NaranjaSeleccionZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta

@Composable
fun ZestaBottomNavBar(
    selectedRoute: String,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(68.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(FondoBarraInferiorZesta)
            .border(1.dp, BordeClaroZesta, RoundedCornerShape(34.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomNavCircleItem(
            isSelected = selectedRoute == AppRoutes.Home.route,
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = stringResource(R.string.accesibilidad_ir_inicio),
                    tint = if (selectedRoute == AppRoutes.Home.route) NaranjaSeleccionZesta else NegroZesta,
                    modifier = Modifier.size(30.dp)
                )
            }
        )

        BottomNavWideItem(
            text = stringResource(R.string.navegacion_buscar),
            isSelected = selectedRoute == AppRoutes.Search.route,
            onClick = onSearchClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.accesibilidad_ir_buscar),
                    tint = if (selectedRoute == AppRoutes.Search.route) NaranjaSeleccionZesta else NegroZesta,
                    modifier = Modifier.size(32.dp)
                )
            }
        )

        BottomNavCircleItem(
            isSelected = selectedRoute == AppRoutes.Cart.route,
            onClick = onCartClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = stringResource(R.string.accesibilidad_ir_carrito),
                    tint = if (selectedRoute == AppRoutes.Cart.route) NaranjaSeleccionZesta else NegroZesta,
                    modifier = Modifier.size(28.dp)
                )
            }
        )

        BottomNavCircleItem(
            isSelected = selectedRoute == AppRoutes.Profile.route,
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.accesibilidad_ir_perfil),
                    tint = if (selectedRoute == AppRoutes.Profile.route) NaranjaSeleccionZesta else NegroZesta,
                    modifier = Modifier.size(28.dp)
                )
            }
        )
    }
}

@Composable
private fun BottomNavCircleItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isSelected) FondoSeleccionNaranjaZesta else BlancoZesta)
            .border(1.dp, if (isSelected) NaranjaSeleccionZesta else BordeIconoZesta, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun BottomNavWideItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(if (isSelected) FondoSeleccionNaranjaZesta else FondoPlaceholderZesta)
            .border(
                width = 1.dp,
                color = if (isSelected) NaranjaSeleccionZesta else FondoPlaceholderZesta,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) NaranjaSeleccionZesta else TextoPrincipalZesta
        )
    }
}
