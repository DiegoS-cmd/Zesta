package com.zesta.app.ui.screens.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.restaurant.Restaurant
import com.zesta.app.data.restaurant.RestaurantRepository
import com.zesta.app.ui.theme.ZestaBackground
import com.zesta.app.ui.theme.ZestaBlack
import com.zesta.app.ui.theme.ZestaOffer
import com.zesta.app.ui.theme.ZestaReview
import com.zesta.app.ui.theme.ZestaTextPrimary

data class CategoryItem(
    val name: String,
    val imageRes: Int
)

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRestaurantClick: (Int) -> Unit
) {
    val categories = listOf(
        CategoryItem(stringResource(R.string.category_breakfast), R.drawable.desayuno),
        CategoryItem(stringResource(R.string.category_pizzas), R.drawable.pizzas),
        CategoryItem(stringResource(R.string.category_burgers), R.drawable.hamburguesas),
        CategoryItem(stringResource(R.string.category_bakery), R.drawable.panaderia),
        CategoryItem(stringResource(R.string.category_asian), R.drawable.china),
        CategoryItem(stringResource(R.string.category_mexican), R.drawable.mexicana)
    )

    val featuredRestaurants = RestaurantRepository.getFeaturedRestaurants()
    val promoRestaurants = RestaurantRepository.getPromoRestaurants()
    val exploreRestaurants = RestaurantRepository.getExploreRestaurants()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZestaBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HomeTopBar()
            }

            item {
                CategoryRow(categories = categories)
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.home_featured),
                    subtitle = stringResource(R.string.home_sponsored)
                )
            }

            item {
                RestaurantRow(
                    restaurants = featuredRestaurants,
                    onRestaurantClick = onRestaurantClick
                )
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.home_promotions)
                )
            }

            item {
                RestaurantRow(
                    restaurants = promoRestaurants,
                    onRestaurantClick = onRestaurantClick
                )
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.home_explore)
                )
            }

            item {
                ExploreRestaurantGrid(
                    restaurants = exploreRestaurants,
                    onRestaurantClick = onRestaurantClick
                )
            }
        }

        BottomHomeBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            onSearchClick = onSearchClick,
            onCartClick = onCartClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.home_address),
            style = MaterialTheme.typography.titleLarge,
            color = ZestaTextPrimary
        )

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = stringResource(R.string.cd_notifications),
            tint = ZestaBlack,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun CategoryRow(categories: List<CategoryItem>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(categories) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = category.imageRes),
                    contentDescription = category.name,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFFD3D3D3), CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ZestaTextPrimary
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = ZestaTextPrimary
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = ZestaReview
            )
        }
    }
}

@Composable
private fun RestaurantRow(
    restaurants: List<Restaurant>,
    onRestaurantClick: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = restaurants,
            key = { restaurant -> restaurant.id }
        ) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) }
            )
        }
    }
}

@Composable
private fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(185.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF1F1F1))
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop
            )

            if (restaurant.promoText != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp, start = 6.dp, end = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ZestaOffer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = restaurant.promoText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = restaurant.name,
            style = MaterialTheme.typography.bodyLarge,
            color = ZestaTextPrimary,
            fontWeight = FontWeight.Normal
        )

        Text(
            text = restaurant.deliveryInfo,
            style = MaterialTheme.typography.bodyMedium,
            color = ZestaTextPrimary
        )

        Text(
            text = restaurant.rating,
            style = MaterialTheme.typography.bodyMedium,
            color = ZestaReview
        )
    }
}

@Composable
private fun ExploreRestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(190.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF1F1F1))
        ) {
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            if (restaurant.promoText != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = restaurant.promoText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = restaurant.name,
            style = MaterialTheme.typography.titleMedium,
            color = ZestaTextPrimary
        )

        Text(
            text = restaurant.deliveryInfo,
            style = MaterialTheme.typography.bodyMedium,
            color = ZestaTextPrimary
        )

        Text(
            text = restaurant.rating,
            style = MaterialTheme.typography.bodyMedium,
            color = ZestaReview
        )
    }
}

@Composable
private fun ExploreRestaurantGrid(
    restaurants: List<Restaurant>,
    onRestaurantClick: (Int) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items(
            items = restaurants,
            key = { restaurant -> restaurant.id }
        ) { restaurant ->
            ExploreRestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) }
            )
        }
    }
}

@Composable
private fun BottomHomeBar(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFFF1F1F1))
            .border(1.dp, Color(0xFFD0D0D0), RoundedCornerShape(30.dp))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = stringResource(R.string.nav_home),
            tint = ZestaBlack,
            modifier = Modifier.size(34.dp)
        )

        Row(
            modifier = Modifier.clickable { onSearchClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.home_search),
                tint = ZestaBlack,
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.home_search),
                style = MaterialTheme.typography.bodyLarge,
                color = ZestaTextPrimary
            )
        }

        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = stringResource(R.string.nav_cart),
            tint = ZestaBlack,
            modifier = Modifier
                .size(34.dp)
                .clickable { onCartClick() }
        )

        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = stringResource(R.string.nav_profile),
            tint = ZestaBlack,
            modifier = Modifier
                .size(34.dp)
                .clickable { onProfileClick() }
        )
    }
}
