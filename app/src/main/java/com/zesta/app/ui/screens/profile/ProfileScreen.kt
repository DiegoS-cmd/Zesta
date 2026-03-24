package com.zesta.app.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Person
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
import com.zesta.app.navigation.AppRoutes
import com.zesta.app.ui.components.ZestaBottomNavBar
import com.zesta.app.ui.theme.AzulAvatarZesta
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.IndigoAvatarZesta
import com.zesta.app.ui.theme.NaranjaZesta
import com.zesta.app.ui.theme.RosaAvatarZesta
import com.zesta.app.ui.theme.TextoOpcionZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta

@Composable
fun ProfileScreen(
    userName: String = "",
    isGuest: Boolean = false,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onHelpClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAccessibilityClick: () -> Unit,
    onManageAccountClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val displayName = when {
        isGuest -> stringResource(R.string.perfil_nombre_invitado)
        userName.isBlank() -> stringResource(R.string.perfil_nombre_placeholder)
        else -> userName
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 118.dp)
        ) {
            item {
                ProfileHeader(
                    displayName = displayName,
                    isGuest = isGuest,
                    onFavoritesClick = onFavoritesClick,
                    onOrderHistoryClick = onOrderHistoryClick,
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick
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

        ZestaBottomNavBar(
            selectedRoute = AppRoutes.Profile.route,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onCartClick = onCartClick,
            onProfileClick = { }
        )
    }
}

@Composable
private fun ProfileHeader(
    displayName: String,
    isGuest: Boolean,
    onFavoritesClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.perfil_titulo),
            style = MaterialTheme.typography.headlineMedium,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(28.dp))

        ProfileAvatar()

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineMedium,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProfileQuickActionCard(
                text = stringResource(R.string.perfil_favoritos),
                onClick = onFavoritesClick
            )

            ProfileQuickActionCard(
                text = stringResource(R.string.perfil_historial_pedidos),
                onClick = onOrderHistoryClick
            )
        }

        if (isGuest) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.perfil_mensaje_invitado),
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPrincipalZesta
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.inicio_sesion_entrar),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoPrincipalZesta,
                    modifier = Modifier.clickable { onLoginClick() }
                )

                Text(
                    text = stringResource(R.string.inicio_sesion_registrarse),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoPrincipalZesta,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
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
                        AzulAvatarZesta,
                        IndigoAvatarZesta,
                        RosaAvatarZesta
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
            tint = BlancoZesta,
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
            .background(NaranjaZesta)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = TextoOpcionZesta,
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
            .background(NaranjaZesta)
            .padding(horizontal = 28.dp, vertical = 22.dp)
    ) {
        ProfileOptionItem(
            text = stringResource(R.string.perfil_ayuda),
            onClick = onHelpClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            text = stringResource(R.string.perfil_privacidad),
            onClick = onPrivacyClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            text = stringResource(R.string.perfil_accesibilidad),
            onClick = onAccessibilityClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            text = stringResource(R.string.perfil_gestionar_cuenta),
            onClick = onManageAccountClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            text = stringResource(R.string.perfil_acerca_de),
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
        color = TextoOpcionZesta,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.clickable { onClick() }
    )
}
