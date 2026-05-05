package com.zesta.app.ui.screens.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.zesta.app.R
import com.zesta.app.ui.components.AddressBottomSheet
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.ui.text.style.TextAlign
import com.zesta.app.ui.components.RatingDialog

/**
 * Pantalla de gestión de cuenta del usuario.
 *
 * Permite al usuario registrado:
 * - Ver y cambiar su foto de perfil (cámara o galería)
 * - Guardar/eliminar su número de teléfono
 * - Cambiar su dirección de entrega
 * - Cambiar su contraseña
 * - Cerrar sesión
 * - Deshabilitar su cuenta (soft delete — no borra datos)
 *
 * Si el usuario es invitado ([isGuest] = true), se muestran
 * únicamente los botones de login y registro.
 *
 * @param isGuest          Indica si el usuario actual es un invitado (sin cuenta).
 * @param userName         Nombre del usuario autenticado.
 * @param onBackClick      Callback para volver a la pantalla anterior.
 * @param onLoginClick     Callback para navegar al login (solo modo invitado).
 * @param onRegisterClick  Callback para navegar al registro (solo modo invitado).
 * @param onLogoutClick    Callback ejecutado al cerrar sesión.
 * @param onDeleteAccountSuccess Callback ejecutado tras deshabilitar la cuenta con éxito.
 * @param authViewModel    ViewModel que gestiona el estado de autenticación y perfil.
 */
@Composable
fun ManageAccountScreen(
    isGuest: Boolean,
    userName: String,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    val errorTelefono = stringResource(R.string.gestionar_error_telefono)

    // Estado general de autenticación (usuario actual, errores, etc.)
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Controla la visibilidad del bottom sheet de direcciones
    var showAddressSheet by remember { mutableStateOf(false) }

    // Imagen de perfil almacenada como Base64 en el ViewModel
    val profileImageBase64 by authViewModel.profileImageUrl.collectAsState()

    // Decodifica el Base64 a Bitmap de forma asíncrona; se recalcula cuando cambia el Base64
    val profileBitmap by produceState<Bitmap?>(initialValue = null, key1 = profileImageBase64) {
        value = try {
            val base64 = profileImageBase64
            if (base64.isNullOrBlank()) null
            else {
                val clean = base64.replace("\\s".toRegex(), "")
                val bytes = Base64.decode(clean, Base64.NO_WRAP)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: Exception) { null }
    }

    // Controla la visibilidad del diálogo para elegir fuente de imagen (cámara / galería)
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Estados para el flujo de deshabilitación de cuenta

    // Paso 1: diálogo de confirmación ("¿Seguro que quieres deshabilitar tu cuenta?")
    var showDisableConfirmDialog by remember { mutableStateOf(false) }
    // Paso 2: diálogo de valoración antes de irse
    var showRatingDialog by remember { mutableStateOf(false) }
    // Indica si la operación de deshabilitación está en curso (evita doble pulsación)
    var isDisablingAccount by remember { mutableStateOf(false) }
    // Mensaje de error visible en pantalla si falla la deshabilitación
    var disableErrorMessage by remember { mutableStateOf<String?>(null) }

    // URI temporal para la foto tomada con la cámara, almacenada en caché privada
    val cameraImageUri = remember {
        val file = File(context.cacheDir, "images/profile_photo_${System.currentTimeMillis()}.jpg")
            .also { it.parentFile?.mkdirs() }
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    // Launcher para seleccionar imagen de la galería del dispositivo
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val base64 = uriToBase64(context, it)
            if (base64 != null) authViewModel.setProfileImageBase64(base64)
        }
    }

    // Launcher para capturar foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val base64 = uriToBase64(context, cameraImageUri)
            if (base64 != null) authViewModel.setProfileImageBase64(base64)
        }
    }

    // Launcher para solicitar permiso de cámara; lanza la cámara si se concede
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(cameraImageUri)
    }

    // Teléfono local: se inicializa con el valor del usuario actual y se sincroniza si cambia
    var phone by remember(authState.currentUser) { mutableStateOf(authState.currentUser?.telefono ?: "") }
    var phoneSuccess by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var isSavingPhone by remember { mutableStateOf(false) }

    // Contenido principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TopBar: botón atrás + título centrado
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(BlancoZesta)
                    .border(1.dp, BordeIconoZesta, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.accesibilidad_volver),
                    tint = NegroZesta
                )
            }
            Text(
                text = stringResource(R.string.perfil_gestionar_cuenta),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPrincipalZesta,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Avatar de perfil
        // Si el usuario no es invitado, al pulsar se muestra el diálogo de fuente de imagen
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(FondoPlaceholderZesta)
                .then(if (!isGuest) Modifier.clickable { showImageSourceDialog = true } else Modifier),
            contentAlignment = Alignment.Center
        ) {
            val bmp = profileBitmap
            if (bmp != null) {
                // Foto personalizada del usuario
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                    modifier = Modifier.size(110.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder con el logo de Zesta
                Image(
                    painter = painterResource(R.drawable.logo_zesta),
                    contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                    modifier = Modifier.size(70.dp).padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isGuest) {
            // Vista invitado: mensaje + botones de acceso
            Text(
                stringResource(R.string.gestionar_cuenta_descripcion_invitado),
                style = MaterialTheme.typography.bodyLarge,
                color = TextoPrincipalZesta
            )
            Spacer(modifier = Modifier.height(36.dp))
            PrimaryGradientButton(text = stringResource(R.string.inicio_sesion_entrar), onClick = onLoginClick)
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryGradientButton(text = stringResource(R.string.inicio_sesion_registrarse), onClick = onRegisterClick)

        } else {
            //  Vista usuario autenticado

            Text(
                text = stringResource(R.string.gestionar_cuenta_datos_entrega),
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipalZesta,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            // Campo de teléfono
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; phoneError = null; phoneSuccess = false },
                label = { Text(stringResource(R.string.carrito_campo_telefono), color = TextoSecundarioZesta) },
                trailingIcon = {
                    // Icono de borrar: solo visible si hay texto escrito
                    if (phone.isNotBlank()) {
                        IconButton(onClick = {
                            authViewModel.clearProfileField(
                                "telefono",
                                onSuccess = { phone = ""; phoneSuccess = false },
                                onError = { phoneError = it }
                            )
                        }) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.gestionar_cuenta_eliminar),
                                tint = TextoSecundarioZesta
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FondoTarjetaRestauranteZesta,
                    unfocusedContainerColor = FondoTarjetaRestauranteZesta,
                    focusedBorderColor = NaranjaZesta,
                    unfocusedBorderColor = BordeCirculoZesta,
                    cursorColor = NegroZesta,
                    focusedTextColor = TextoPrincipalZesta,
                    unfocusedTextColor = TextoPrincipalZesta
                )
            )
            phoneError?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            if (phoneSuccess) {
                Text(
                    stringResource(R.string.gestionar_cuenta_guardado_ok),
                    style = MaterialTheme.typography.bodySmall,
                    color = NaranjaZesta
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón guardar teléfono
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(brush = Brush.horizontalGradient(listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)))
                    .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                    .clickable(enabled = !isSavingPhone) {
                        // Validación: mínimo 9 dígitos y solo números tiene que empezar por 6,7,8,9
                        if (phone.isNotBlank()) {
                            if (!isValidSpanishPhone(phone)) {
                                phoneError = errorTelefono
                                return@clickable
                            }
                        }
                        isSavingPhone = true
                        authViewModel.updateProfile(
                            telefono = phone,
                            direccion = authState.currentUser?.direccion.orEmpty(),
                            onSuccess = { isSavingPhone = false; phoneSuccess = true },
                            onError = { isSavingPhone = false; phoneError = it }
                        )
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSavingPhone) stringResource(R.string.carrito_guardando)
                    else stringResource(R.string.gestionar_cuenta_guardar_telefono),
                    color = BlancoZesta,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            //  Selector de dirección de entrega
            val direccionActiva = authState.currentUser?.direccion
            val tieneDireccion = !direccionActiva.isNullOrBlank()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (tieneDireccion) FondoSeleccionNaranjaZesta else FondoTarjetaRestauranteZesta)
                    .border(
                        width = if (tieneDireccion) 1.5.dp else 1.dp,
                        color = if (tieneDireccion) NaranjaZesta else BordeCirculoZesta,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { showAddressSheet = true }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Outlined.LocationOn, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                Text(
                    text = if (tieneDireccion) direccionActiva!! else stringResource(R.string.inicio_direccion),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (tieneDireccion) NaranjaZesta else TextoPrincipalZesta,
                    fontWeight = if (tieneDireccion) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                // Icono de lápiz indicando que es editable
                Icon(Icons.Outlined.Edit, null, tint = NaranjaZesta, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = BordeCirculoZesta)
            Spacer(modifier = Modifier.height(28.dp))

            //  Sección cambio de contraseña
            ChangePasswordSection()

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = BordeCirculoZesta)
            Spacer(modifier = Modifier.height(28.dp))

            //  Cerrar sesión
            PrimaryGradientButton(
                text = stringResource(R.string.perfil_cerrar_sesion),
                onClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            //  Error de deshabilitación (si ocurrió)
            disableErrorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }

            //  Botón deshabilitar cuenta
            // No borra datos: activa el campo `isDisabled = true` en Firestore
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFD94B57))
                    .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                    .clickable(enabled = !isDisablingAccount) {
                        disableErrorMessage = null
                        showDisableConfirmDialog = true
                    }
                    .padding(horizontal = 18.dp, vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.eliminar_cuenta),
                    color = BlancoZesta,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        //  Enlace para volver al perfil
        Text(
            text = stringResource(R.string.gestionar_cuenta_volver_perfil),
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta,
            modifier = Modifier.clickable { onBackClick() }
        )
    }

    //  Bottom sheet: gestión de direcciones
    if (showAddressSheet) {
        AddressBottomSheet(
            currentUser = authState.currentUser,
            authViewModel = authViewModel,
            onDismiss = { showAddressSheet = false }
        )
    }

    //  Diálogo 1: confirmación antes de deshabilitar cuenta
    if (showDisableConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDisableConfirmDialog = false },
            containerColor = FondoTarjetaRestauranteZesta,
            shape = RoundedCornerShape(24.dp),
            icon = {
                // Icono de advertencia con fondo rojo semitransparente
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD94B57).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.WarningAmber,
                        contentDescription = null,
                        tint = Color(0xFFD94B57),
                        modifier = Modifier.size(34.dp)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.eliminar_cuenta_titulo),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.eliminar_cuenta_mensaje),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundarioZesta,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                // Al confirmar, se avanza al diálogo de valoración (paso 2)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFD94B57))
                        .clickable {
                            showDisableConfirmDialog = false
                            showRatingDialog = true
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.gestionar_cuenta_eliminar),
                        color = BlancoZesta,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisableConfirmDialog = false }) {
                    Text(
                        text = stringResource(R.string.inicio_sheet_cancelar),
                        textAlign = TextAlign.Center,
                        color = TextoSecundarioZesta,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }

    //  Diálogo 2: valoración antes de deshabilitar
    // El usuario puede puntuar la app o cerrar sin puntuar; en ambos casos
    // se deshabilita la cuenta (isDisabled = true en Firestore) y se cierra sesión.
    if (showRatingDialog) {
        RatingDialog(
            onDismiss = {
                // Cerró sin valorar → deshabilitar igualmente
                if (!isDisablingAccount) {
                    showRatingDialog = false
                    isDisablingAccount = true
                    authViewModel.disableAccount(
                        onSuccess = {
                            isDisablingAccount = false
                            onDeleteAccountSuccess()
                        },
                        onError = { error ->
                            isDisablingAccount = false
                            disableErrorMessage = error
                        }
                    )
                }
            },
            onSubmit = { stars ->
                // Guardó valoración → enviar rating y luego deshabilitar
                if (!isDisablingAccount) {
                    showRatingDialog = false
                    isDisablingAccount = true
                    authViewModel.sendRatingAndDisableAccount(
                        rating = stars.toString(),
                        onSuccess = {
                            isDisablingAccount = false
                            onDeleteAccountSuccess()
                        },
                        onError = { error ->
                            isDisablingAccount = false
                            disableErrorMessage = error
                        }
                    )
                }
            }
        )
    }

    //  Diálogo: selección de fuente de imagen de perfil
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            containerColor = FondoTarjetaRestauranteZesta,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.gestionar_cuenta_foto_titulo),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Opción: cámara
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(FondoZesta)
                            .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable {
                                showImageSourceDialog = false
                                val granted = ContextCompat.checkSelfPermission(
                                    context, android.Manifest.permission.CAMERA
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (granted) cameraLauncher.launch(cameraImageUri)
                                else cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.CameraAlt, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                        Text(
                            stringResource(R.string.gestionar_cuenta_foto_camara),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Opción: galería
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(FondoZesta)
                            .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable { showImageSourceDialog = false; galleryLauncher.launch("image/*") }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.PhotoLibrary, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                        Text(
                            stringResource(R.string.gestionar_cuenta_foto_galeria),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Opción: eliminar foto (solo visible si hay foto personalizada)
                    if (profileBitmap != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(FondoZesta)
                                .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                                .clickable {
                                    showImageSourceDialog = false
                                    authViewModel.clearProfileImage()
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Outlined.Delete, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                            Text(
                                stringResource(R.string.gestionar_cuenta_foto_eliminar),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoPrincipalZesta,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text(stringResource(R.string.inicio_sheet_cancelar), color = TextoSecundarioZesta)
                }
            }
        )
    }
}

/**
 * Sección de cambio de contraseña dentro de [ManageAccountScreen].
 *
 * Muestra tres campos (contraseña actual, nueva, confirmación) y un botón
 * que reautentica al usuario con Firebase antes de actualizar la contraseña.
 * Si el cambio tiene éxito, reemplaza el formulario por un mensaje de confirmación.
 *
 * Es un composable privado porque solo se usa en esta pantalla.
 */
@Composable
private fun ChangePasswordSection() {
    val scope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    // Cuando no es null, se oculta el formulario y se muestra el banner de éxito
    var successMessage by remember { mutableStateOf<String?>(null) }

    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Strings cargados fuera de la lambda para evitar llamar a stringResource
    // dentro de un contexto no-composable (coroutine)
    val errContraseñaVacia = stringResource(R.string.intro_actual_contraseña)
    val errMinCaracteres = stringResource(R.string.nueva_contrasena_minimo)
    val errNoCoinciden = stringResource(R.string.contrasenas_no_coinciden)
    val errActualIncorrecta = stringResource(R.string.contrasena_actual_incorrecta)

    // Colores y forma reutilizados en los tres campos
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = FondoTarjetaRestauranteZesta,
        unfocusedContainerColor = FondoTarjetaRestauranteZesta,
        focusedBorderColor = NaranjaZesta,
        unfocusedBorderColor = BordeCirculoZesta,
        cursorColor = NegroZesta,
        focusedTextColor = TextoPrincipalZesta,
        unfocusedTextColor = TextoPrincipalZesta
    )
    val fieldShape = RoundedCornerShape(14.dp)

    Text(
        text = stringResource(R.string.cambiar_contraseña),
        style = MaterialTheme.typography.titleMedium,
        color = TextoPrincipalZesta,
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    )

    //  Banner de éxito: reemplaza el formulario tras un cambio correcto
    if (successMessage != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(FondoTarjetaRestauranteZesta)
                .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Outlined.CheckCircle, null, tint = VerdeExitoZesta, modifier = Modifier.size(22.dp))
            Text(
                text = stringResource(R.string.actualizar_contraseña),
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPrincipalZesta
            )
        }
        return // No renderizar el formulario si ya se cambió la contraseña
    }

    //  Formulario
    OutlinedTextField(
        value = currentPassword,
        onValueChange = { currentPassword = it; currentPasswordError = null },
        label = { Text(stringResource(R.string.actual_contraseña), color = TextoSecundarioZesta) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = fieldShape,
        isError = currentPasswordError != null,
        supportingText = if (currentPasswordError != null) {{ Text(currentPasswordError!!) }} else null,
        colors = fieldColors
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it; newPasswordError = null },
        label = { Text(stringResource(R.string.nueva_contraseña), color = TextoSecundarioZesta) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = fieldShape,
        isError = newPasswordError != null,
        supportingText = if (newPasswordError != null) {{ Text(newPasswordError!!) }} else null,
        colors = fieldColors
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it; confirmPasswordError = null },
        label = { Text(stringResource(R.string.conf_nueva_contraseña), color = TextoSecundarioZesta) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = fieldShape,
        isError = confirmPasswordError != null,
        supportingText = if (confirmPasswordError != null) {{ Text(confirmPasswordError!!) }} else null,
        colors = fieldColors
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (isLoading) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NaranjaZesta)
        }
    } else {
        //  Botón cambiar contraseña
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(brush = Brush.horizontalGradient(listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)))
                .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                .clickable {
                    // Validación local antes de llamar a Firebase
                    var valid = true
                    if (currentPassword.isBlank()) { currentPasswordError = errContraseñaVacia; valid = false }
                    if (newPassword.length < 6) { newPasswordError = errMinCaracteres; valid = false }
                    if (confirmPassword != newPassword) { confirmPasswordError = errNoCoinciden; valid = false }
                    if (!valid) return@clickable

                    scope.launch {
                        isLoading = true
                        try {
                            val user = FirebaseAuth.getInstance().currentUser
                            // Reautenticación necesaria para operaciones sensibles en Firebase Auth
                            val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)
                            user?.reauthenticate(credential)?.await()
                            user?.updatePassword(newPassword)?.await()
                            // Limpiar campos tras éxito
                            currentPassword = ""; newPassword = ""; confirmPassword = ""
                            successMessage = "ok"
                        } catch (e: Exception) {
                            currentPasswordError = errActualIncorrecta
                        }
                        isLoading = false
                    }
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.cambiar_contraseña),
                color = BlancoZesta,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Convierte una [Uri] de imagen (galería o cámara) a una cadena Base64.
 *
 * El proceso es:
 * 1. Abre el stream del contenido asociado a la URI.
 * 2. Decodifica el stream a [Bitmap].
 * 3. Escala la imagen a 300×300 px para reducir el tamaño almacenado en Firestore.
 * 4. Comprime a JPEG con calidad 70 y codifica en Base64 sin saltos de línea ([Base64.NO_WRAP]).
 *
 * @param context Contexto necesario para acceder al [ContentResolver].
 * @param uri     URI de la imagen seleccionada o capturada.
 * @return        String Base64 de la imagen, o `null` si ocurre algún error.
 */
fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        if (original == null) return null
        // Reducir a 300×300 para no exceder el límite de tamaño de Firestore (1 MB por documento)
        val scaled = Bitmap.createScaledBitmap(original, 300, 300, true)
        val baos = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    } catch (e: Exception) { null }
}
private fun isValidSpanishPhone(phone: String): Boolean {
    val regex = Regex("^[6789]\\d{8}$")
    return regex.matches(phone.trim())
}