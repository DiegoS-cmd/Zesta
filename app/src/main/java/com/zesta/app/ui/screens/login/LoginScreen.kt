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
 * Pantalla principal de inicio de sesión de la aplicación Zesta.
 *
 * Esta pantalla permite:
 * - Autenticación mediante email y contraseña.
 * - Inicio de sesión con Google.
 * - Acceso como invitado sin autenticación.
 * - Navegación a registro, recuperación de contraseña y acceso empresarial.
 *
 * También gestiona:
 * - Mensajes de error de autenticación.
 * - Estado visual de errores.
 * - Opción de reactivación de cuenta si está deshabilitada.
 *
 * @param email Email actual introducido por el usuario.
 * @param password Contraseña actual introducida por el usuario.
 * @param errorMessage Mensaje de error mostrado en caso de fallo de login.
 * @param onEmailChange Callback que se ejecuta cuando cambia el email.
 * @param onPasswordChange Callback que se ejecuta cuando cambia la contraseña.
 * @param onLoginClick Callback para ejecutar el login con email/contraseña.
 * @param onGoRegister Navegación a la pantalla de registro.
 * @param onGoogleSignIn Acción de inicio de sesión con Google.
 * @param onForgotPassword Navegación a recuperación de contraseña.
 * @param onContinueAsGuestClick Acceso como usuario invitado.
 * @param onEresEmpresa Navegación al flujo de contacto empresarial.
 * @param onReactivateAccount Acción para reactivar cuenta deshabilitada.
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

        // Espaciado superior para layout visual
        Spacer(modifier = Modifier.height(36.dp))

        /**
         * Título de bienvenida de la pantalla de login.
         */
        Text(
            text = stringResource(R.string.inicio_sesion_bienvenida),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        /**
         * Logo principal de la aplicación.
         */
        Image(
            painter = painterResource(id = R.drawable.logo_zesta),
            contentDescription = stringResource(R.string.inicio_sesion_descripcion_logo),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(36.dp))

        /**
         * Campo de email del usuario.
         */
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.inicio_sesion_email))
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

        /**
         * Campo de contraseña del usuario.
         */
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.inicio_sesion_contrasena))
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

        /**
         * Sección de error de autenticación.
         * Si el mensaje contiene "deshabilitada", se muestra opción de reactivación.
         */
        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            if (errorMessage.contains("deshabilitada", ignoreCase = true)) {
                TextButton(onClick = onReactivateAccount) {
                    Text(stringResource(R.string.login_reactivar_cuenta))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        /**
         * Botón principal de inicio de sesión.
         */
        PrimaryGradientButton(
            text = stringResource(R.string.inicio_sesion_entrar),
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        /**
         * Separador visual entre login y Google Sign-In.
         */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("o")
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        /**
         * Botón de inicio de sesión con Google.
         */
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
            Row {
                Image(
                    painter = painterResource(R.drawable.logo_google),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = stringResource(R.string.login_google),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /**
         * Botón de navegación a registro.
         */
        PrimaryGradientButton(
            text = stringResource(R.string.inicio_sesion_registrarse),
            onClick = onGoRegister
        )

        Spacer(modifier = Modifier.height(14.dp))

        /**
         * Enlaces secundarios:
         * - Recuperar contraseña
         * - Continuar como invitado
         * - Contacto empresarial
         */
        TextButton(onClick = onForgotPassword) {
            Text(stringResource(R.string.inicio_sesion_olvide_contrasena))
        }

        TextButton(onClick = onContinueAsGuestClick) {
            Text(stringResource(R.string.inicio_sesion_continuar_invitado))
        }

        TextButton(onClick = onEresEmpresa) {
            Text(stringResource(R.string.login_eres_empresa))
        }
    }
}
