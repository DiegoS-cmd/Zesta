package com.zesta.app.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.FondoPlaceholderZesta
import com.zesta.app.ui.theme.FondoZesta
import com.zesta.app.ui.theme.LinkTextStyle
import com.zesta.app.ui.theme.PlaceholderShape
import com.zesta.app.ui.theme.TextoPrincipalZesta

@Composable
fun RegisterScreen(
    fullName: String,
    email: String,
    password: String,
    phone: String,
    address: String,
    errorMessage: String?,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_zesta),
            contentDescription = stringResource(R.string.inicio_sesion_descripcion_logo),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.registro_nombre_completo),
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
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.registro_email),
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
                    text = stringResource(R.string.registro_contrasena),
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

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.registro_telefono),
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
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.registro_direccion),
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

        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        PrimaryGradientButton(
            text = stringResource(R.string.registro_crear_cuenta),
            onClick = onRegisterClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onBack) {
            Text(
                text = stringResource(R.string.registro_ya_tengo_cuenta),
                style = LinkTextStyle,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
