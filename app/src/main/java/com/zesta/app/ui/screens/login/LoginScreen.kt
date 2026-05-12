package com.zesta.app.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*

/**
 * Pantalla de login: email/contraseña, Google, invitado y enlaces secundarios.
 * Si el error contiene "deshabilitada" aparece también el botón de reactivación.
 */
@Composable
fun LoginScreen(
    email: String,
    password: String,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoRegister: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onContinueAsGuestClick: () -> Unit,
    onEresEmpresa: () -> Unit,
    onReactivateAccount: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
            .padding(horizontal = 28.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = stringResource(R.string.inicio_sesion_bienvenida),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_zesta),
            contentDescription = stringResource(R.string.inicio_sesion_descripcion_logo),
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Campos de acceso
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.inicio_sesion_email), style = MaterialTheme.typography.bodyMedium)
            },
            shape = PlaceholderShape,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FondoPlaceholderZesta,
                unfocusedContainerColor = FondoPlaceholderZesta,
                disabledContainerColor = FondoPlaceholderZesta,
                focusedBorderColor = FondoPlaceholderZesta,
                unfocusedBorderColor = FondoPlaceholderZesta,
                cursorColor = TextoPrincipalZesta
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.inicio_sesion_contrasena), style = MaterialTheme.typography.bodyMedium)
            },
            shape = PlaceholderShape,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FondoPlaceholderZesta,
                unfocusedContainerColor = FondoPlaceholderZesta,
                disabledContainerColor = FondoPlaceholderZesta,
                focusedBorderColor = FondoPlaceholderZesta,
                unfocusedBorderColor = FondoPlaceholderZesta,
                cursorColor = TextoPrincipalZesta
            )
        )

        // Error: si la cuenta está deshabilitada aparece además el botón de reactivación
        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            if (errorMessage.contains("deshabilitada", ignoreCase = true)) {
                TextButton(onClick = onReactivateAccount) {
                    Text(
                        text = stringResource(R.string.login_reactivar_cuenta),
                        style = LinkTextStyle
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryGradientButton(
            text = stringResource(R.string.inicio_sesion_entrar),
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Separador entre email y Google
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = BordeCirculoZesta)
            Text(stringResource(R.string.login_o), style = MaterialTheme.typography.bodyMedium, color = TextoSecundarioZesta)
            HorizontalDivider(modifier = Modifier.weight(1f), color = BordeCirculoZesta)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón de Google
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(BlancoZesta)
                .border(1.dp, BordeCirculoZesta, RoundedCornerShape(28.dp))
                .clickable { onGoogleSignIn() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_google),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.login_google),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryGradientButton(
            text = stringResource(R.string.inicio_sesion_registrarse),
            onClick = onGoRegister
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Enlaces secundarios
        TextButton(onClick = onForgotPassword) {
            Text(stringResource(R.string.inicio_sesion_olvide_contrasena), style = LinkTextStyle)
        }

        TextButton(onClick = onContinueAsGuestClick) {
            Text(stringResource(R.string.inicio_sesion_continuar_invitado), style = LinkTextStyle)
        }

        TextButton(onClick = onEresEmpresa) {
            Text(stringResource(R.string.login_eres_empresa), style = LinkTextStyle)
        }
    }
}