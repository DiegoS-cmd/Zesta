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
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zesta.app.R
import com.zesta.app.data.repository.ContactRepository
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun BusinessContactScreen(onBack: () -> Unit) {
    var nombreEmpresa by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var enviado by remember { mutableStateOf(false) }
    var nombreError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val contactRepository = remember { ContactRepository() }

    val strNombreVacio = stringResource(R.string.empresa_error_nombre_vacio)
    val strCorreoVacio = stringResource(R.string.empresa_error_correo_vacio)
    val strCorreoInvalido = stringResource(R.string.empresa_error_correo_invalido)
    val strMensajeVacio = stringResource(R.string.empresa_error_mensaje_vacio)
    val strErrorEnvio = stringResource(R.string.empresa_error_envio)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = FondoPlaceholderZesta,
        unfocusedContainerColor = FondoPlaceholderZesta,
        focusedBorderColor = NaranjaZesta,
        unfocusedBorderColor = FondoPlaceholderZesta,
        cursorColor = TextoPrincipalZesta
    )
    val fieldShape = RoundedCornerShape(14.dp)

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

            Spacer(modifier = Modifier.height(48.dp))

            if (enviado) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = VerdeExitoZesta,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.empresa_exito_titulo),
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.empresa_exito_cuerpo),
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
                        imageVector = Icons.Outlined.Business,
                        contentDescription = null,
                        tint = NaranjaZesta,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.empresa_titulo),
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.empresa_subtitulo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundarioZesta,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Nombre empresa
                OutlinedTextField(
                    value = nombreEmpresa,
                    onValueChange = { nombreEmpresa = it; nombreError = null },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.empresa_placeholder_nombre),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    isError = nombreError != null,
                    supportingText = if (nombreError != null) {{ Text(nombreError!!) }} else null,
                    shape = fieldShape,
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Correo de contacto
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; correoError = null },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.empresa_placeholder_correo),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = correoError != null,
                    supportingText = if (correoError != null) {{ Text(correoError!!) }} else null,
                    shape = fieldShape,
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mensaje
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it; mensajeError = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.empresa_placeholder_mensaje),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = false,
                    maxLines = 6,
                    isError = mensajeError != null,
                    supportingText = if (mensajeError != null) {{ Text(mensajeError!!) }} else null,
                    shape = fieldShape,
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(28.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = NaranjaZesta)
                } else {
                    PrimaryGradientButton(
                        text = stringResource(R.string.empresa_enviar),
                        onClick = {
                            var valid = true
                            if (nombreEmpresa.isBlank()) {
                                nombreError = strNombreVacio; valid = false
                            }
                            val correoTrimmed = correo.trim()
                            if (correoTrimmed.isBlank()) {
                                correoError = strCorreoVacio; valid = false
                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoTrimmed).matches()) {
                                correoError = strCorreoInvalido; valid = false
                            }
                            if (mensaje.isBlank()) {
                                mensajeError = strMensajeVacio; valid = false
                            }
                            if (!valid) return@PrimaryGradientButton

                            scope.launch {
                                isLoading = true
                                val result = contactRepository.enviarSolicitudEmpresa(
                                    nombre = nombreEmpresa.trim(),
                                    correo = correoTrimmed,
                                    mensaje = mensaje.trim()
                                )
                                if (result.isSuccess) {
                                    enviado = true
                                } else {
                                    mensajeError = strErrorEnvio
                                }
                                isLoading = false
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}