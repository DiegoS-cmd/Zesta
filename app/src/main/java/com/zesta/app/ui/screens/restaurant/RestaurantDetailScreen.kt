package com.zesta.app.ui.screens.restaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.restaurant.ProductItem
import com.zesta.app.data.restaurant.Restaurant
import com.zesta.app.data.restaurant.RestaurantRepository
import com.zesta.app.ui.theme.ZestaBackground
import com.zesta.app.ui.theme.ZestaBlack
import com.zesta.app.ui.theme.ZestaPlaceholder
import com.zesta.app.ui.theme.ZestaReview
import com.zesta.app.ui.theme.ZestaTextPrimary
import com.zesta.app.ui.theme.ZestaWhite

@Composable
fun RestaurantDetailScreen(
    restaurantId: Int,
    onBack: () -> Unit,
    onGoToCart: () -> Unit
) {
    val restaurant = RestaurantRepository.getRestaurantById(restaurantId)
        ?: RestaurantRepository.getAllRestaurants().first()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZestaBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp)
        ) {
            item {
                RestaurantTopBar(onBack = onBack)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                RestaurantHeader(restaurant = restaurant)
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.restaurant_promotions),
                    style = MaterialTheme.typography.titleLarge,
                    color = ZestaTextPrimary
                )
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    restaurant.products.take(2).forEach { product ->
                        ProductPromoCard(
                            product = product,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.restaurant_explore_menu),
                    style = MaterialTheme.typography.titleLarge,
                    color = ZestaTextPrimary
                )
            }
        }

        ViewCartButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp),
            onClick = onGoToCart
        )
    }
}

@Composable
private fun RestaurantTopBar(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            icon = Icons.Outlined.ArrowBack,
            contentDescription = "Volver",
            onClick = onBack
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircleIconButton(
                icon = Icons.Outlined.Search,
                contentDescription = "Buscar",
                onClick = { }
            )

            CircleIconButton(
                icon = Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorito",
                onClick = { }
            )
        }
    }
}

@Composable
private fun RestaurantHeader(
    restaurant: Restaurant
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = restaurant.imageRes),
            contentDescription = restaurant.name,
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = restaurant.imageRes),
            contentDescription = restaurant.name,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFD7D7D7), CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = restaurant.name,
            style = MaterialTheme.typography.titleLarge,
            color = ZestaTextPrimary,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = restaurant.rating,
            style = MaterialTheme.typography.bodyMedium,
            color = ZestaReview
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = stringResource(R.string.restaurant_location),
                tint = Color(0xFFE74C3C),
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = stringResource(R.string.restaurant_location),
                style = MaterialTheme.typography.bodyLarge,
                color = ZestaTextPrimary
            )
        }
    }
}

@Composable
private fun ProductPromoCard(
    product: ProductItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(ZestaPlaceholder)
            .padding(10.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp, y = 10.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9E9E9))
                    .border(1.dp, Color(0xFFD0D0D0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = ZestaBlack,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyLarge,
            color = ZestaTextPrimary
        )

        Text(
            text = product.price,
            style = MaterialTheme.typography.bodyLarge,
            color = ZestaTextPrimary
        )

        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            color = ZestaReview
        )
    }
}

@Composable
private fun ViewCartButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(ZestaBlack, Color(0xFF111111))
                )
            )
            .border(2.dp, Color(0xFFB89B3C), RoundedCornerShape(30.dp))
            .clickable { onClick() }
            .padding(horizontal = 26.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = stringResource(R.string.restaurant_view_cart),
                tint = ZestaWhite,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = stringResource(R.string.restaurant_view_cart),
                style = MaterialTheme.typography.bodyLarge,
                color = ZestaWhite
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFFF0F0F0))
            .border(1.dp, Color(0xFFD0D0D0), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ZestaBlack,
            modifier = Modifier.size(28.dp)
        )
    }
}
