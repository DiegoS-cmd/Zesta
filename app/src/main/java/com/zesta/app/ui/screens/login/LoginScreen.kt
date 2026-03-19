package com.zesta.app.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.LinkTextStyle
import com.zesta.app.ui.theme.PlaceholderShape
import com.zesta.app.ui.theme.TextoPrincipalZesta

@Composable
fun LoginScreen(
    email: String,
    password: String,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoRegister: () -> Unit,
    onContinueAsGuestClick: () -> Unit
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
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(42.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.inicio_sesion_email),
                    style = MaterialTheme.typography.bodyMedium
                )
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

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.inicio_sesion_contrasena),
                    style = MaterialTheme.typography.bodyMedium
                )
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

        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryGradientButton(
            text = stringResource(R.string.inicio_sesion_entrar),
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(18.dp))

        PrimaryGradientButton(
            text = stringResource(R.string.inicio_sesion_registrarse),
            onClick = onGoRegister
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { }) {
            Text(
                text = stringResource(R.string.inicio_sesion_olvide_contrasena),
                style = LinkTextStyle
            )
        }

        TextButton(onClick = onContinueAsGuestClick) {
            Text(
                text = stringResource(R.string.inicio_sesion_continuar_invitado),
                style = LinkTextStyle
            )
        }
    }
}
