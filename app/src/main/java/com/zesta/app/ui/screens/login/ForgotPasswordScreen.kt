package com.zesta.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var emailSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val errVacio = stringResource(R.string.forgot_error_email_vacio)
    val errInvalido = stringResource(R.string.forgot_error_email_invalido)
    val errNoEncontrado = stringResource(R.string.forgot_error_no_encontrado)

    Scaffold(containerColor = FondoZesta) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ── TopBar ───────────────────────────────────────────────────
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

            Spacer(modifier = Modifier.height(48.dp))

            if (emailSent) {
                // ── Estado: email enviado ────────────────────────────────
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
                // ── Estado: formulario ───────────────────────────────────
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
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.forgot_placeholder_email),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = if (errorMessage != null) {
                        { Text(errorMessage!!) }
                    } else null,
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
                                try {
                                    FirebaseAuth.getInstance()
                                        .sendPasswordResetEmail(trimmed)
                                        .await()
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
    }
}