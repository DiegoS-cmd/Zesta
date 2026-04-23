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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private enum class ForgotMode { EMAIL, CHANGE }

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var mode by remember { mutableStateOf(ForgotMode.EMAIL) }

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

            when (mode) {
                ForgotMode.EMAIL -> EmailResetContent(onBack = onBack)
                ForgotMode.CHANGE -> ChangePasswordContent(onBack = onBack)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EmailResetContent(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var emailSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val errVacio = stringResource(R.string.forgot_error_email_vacio)
    val errInvalido = stringResource(R.string.forgot_error_email_invalido)
    val errNoEncontrado = stringResource(R.string.forgot_error_no_encontrado)

    if (emailSent) {
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
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.forgot_subtitulo),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundarioZesta,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(36.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.forgot_placeholder_email),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = true,
            isError = errorMessage != null,
            supportingText = if (errorMessage != null) {{ Text(errorMessage!!) }} else null,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FondoPlaceholderZesta,
                unfocusedContainerColor = FondoPlaceholderZesta,
                focusedBorderColor = NaranjaZesta,
                unfocusedBorderColor = FondoPlaceholderZesta,
                cursorColor = TextoPrincipalZesta
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (isLoading) {
            CircularProgressIndicator(color = NaranjaZesta)
        } else {
            PrimaryGradientButton(
                text = stringResource(R.string.forgot_enviar),
                onClick = {
                    val trimmed = email.trim()
                    if (trimmed.isBlank()) { errorMessage = errVacio; return@PrimaryGradientButton }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                        errorMessage = errInvalido; return@PrimaryGradientButton
                    }
                    scope.launch {
                        isLoading = true
                        try {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(trimmed).await()
                            emailSent = true
                        } catch (e: Exception) {
                            errorMessage = errNoEncontrado
                        }
                        isLoading = false
                    }
                }
            )
        }
    }
}

@Composable
private fun ChangePasswordContent(onBack: () -> Unit) {
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

    // Strings para usar dentro de lambdas
    val strCorreoValido = stringResource(R.string.correo_valido)
    val strIntroActual = stringResource(R.string.intro_actual_contraseña)
    val strMinimo = stringResource(R.string.nueva_contrasena_minimo)
    val strNoCoinciden = stringResource(R.string.contrasenas_no_coinciden)
    val strIncorrecta = stringResource(R.string.contrasena_actual_incorrecta)
    val strExitoTitulo = stringResource(R.string.actualizar_contraseña)
    val strExitoCuerpo = stringResource(R.string.forgot_contrasena_exito_cuerpo)
    val strVolverLogin = stringResource(R.string.forgot_volver_login)
    val strCambiarTitulo = stringResource(R.string.cambiar_contraseña)
    val strCambiarSubtitulo = stringResource(R.string.forgot_cambiar_subtitulo)
    val strPlaceholderEmail = stringResource(R.string.forgot_placeholder_email)
    val strActualContraseña = stringResource(R.string.actual_contraseña)
    val strNuevaContraseña = stringResource(R.string.nueva_contraseña)
    val strConfNuevaContraseña = stringResource(R.string.conf_nueva_contraseña)

    if (successMessage != null) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = VerdeExitoZesta,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = strExitoTitulo,
            style = MaterialTheme.typography.headlineSmall,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = strExitoCuerpo,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundarioZesta,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        PrimaryGradientButton(text = strVolverLogin, onClick = onBack)
        return
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(FondoPlaceholderZesta)
            .border(1.dp, BordeCirculoZesta, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            tint = NaranjaZesta,
            modifier = Modifier.size(34.dp)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = strCambiarTitulo,
        style = MaterialTheme.typography.headlineSmall,
        color = TextoPrincipalZesta,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = strCambiarSubtitulo,
        style = MaterialTheme.typography.bodyMedium,
        color = TextoSecundarioZesta,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(36.dp))

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = FondoPlaceholderZesta,
        unfocusedContainerColor = FondoPlaceholderZesta,
        focusedBorderColor = NaranjaZesta,
        unfocusedBorderColor = FondoPlaceholderZesta,
        cursorColor = TextoPrincipalZesta
    )
    val fieldShape = RoundedCornerShape(14.dp)

    OutlinedTextField(
        value = email,
        onValueChange = { email = it; emailError = null },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(strPlaceholderEmail, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        isError = emailError != null,
        supportingText = if (emailError != null) {{ Text(emailError!!) }} else null,
        shape = fieldShape,
        colors = fieldColors
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = currentPassword,
        onValueChange = { currentPassword = it; currentPasswordError = null },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(strActualContraseña, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        isError = currentPasswordError != null,
        supportingText = if (currentPasswordError != null) {{ Text(currentPasswordError!!) }} else null,
        shape = fieldShape,
        colors = fieldColors
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it; newPasswordError = null },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(strNuevaContraseña, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        isError = newPasswordError != null,
        supportingText = if (newPasswordError != null) {{ Text(newPasswordError!!) }} else null,
        shape = fieldShape,
        colors = fieldColors
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it; confirmPasswordError = null },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(strConfNuevaContraseña, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        isError = confirmPasswordError != null,
        supportingText = if (confirmPasswordError != null) {{ Text(confirmPasswordError!!) }} else null,
        shape = fieldShape,
        colors = fieldColors
    )

    Spacer(modifier = Modifier.height(28.dp))

    if (isLoading) {
        CircularProgressIndicator(color = NaranjaZesta)
    } else {
        PrimaryGradientButton(
            text = strCambiarTitulo,
            onClick = {
                var valid = true
                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    emailError = strCorreoValido; valid = false
                }
                if (currentPassword.isBlank()) {
                    currentPasswordError = strIntroActual; valid = false
                }
                if (newPassword.length < 6) {
                    newPasswordError = strMinimo; valid = false
                }
                if (confirmPassword != newPassword) {
                    confirmPasswordError = strNoCoinciden; valid = false
                }
                if (!valid) return@PrimaryGradientButton

                scope.launch {
                    isLoading = true
                    try {
                        val auth = FirebaseAuth.getInstance()
                        val credential = EmailAuthProvider.getCredential(email.trim(), currentPassword)
                        auth.signInWithCredential(credential).await()
                        auth.currentUser?.updatePassword(newPassword)?.await()
                        successMessage = "ok"
                    } catch (e: Exception) {
                        currentPasswordError = strIncorrecta
                    }
                    isLoading = false
                }
            }
        )
    }
}