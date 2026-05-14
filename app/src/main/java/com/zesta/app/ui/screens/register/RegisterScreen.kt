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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

// Pantalla de registro: valida los campos localmente antes de llamar al ViewModel
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
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    // Strings de error para poder meter en las lambdas
    val errNombreObligatorio = stringResource(R.string.registro_error_nombre_obligatorio)
    val errNombreMinimo = stringResource(R.string.registro_error_nombre_minimo)
    val errEmailObligatorio = stringResource(R.string.registro_error_email_obligatorio)
    val errEmailFormato = stringResource(R.string.registro_error_email_formato)
    val errPasswordObligatoria = stringResource(R.string.registro_error_password_obligatoria)
    val errPasswordMinimo = stringResource(R.string.registro_error_password_minimo)
    val errTelefonoFormato = stringResource(R.string.registro_error_telefono_formato)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = FondoPlaceholderZesta,
        unfocusedContainerColor = FondoPlaceholderZesta,
        disabledContainerColor = FondoPlaceholderZesta,
        focusedBorderColor = FondoPlaceholderZesta,
        unfocusedBorderColor = FondoPlaceholderZesta,
        cursorColor = TextoPrincipalZesta,
        errorContainerColor = FondoPlaceholderZesta,
        errorBorderColor = MaterialTheme.colorScheme.error
    )

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
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre — obligatorio, mínimo 3 caracteres
        OutlinedTextField(
            value = fullName,
            onValueChange = { onFullNameChange(it); fullNameError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.registro_nombre_completo), style = MaterialTheme.typography.bodyMedium) },
            shape = PlaceholderShape,
            singleLine = true,
            isError = fullNameError != null,
            supportingText = fullNameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Email — obligatorio, formato válido
        OutlinedTextField(
            value = email,
            onValueChange = { onEmailChange(it); emailError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.registro_email), style = MaterialTheme.typography.bodyMedium) },
            shape = PlaceholderShape,
            singleLine = true,
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Contraseña — obligatoria, mínimo 6 caracteres
        OutlinedTextField(
            value = password,
            onValueChange = { onPasswordChange(it); passwordError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.registro_contrasena), style = MaterialTheme.typography.bodyMedium) },
            shape = PlaceholderShape,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Teléfono — opcional; si se rellena debe ser número español válido
        OutlinedTextField(
            value = phone,
            onValueChange = { onPhoneChange(it); phoneError = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.registro_telefono), style = MaterialTheme.typography.bodyMedium) },
            shape = PlaceholderShape,
            singleLine = true,
            isError = phoneError != null,
            supportingText = phoneError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Dirección — opcional, sin validación
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.registro_direccion), style = MaterialTheme.typography.bodyMedium) },
            shape = PlaceholderShape,
            singleLine = true,
            colors = fieldColors
        )

        // Error del servidor (email ya registrado, error de red, etc.)
        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Valida localmente antes de llamar al ViewModel
        PrimaryGradientButton(
            text = stringResource(R.string.registro_crear_cuenta),
            onClick = {
                var valid = true

                if (fullName.isBlank()) { fullNameError = errNombreObligatorio; valid = false }
                else if (fullName.trim().length < 3) { fullNameError = errNombreMinimo; valid = false }

                if (email.isBlank()) { emailError = errEmailObligatorio; valid = false }
                else if (!isValidEmail(email.trim())) { emailError = errEmailFormato; valid = false }

                if (password.isBlank()) { passwordError = errPasswordObligatoria; valid = false }
                else if (password.length < 6) { passwordError = errPasswordMinimo; valid = false }

                if (phone.isNotBlank() && !isValidSpanishPhone(phone)) { phoneError = errTelefonoFormato; valid = false }

                if (valid) onRegisterClick()
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onBack) {
            Text(text = stringResource(R.string.registro_ya_tengo_cuenta), style = LinkTextStyle, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// Email válido: parte nombre ≥2 chars, dominio ≥2 chars, parte despues de punto 2-6 chars
private fun isValidEmail(email: String): Boolean =
    Regex("^[a-zA-Z0-9._%-]{2,}@[a-zA-Z0-9.-]{2,}\\.[a-zA-Z]{2,6}$").matches(email)

// Teléfono español válido: 9 dígitos, empieza por 6/7/8/9
private fun isValidSpanishPhone(phone: String): Boolean =
    Regex("^[6789]\\d{8}$").matches(phone.trim())