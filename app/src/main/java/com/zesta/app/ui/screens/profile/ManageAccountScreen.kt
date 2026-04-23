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

@Composable
fun ManageAccountScreen(
    isGuest: Boolean,
    userName: String,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    val errorTelefono = stringResource(R.string.carrito_error_telefono)
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddressSheet by remember { mutableStateOf(false) }

    val profileImageBase64 by authViewModel.profileImageUrl.collectAsState()
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

    var showImageSourceDialog by remember { mutableStateOf(false) }

    val cameraImageUri = remember {
        val file = File(context.cacheDir, "images/profile_photo_${System.currentTimeMillis()}.jpg")
            .also { it.parentFile?.mkdirs() }
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val base64 = uriToBase64(context, it)
            if (base64 != null) authViewModel.setProfileImageBase64(base64)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val base64 = uriToBase64(context, cameraImageUri)
            if (base64 != null) authViewModel.setProfileImageBase64(base64)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(cameraImageUri)
    }

    var phone by remember(authState.currentUser) { mutableStateOf(authState.currentUser?.telefono ?: "") }
    var phoneSuccess by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var isSavingPhone by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TopBar
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.size(46.dp).clip(CircleShape)
                    .background(BlancoZesta).border(1.dp, BordeIconoZesta, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.accesibilidad_volver), tint = NegroZesta)
            }
            Text(
                text = stringResource(R.string.perfil_gestionar_cuenta),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPrincipalZesta,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(110.dp).clip(CircleShape).background(FondoPlaceholderZesta)
                .then(if (!isGuest) Modifier.clickable { showImageSourceDialog = true } else Modifier),
            contentAlignment = Alignment.Center
        ) {
            val bmp = profileBitmap
            if (bmp != null) {
                Image(bitmap = bmp.asImageBitmap(), contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                    modifier = Modifier.size(110.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            } else {
                Image(painter = painterResource(R.drawable.logo_zesta), contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                    modifier = Modifier.size(70.dp).padding(8.dp), contentScale = ContentScale.Fit)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isGuest) {
            Text(stringResource(R.string.gestionar_cuenta_descripcion_invitado), style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta)
            Spacer(modifier = Modifier.height(36.dp))
            PrimaryGradientButton(text = stringResource(R.string.inicio_sesion_entrar), onClick = onLoginClick)
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryGradientButton(text = stringResource(R.string.inicio_sesion_registrarse), onClick = onRegisterClick)
        } else {
            Text(
                text = stringResource(R.string.gestionar_cuenta_datos_entrega),
                style = MaterialTheme.typography.titleMedium, color = TextoPrincipalZesta,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            // ── Teléfono
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; phoneError = null; phoneSuccess = false },
                label = { Text(stringResource(R.string.carrito_campo_telefono), color = TextoSecundarioZesta) },
                trailingIcon = {
                    if (phone.isNotBlank()) {
                        IconButton(onClick = {
                            authViewModel.clearProfileField("telefono",
                                onSuccess = { phone = ""; phoneSuccess = false },
                                onError = { phoneError = it })
                        }) {
                            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.gestionar_cuenta_eliminar), tint = TextoSecundarioZesta)
                        }
                    }
                },
                singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FondoTarjetaRestauranteZesta, unfocusedContainerColor = FondoTarjetaRestauranteZesta,
                    focusedBorderColor = NaranjaZesta, unfocusedBorderColor = BordeCirculoZesta,
                    cursorColor = NegroZesta, focusedTextColor = TextoPrincipalZesta, unfocusedTextColor = TextoPrincipalZesta
                )
            )
            phoneError?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
            if (phoneSuccess) Text(stringResource(R.string.gestionar_cuenta_guardado_ok), style = MaterialTheme.typography.bodySmall, color = NaranjaZesta)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                    .background(brush = Brush.horizontalGradient(colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)))
                    .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                    .clickable(enabled = !isSavingPhone) {
                        if (phone.trim().length < 9 || !phone.trim().all { it.isDigit() }) { phoneError = errorTelefono; return@clickable }
                        isSavingPhone = true
                        authViewModel.updateProfile(
                            telefono = phone, direccion = authState.currentUser?.direccion.orEmpty(),
                            onSuccess = { isSavingPhone = false; phoneSuccess = true },
                            onError = { isSavingPhone = false; phoneError = it }
                        )
                    }.padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSavingPhone) stringResource(R.string.carrito_guardando) else stringResource(R.string.gestionar_cuenta_guardar_telefono),
                    color = BlancoZesta, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── ✅ Dirección — botón que abre el sheet completo
            val direccionActiva = authState.currentUser?.direccion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (!direccionActiva.isNullOrBlank()) FondoSeleccionNaranjaZesta else FondoTarjetaRestauranteZesta)
                    .border(
                        width = if (!direccionActiva.isNullOrBlank()) 1.5.dp else 1.dp,
                        color = if (!direccionActiva.isNullOrBlank()) NaranjaZesta else BordeCirculoZesta,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { showAddressSheet = true }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Outlined.LocationOn, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                Text(
                    text = if (!direccionActiva.isNullOrBlank()) direccionActiva else stringResource(R.string.inicio_direccion),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (!direccionActiva.isNullOrBlank()) NaranjaZesta else TextoPrincipalZesta,
                    fontWeight = if (!direccionActiva.isNullOrBlank()) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Icon(Icons.Outlined.Edit, null, tint = NaranjaZesta, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = BordeCirculoZesta)
            Spacer(modifier = Modifier.height(28.dp))

            ChangePasswordSection()

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = BordeCirculoZesta)
            Spacer(modifier = Modifier.height(28.dp))

            PrimaryGradientButton(text = stringResource(R.string.perfil_cerrar_sesion), onClick = onLogoutClick)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.gestionar_cuenta_volver_perfil),
            style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta,
            modifier = Modifier.clickable { onBackClick() }
        )
    }

    // ✅ Sheet de direcciones (mismo que en HomeScreen)
    if (showAddressSheet) {
        AddressBottomSheet(
            currentUser = authState.currentUser,
            authViewModel = authViewModel,
            onDismiss = { showAddressSheet = false }
        )
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            containerColor = FondoTarjetaRestauranteZesta,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(stringResource(R.string.gestionar_cuenta_foto_titulo),
                    style = MaterialTheme.typography.titleMedium, color = TextoPrincipalZesta, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(FondoZesta).border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable {
                                showImageSourceDialog = false
                                val granted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (granted) cameraLauncher.launch(cameraImageUri)
                                else cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.CameraAlt, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                        Text(stringResource(R.string.gestionar_cuenta_foto_camara), style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(FondoZesta).border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable { showImageSourceDialog = false; galleryLauncher.launch("image/*") }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.PhotoLibrary, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                        Text(stringResource(R.string.gestionar_cuenta_foto_galeria), style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta, fontWeight = FontWeight.SemiBold)
                    }
                    if (profileBitmap != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                .background(FondoZesta).border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                                .clickable { showImageSourceDialog = false; authViewModel.clearProfileImage() }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Outlined.Delete, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                            Text(stringResource(R.string.gestionar_cuenta_foto_eliminar), style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta, fontWeight = FontWeight.SemiBold)
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

@Composable
private fun ChangePasswordSection() {
    val scope = rememberCoroutineScope()
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    val errContraseñaVacia = stringResource(R.string.intro_actual_contraseña)
    val errMinCaracteres = stringResource(R.string.nueva_contrasena_minimo)
    val errNoCoinciden = stringResource(R.string.contrasenas_no_coinciden)
    val errActualIncorrecta = stringResource(R.string.contrasena_actual_incorrecta)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = FondoTarjetaRestauranteZesta, unfocusedContainerColor = FondoTarjetaRestauranteZesta,
        focusedBorderColor = NaranjaZesta, unfocusedBorderColor = BordeCirculoZesta,
        cursorColor = NegroZesta, focusedTextColor = TextoPrincipalZesta, unfocusedTextColor = TextoPrincipalZesta
    )
    val fieldShape = RoundedCornerShape(14.dp)

    Text(text = stringResource(R.string.cambiar_contraseña), style = MaterialTheme.typography.titleMedium,
        color = TextoPrincipalZesta, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))

    if (successMessage != null) {
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(FondoTarjetaRestauranteZesta).border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Outlined.CheckCircle, null, tint = VerdeExitoZesta, modifier = Modifier.size(22.dp))
            Text(text = stringResource(R.string.actualizar_contraseña), style = MaterialTheme.typography.bodyMedium, color = TextoPrincipalZesta)
        }
        return
    }

    OutlinedTextField(
        value = currentPassword, onValueChange = { currentPassword = it; currentPasswordError = null },
        label = { Text(stringResource(R.string.actual_contraseña), color = TextoSecundarioZesta) },
        singleLine = true, visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(), shape = fieldShape,
        isError = currentPasswordError != null,
        supportingText = if (currentPasswordError != null) {{ Text(currentPasswordError!!) }} else null,
        colors = fieldColors
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = newPassword, onValueChange = { newPassword = it; newPasswordError = null },
        label = { Text(stringResource(R.string.nueva_contraseña), color = TextoSecundarioZesta) },
        singleLine = true, visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(), shape = fieldShape,
        isError = newPasswordError != null,
        supportingText = if (newPasswordError != null) {{ Text(newPasswordError!!) }} else null,
        colors = fieldColors
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = confirmPassword, onValueChange = { confirmPassword = it; confirmPasswordError = null },
        label = { Text(stringResource(R.string.conf_nueva_contraseña), color = TextoSecundarioZesta) },
        singleLine = true, visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(), shape = fieldShape,
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
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                .background(brush = Brush.horizontalGradient(colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)))
                .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                .clickable {
                    var valid = true
                    if (currentPassword.isBlank()) { currentPasswordError = errContraseñaVacia; valid = false }
                    if (newPassword.length < 6) { newPasswordError = errMinCaracteres; valid = false }
                    if (confirmPassword != newPassword) { confirmPasswordError = errNoCoinciden; valid = false }
                    if (!valid) return@clickable
                    scope.launch {
                        isLoading = true
                        try {
                            val auth = FirebaseAuth.getInstance()
                            val user = auth.currentUser
                            val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)
                            user?.reauthenticate(credential)?.await()
                            user?.updatePassword(newPassword)?.await()
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
            Text(text = stringResource(R.string.cambiar_contraseña), color = BlancoZesta,
                style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        if (original == null) return null
        val scaled = Bitmap.createScaledBitmap(original, 300, 300, true)
        val baos = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    } catch (e: Exception) { null }
}