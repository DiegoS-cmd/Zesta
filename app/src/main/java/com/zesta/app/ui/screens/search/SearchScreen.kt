package com.zesta.app.ui.screens.search

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.navigation.AppRoutes
import com.zesta.app.ui.components.ZestaBottomNavBar
import com.zesta.app.ui.theme.BordeCategoriaZesta
import com.zesta.app.ui.theme.FondoTarjetaRestauranteZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta
import com.zesta.app.ui.theme.TextoSecundarioZesta

data class SearchCategoryItemUi(
    @StringRes val titleRes: Int,
    @DrawableRes val imageRes: Int
)

@Composable
fun SearchScreen(
    onHomeClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val recentSearches = listOf(
        R.string.busqueda_reciente_hamburguesas,
        R.string.busqueda_reciente_sushi
    )

    val categories = listOf(
        SearchCategoryItemUi(R.string.categoria_desayuno, R.drawable.desayuno),
        SearchCategoryItemUi(R.string.categoria_pizzas, R.drawable.pizzas),
        SearchCategoryItemUi(R.string.categoria_hamburguesas, R.drawable.hamburguesas),
        SearchCategoryItemUi(R.string.categoria_panaderia, R.drawable.panaderia),
        SearchCategoryItemUi(R.string.categoria_asiatica, R.drawable.china),
        SearchCategoryItemUi(R.string.categoria_mexicana, R.drawable.mexicana)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
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
                    text = stringResource(R.string.busqueda_titulo_recientes),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
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
                    text = stringResource(R.string.busqueda_titulo_categorias_principales),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
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

        ZestaBottomNavBar(
            selectedRoute = AppRoutes.Search.route,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onHomeClick = onHomeClick,
            onSearchClick = { },
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
                text = stringResource(R.string.busqueda_placeholder),
                color = TextoSecundarioZesta
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.accesibilidad_ir_buscar),
                tint = NegroZesta
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FondoTarjetaRestauranteZesta,
            unfocusedContainerColor = FondoTarjetaRestauranteZesta,
            focusedBorderColor = FondoTarjetaRestauranteZesta,
            unfocusedBorderColor = FondoTarjetaRestauranteZesta,
            cursorColor = NegroZesta
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
            .background(FondoTarjetaRestauranteZesta)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta
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
            contentDescription = stringResource(R.string.accesibilidad_imagen_categoria, title),
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .border(2.dp, BordeCategoriaZesta, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipalZesta
        )
    }
}
