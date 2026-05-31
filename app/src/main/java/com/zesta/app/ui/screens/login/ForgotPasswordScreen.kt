package com.zesta.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.repository.AuthRepository
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*
import kotlinx.coroutines.launch

// Los dos tabs de arriba: mandar correo o cambiar la contraseña ya logueado
private enum class ForgotMode { EMAIL, CHANGE }

/**
 * Pantalla de olvidé mi contraseña.
 * Arriba hay un selector para elegir si quieres que te llegue un email
 * o si prefieres cambiarla directamente con la actual.
 */
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var mode by remember { mutableStateOf(ForgotMode.EMAIL) }

    // Lo instancio aquí una vez y lo paso a los dos contenidos
    val authRepository = remember { AuthRepository() }

    val tabEmail = stringResource(R.string.forgot_enviar_correo_tab)
    val tabCambiar = stringResource(R.string.cambiar_contraseña)

    Scaffold(containerColor = FondoZesta) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Flecha para volver al login
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(FondoCirculoZesta)
                        .border(1.dp, BordeCirculoZesta, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.accesibilidad_volver),
                        tint = NegroZesta,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tabs tipo pill — el seleccionado se ve en blanco
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(FondoPlaceholderZesta)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ForgotMode.entries.forEach { m ->
                    val selected = mode == m
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) BlancoZesta else FondoPlaceholderZesta)
                            .clickable { mode = m }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (m == ForgotMode.EMAIL) tabEmail else tabCambiar,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) TextoPrincipalZesta else TextoSecundarioZesta
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Según el tab activo muestro un formulario u otro
            when (mode) {
                ForgotMode.EMAIL -> EmailResetContent(onBack = onBack, authRepository = authRepository)
                ForgotMode.CHANGE -> ChangePasswordContent(onBack = onBack, authRepository = authRepository)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Formulario del tab "Enviar correo" — pides email y Firebase manda el reset
@Composable
private fun EmailResetContent(
    onBack: () -> Unit,
    authRepository: AuthRepository
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var emailSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val errVacio = stringResource(R.string.forgot_error_email_vacio)
    val errInvalido = stringResource(R.string.forgot_error_email_invalido)

    if (emailSent) {
        // Ya se mandó — en vez del formulario enseño confirmación
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = VerdeExitoZesta,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.forgot_email_enviado_titulo),
            style = MaterialTheme.typography.headlineSmall,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.forgot_email_enviado_cuerpo, email),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundarioZesta,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        PrimaryGradientButton(
            text = stringResource(R.string.forgot_volver_login),
            onClick = onBack
        )
    } else {
        // Vista normal: icono, título y campo de email
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(FondoPlaceholderZesta)
                .border(1.dp, BordeCirculoZesta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = null,
                tint = NaranjaZesta,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.forgot_titulo),
            style = MaterialTheme.typography.headlineSmall,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null,
            supportingText = if (errorMessage != null) {{ Text(errorMessage!!) }} else null,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(color = NaranjaZesta)
        } else {
            PrimaryGradientButton(
                text = stringResource(R.string.forgot_enviar),
                onClick = {
                    val trimmed = email.trim()

                    // Antes de llamar a Firebase compruebo que el email tenga pinta de válido
                    if (trimmed.isBlank()) {
                        errorMessage = errVacio
                        return@PrimaryGradientButton
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                        errorMessage = errInvalido
                        return@PrimaryGradientButton
                    }

                    scope.launch {
                        isLoading = true
                        authRepository.enviarResetEmail(trimmed)
                        emailSent = true
                        isLoading = false
                    }
                }
            )
        }
    }
}

// Segundo tab: cambias la contraseña poniendo la actual y la nueva
@Composable
private fun ChangePasswordContent(
    onBack: () -> Unit,
    authRepository: AuthRepository
) {
    var email by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    if (successMessage != null) {
        // Todo ok, pantalla sencilla de éxito
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = VerdeExitoZesta,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Éxito", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(40.dp))
        PrimaryGradientButton(text = "Volver", onClick = onBack)
        return
    }

    // Cuatro campos: email, contraseña actual, nueva y confirmación
    OutlinedTextField(value = email, onValueChange = { email = it })
    OutlinedTextField(value = currentPassword, onValueChange = { currentPassword = it })
    OutlinedTextField(value = newPassword, onValueChange = { newPassword = it })
    OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it })

    Spacer(modifier = Modifier.height(28.dp))

    if (isLoading) {
        CircularProgressIndicator(color = NaranjaZesta)
    } else {
        PrimaryGradientButton(
            text = "Cambiar contraseña",
            onClick = {
                // Reviso que no falte nada y que las dos contraseñas nuevas coincidan
                var valid = true

                if (email.isBlank()) valid = false
                if (currentPassword.isBlank()) valid = false
                if (newPassword.length < 6) valid = false
                if (confirmPassword != newPassword) valid = false

                if (!valid) return@PrimaryGradientButton

                scope.launch {
                    isLoading = true
                    val result = authRepository.cambiarContrasena(
                        email = email.trim(),
                        currentPassword = currentPassword,
                        newPassword = newPassword
                    )
                    if (result.isSuccess) successMessage = "ok"
                    isLoading = false
                }
            }
        )
    }
}
