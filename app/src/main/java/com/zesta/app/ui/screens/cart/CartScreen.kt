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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.theme.ZestaBackground
import com.zesta.app.ui.theme.ZestaBlack
import com.zesta.app.ui.theme.ZestaTextPrimary
import com.zesta.app.ui.theme.ZestaWhite

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
            .background(ZestaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.cart_title),
                style = MaterialTheme.typography.headlineMedium,
                color = ZestaTextPrimary,
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
                .background(Color(0xFFF3F3F3))
                .border(1.dp, Color(0xFFE0E0E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = stringResource(R.string.cd_cart_illustration),
                tint = ZestaBlack,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = stringResource(R.string.cart_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = ZestaTextPrimary,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.cart_empty_description),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF7A7A7A)
        )

        Spacer(modifier = Modifier.height(34.dp))

        BlueActionButton(
            text = stringResource(R.string.cart_start_shopping),
            onClick = onStartShoppingClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        BlueActionButton(
            text = stringResource(R.string.cart_test_purchase),
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
                    colors = listOf(Color(0xFF1C36FF), Color(0xFF140394))
                )
            )
            .border(2.dp, Color(0xFF2D2D2D), RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = ZestaWhite,
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
            .background(Color(0xFFF4F4F4))
            .border(1.dp, Color(0xFFD2D2D2), RoundedCornerShape(34.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ZestaWhite)
                .border(1.dp, Color(0xFFD6D6D6), CircleShape)
                .clickable { onHomeClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = stringResource(R.string.cd_home),
                tint = ZestaBlack,
                modifier = Modifier.size(30.dp)
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFEDEDED))
                .clickable { onSearchClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.cd_search),
                tint = ZestaBlack,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.nav_search),
                style = MaterialTheme.typography.bodyLarge,
                color = ZestaTextPrimary
            )
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ZestaWhite)
                .border(1.dp, Color(0xFFD6D6D6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = stringResource(R.string.cd_cart),
                tint = ZestaBlack,
                modifier = Modifier.size(28.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ZestaWhite)
                .border(1.dp, Color(0xFFD6D6D6), CircleShape)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(R.string.cd_profile),
                tint = ZestaBlack,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
