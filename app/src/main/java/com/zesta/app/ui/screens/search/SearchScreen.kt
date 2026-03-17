package com.zesta.app.ui.screens.search

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.zesta.app.ui.theme.ZestaBackground
import com.zesta.app.ui.theme.ZestaBlack
import com.zesta.app.ui.theme.ZestaTextPrimary
import com.zesta.app.ui.theme.ZestaWhite

data class SearchCategoryItemUi(
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val imageRes: Int
)

@Composable
fun SearchScreen(
    onHomeClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val recentSearches = listOf(
        R.string.search_recent_burgers,
        R.string.search_recent_sushi
    )

    val categories = listOf(
        SearchCategoryItemUi(R.string.category_breakfast, R.drawable.desayuno),
        SearchCategoryItemUi(R.string.category_pizzas, R.drawable.pizzas),
        SearchCategoryItemUi(R.string.category_burgers, R.drawable.hamburguesas),
        SearchCategoryItemUi(R.string.category_bakery, R.drawable.panaderia),
        SearchCategoryItemUi(R.string.category_asian, R.drawable.china),
        SearchCategoryItemUi(R.string.category_mexican, R.drawable.mexicana)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZestaBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 118.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SearchField(
                    query = query,
                    onQueryChange = { query = it }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.search_recent_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = ZestaTextPrimary,
                    fontWeight = FontWeight.Normal
                )
            }

            item {
                RecentSearchRow(
                    searches = recentSearches,
                    onChipClick = { selectedText ->
                        query = selectedText
                    }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.search_main_categories_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = ZestaTextPrimary,
                    fontWeight = FontWeight.Normal
                )
            }

            items(categories) { category ->
                SearchCategoryRow(
                    title = stringResource(category.titleRes),
                    imageRes = category.imageRes
                )
            }
        }

        SearchBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onHomeClick = onHomeClick,
            onCartClick = onCartClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(30.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.search_placeholder),
                color = Color(0xFF8F8F8F)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.cd_search),
                tint = ZestaBlack
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF1F1F1),
            unfocusedContainerColor = Color(0xFFF1F1F1),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = ZestaBlack
        )
    )
}

@Composable
private fun RecentSearchRow(
    searches: List<Int>,
    onChipClick: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        searches.forEach { recentRes ->
            val text = stringResource(recentRes)

            SearchChip(
                text = text,
                onClick = { onChipClick(text) }
            )
        }
    }
}

@Composable
private fun SearchChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF1F1F1))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = ZestaTextPrimary
        )
    }
}

@Composable
private fun SearchCategoryRow(
    title: String,
    @DrawableRes imageRes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = stringResource(R.string.cd_category_image, title),
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFE49B32), CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = ZestaTextPrimary
        )
    }
}

@Composable
private fun SearchBottomBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onCartClick: () -> Unit,
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
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFEDEDED))
                .clickable { onHomeClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = stringResource(R.string.cd_home),
                tint = ZestaBlack,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.nav_home),
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
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.cd_search),
                tint = ZestaBlack,
                modifier = Modifier.size(30.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ZestaWhite)
                .border(1.dp, Color(0xFFD6D6D6), CircleShape)
                .clickable { onCartClick() },
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
