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

/**
 * Pantalla de registro de nuevo usuario.
 *
 * Incluye validación local campo a campo antes de llamar al ViewModel.
 * Los campos obligatorios son nombre, email y contraseña.
 * El teléfono es opcional pero si se rellena debe ser un número español válido.
 * La dirección es opcional y no tiene validación de formato.
 *
 * @param fullName Valor actual del campo nombre completo.
 * @param email Valor actual del campo email.
 * @param password Valor actual del campo contraseña.
 * @param phone Valor actual del campo teléfono.
 * @param address Valor actual del campo dirección.
 * @param errorMessage Error general del ViewModel (email ya registrado, error de red, etc.), o null.
 * @param onFullNameChange Callback al cambiar el nombre.
 * @param onEmailChange Callback al cambiar el email.
 * @param onPasswordChange Callback al cambiar la contraseña.
 * @param onPhoneChange Callback al cambiar el teléfono.
 * @param onAddressChange Callback al cambiar la dirección.
 * @param onRegisterClick Callback al pulsar "Crear cuenta" si la validación local pasa.
 * @param onBack Callback al pulsar "Ya tengo cuenta".
 */
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
    // Errores locales de validación por campo
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    // Strings de error cargados fuera de lambdas no-composables
    val errNombreObligatorio = stringResource(R.string.registro_error_nombre_obligatorio)
    val errNombreMinimo = stringResource(R.string.registro_error_nombre_minimo)
    val errEmailObligatorio = stringResource(R.string.registro_error_email_obligatorio)
    val errEmailFormato = stringResource(R.string.registro_error_email_formato)
    val errPasswordObligatoria = stringResource(R.string.registro_error_password_obligatoria)
    val errPasswordMinimo = stringResource(R.string.registro_error_password_minimo)
    val errTelefonoFormato = stringResource(R.string.registro_error_telefono_formato)

    // Colores reutilizados en todos los campos
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

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.logo_zesta),
            contentDescription = stringResource(R.string.inicio_sesion_descripcion_logo),
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // NOMBRE COMPLETO (obligatorio, mínimo 3 caracteres)
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                onFullNameChange(it)
                fullNameError = null
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.registro_nombre_completo), style = MaterialTheme.typography.bodyMedium)
            },
            shape = PlaceholderShape,
            singleLine = true,
            isError = fullNameError != null,
            supportingText = fullNameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // EMAIL (obligatorio, formato válido)
        OutlinedTextField(
            value = email,
            onValueChange = {
                onEmailChange(it)
                emailError = null
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.registro_email), style = MaterialTheme.typography.bodyMedium)
            },
            shape = PlaceholderShape,
            singleLine = true,
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // CONTRASEÑA (obligatoria, mínimo 6 caracteres)
        OutlinedTextField(
            value = password,
            onValueChange = {
                onPasswordChange(it)
                passwordError = null
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.registro_contrasena), style = MaterialTheme.typography.bodyMedium)
            },
            shape = PlaceholderShape,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // TELÉFONO (opcional, si se rellena debe ser número español: 9 dígitos, empieza por 6/7/8/9)
        OutlinedTextField(
            value = phone,
            onValueChange = {
                onPhoneChange(it)
                phoneError = null
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.registro_telefono), style = MaterialTheme.typography.bodyMedium)
            },
            shape = PlaceholderShape,
            singleLine = true,
            isError = phoneError != null,
            supportingText = phoneError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // DIRECCIÓN (opcional, sin validación de formato)
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.registro_direccion), style = MaterialTheme.typography.bodyMedium)
            },
            shape = PlaceholderShape,
            singleLine = true,
            colors = fieldColors
        )

        // ERROR GENERAL DEL VIEWMODEL
        // Se muestra solo cuando el error viene del servidor (email ya registrado,
        // error de red, etc.), no de la validación local.
        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // BOTÓN CREAR CUENTA
        // Ejecuta la validación local antes de llamar a onRegisterClick.
        // Solo llama al ViewModel si todos los campos obligatorios son válidos.
        PrimaryGradientButton(
            text = stringResource(R.string.registro_crear_cuenta),
            onClick = {
                var valid = true

                if (fullName.isBlank()) {
                    fullNameError = errNombreObligatorio
                    valid = false
                } else if (fullName.trim().length < 3) {
                    fullNameError = errNombreMinimo
                    valid = false
                }

                if (email.isBlank()) {
                    emailError = errEmailObligatorio
                    valid = false
                } else if (!isValidEmail(email.trim())) {
                    emailError = errEmailFormato
                    valid = false
                }

                if (password.isBlank()) {
                    passwordError = errPasswordObligatoria
                    valid = false
                } else if (password.length < 6) {
                    passwordError = errPasswordMinimo
                    valid = false
                }

                if (phone.isNotBlank() && !isValidSpanishPhone(phone)) {
                    phoneError = errTelefonoFormato
                    valid = false
                }

                if (valid) onRegisterClick()
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ENLACE "YA TENGO CUENTA"
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

/**
 * Valida que el email tenga formato correcto.
 * - Parte local mínimo 2 caracteres: letras, números, puntos, guiones y porcentajes.
 * - Dominio mínimo 2 caracteres.
 * - TLD entre 2 y 6 caracteres.
 */
private fun isValidEmail(email: String): Boolean {
    val regex = Regex("^[a-zA-Z0-9._%-]{2,}@[a-zA-Z0-9.-]{2,}\\.[a-zA-Z]{2,6}$")
    return regex.matches(email)
}

/**
 * Valida que el teléfono sea un número español válido:
 * - Exactamente 9 dígitos.
 * - Empieza por 6, 7, 8 o 9.
 */
private fun isValidSpanishPhone(phone: String): Boolean {
    val regex = Regex("^[6789]\\d{8}$")
    return regex.matches(phone.trim())
}