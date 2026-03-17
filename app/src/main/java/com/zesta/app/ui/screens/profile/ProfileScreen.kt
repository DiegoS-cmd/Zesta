package com.zesta.app.ui.screens.profile

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
fun ProfileScreen(
    userName: String = "",
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onHelpClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAccessibilityClick: () -> Unit,
    onManageAccountClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val displayName = if (userName.isBlank()) {
        stringResource(R.string.profile_name_placeholder)
    } else {
        userName
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZestaBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 118.dp)
        ) {
            item {
                ProfileHeader(
                    displayName = displayName,
                    onFavoritesClick = onFavoritesClick,
                    onOrderHistoryClick = onOrderHistoryClick
                )
            }

            item {
                ProfileOptionsSection(
                    onHelpClick = onHelpClick,
                    onPrivacyClick = onPrivacyClick,
                    onAccessibilityClick = onAccessibilityClick,
                    onManageAccountClick = onManageAccountClick,
                    onAboutClick = onAboutClick
                )
            }
        }

        ProfileBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onCartClick = onCartClick
        )
    }
}

@Composable
private fun ProfileHeader(
    displayName: String,
    onFavoritesClick: () -> Unit,
    onOrderHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
            color = ZestaTextPrimary,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(28.dp))

        ProfileAvatar()

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineMedium,
            color = ZestaTextPrimary,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProfileQuickActionCard(
                text = stringResource(R.string.profile_favorites),
                onClick = onFavoritesClick
            )

            ProfileQuickActionCard(
                text = stringResource(R.string.profile_order_history),
                onClick = onOrderHistoryClick
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun ProfileAvatar() {
    Box(
        modifier = Modifier
            .size(122.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF47D1FF),
                        Color(0xFF7B8CFF),
                        Color(0xFFD95BFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = stringResource(R.string.cd_profile_avatar),
            tint = ZestaWhite,
            modifier = Modifier.size(74.dp)
        )
    }
}

@Composable
private fun ProfileQuickActionCard(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFFF9800))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF5A4A3A),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun ProfileOptionsSection(
    onHelpClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAccessibilityClick: () -> Unit,
    onManageAccountClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFF9800))
            .padding(horizontal = 28.dp, vertical = 22.dp)
    ) {
        ProfileOptionItem(
            text = stringResource(R.string.profile_help),
            onClick = onHelpClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            text = stringResource(R.string.profile_privacy),
            onClick = onPrivacyClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            text = stringResource(R.string.profile_accessibility),
            onClick = onAccessibilityClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            text = stringResource(R.string.profile_manage_account),
            onClick = onManageAccountClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            text = stringResource(R.string.profile_about),
            onClick = onAboutClick
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun ProfileOptionItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = Color(0xFF5A4A3A),
        fontWeight = FontWeight.Normal,
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
private fun ProfileBottomBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit
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
                .background(Color(0xFFEDEDED))
                .border(1.dp, Color(0xFFD6D6D6), CircleShape),
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
