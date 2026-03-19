package com.zesta.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.BlancoZesta
import com.zesta.app.ui.theme.BordeIconoZesta
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.NegroZesta
import com.zesta.app.ui.theme.TextoPrincipalZesta

@Composable
fun ManageAccountScreen(
    isGuest: Boolean,
    userName: String,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val displayName = if (isGuest || userName.isBlank()) {
        stringResource(R.string.perfil_nombre_invitado)
    } else {
        userName
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(BlancoZesta)
                    .border(1.dp, BordeIconoZesta, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.accesibilidad_volver),
                    tint = NegroZesta
                )
            }

            Text(
                text = stringResource(R.string.perfil_gestionar_cuenta),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPrincipalZesta,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(FondoPlaceholderZesta),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                tint = NegroZesta,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineSmall,
            color = TextoPrincipalZesta
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isGuest) {
                stringResource(R.string.gestionar_cuenta_descripcion_invitado)
            } else {
                stringResource(R.string.gestionar_cuenta_descripcion_usuario)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta
        )

        Spacer(modifier = Modifier.height(36.dp))

        if (isGuest) {
            PrimaryGradientButton(
                text = stringResource(R.string.inicio_sesion_entrar),
                onClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            PrimaryGradientButton(
                text = stringResource(R.string.inicio_sesion_registrarse),
                onClick = onRegisterClick
            )
        } else {
            PrimaryGradientButton(
                text = stringResource(R.string.perfil_cerrar_sesion),
                onClick = onLogoutClick
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.gestionar_cuenta_volver_perfil),
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta,
            modifier = Modifier.clickable { onBackClick() }
        )
    }
}
