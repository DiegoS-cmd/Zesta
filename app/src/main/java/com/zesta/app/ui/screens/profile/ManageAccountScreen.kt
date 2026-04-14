package com.zesta.app.ui.screens.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Build
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale


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
    val errorDireccion = stringResource(R.string.carrito_error_direccion)
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val profileImageBase64 by authViewModel.profileImageUrl.collectAsState()
    val profileBitmap by produceState<Bitmap?>(initialValue = null, key1 = profileImageBase64) {
        value = try {
            val base64 = profileImageBase64
            android.util.Log.d("ZESTA_PHOTO", "produceState ejecutado, base64 es null: ${base64 == null}, longitud: ${base64?.length}")
            if (base64.isNullOrBlank()) {
                null
            } else {
                val clean = base64.replace("\\s".toRegex(), "")
                val bytes = Base64.decode(clean, Base64.NO_WRAP)
                android.util.Log.d("ZESTA_PHOTO", "Bytes decodificados: ${bytes.size}")
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                android.util.Log.d("ZESTA_PHOTO", "Bitmap resultado: $bmp")
                bmp
            }
        } catch (e: Exception) {
            android.util.Log.e("ZESTA_PHOTO", "Error bitmap: ${e.message}")
            null
        }
    }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val cameraImageUri = remember {
        val file = File(context.cacheDir, "images/profile_photo_${System.currentTimeMillis()}.jpg")
            .also { it.parentFile?.mkdirs() }
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val base64 = uriToBase64(context, it)
            android.util.Log.d("ZESTA_PHOTO", "Base64 desde galería: ${base64?.take(30)}")
            if (base64 != null) authViewModel.setProfileImageBase64(base64)
        }
    }

// Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val base64 = uriToBase64(context, cameraImageUri)
            android.util.Log.d("ZESTA_PHOTO", "Base64 desde cámara: ${base64?.take(30)}")
            if (base64 != null) authViewModel.setProfileImageBase64(base64)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(cameraImageUri)
    }

    var phone by remember(authState.currentUser) { mutableStateOf(authState.currentUser?.telefono ?: "") }
    var address by remember(authState.currentUser) { mutableStateOf(authState.currentUser?.direccion ?: "") }

    var phoneSuccess by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var isSavingPhone by remember { mutableStateOf(false) }

    var addressSuccess by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var isSavingAddress by remember { mutableStateOf(false) }
    var isLocating by remember { mutableStateOf(false) }

    fun resolveLocation() {
        isLocating = true
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationToken = CancellationTokenSource()
        try {
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                                address = addresses.firstOrNull()?.getAddressLine(0) ?: ""
                                addressError = null; addressSuccess = false; isLocating = false
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            address = addresses?.firstOrNull()?.getAddressLine(0) ?: ""
                            addressError = null; addressSuccess = false; isLocating = false
                        }
                    } else isLocating = false
                }.addOnFailureListener { isLocating = false }
        } catch (e: SecurityException) { isLocating = false }
    }

    val locationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) resolveLocation()
    }

    fun requestOrResolveLocation() {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fine || coarse) resolveLocation()
        else locationLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

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
                .size(110.dp)
                .clip(CircleShape)
                .background(FondoPlaceholderZesta)
                .then(if (!isGuest) Modifier.clickable { showImageSourceDialog = true } else Modifier),
            contentAlignment = Alignment.Center
        ) {
            val bmp = profileBitmap
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                    modifier = Modifier.size(110.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
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

            // Teléfono
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

            // Dirección
            OutlinedTextField(
                value = address,
                onValueChange = { address = it; addressError = null; addressSuccess = false },
                label = { Text(stringResource(R.string.carrito_campo_direccion), color = TextoSecundarioZesta) },
                trailingIcon = {
                    if (address.isNotBlank()) {
                        IconButton(onClick = {
                            authViewModel.clearProfileField("direccion",
                                onSuccess = { address = ""; addressSuccess = false },
                                onError = { addressError = it })
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clip(RoundedCornerShape(14.dp))
                    .background(FondoTarjetaRestauranteZesta).border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                    .clickable(enabled = !isLocating) { requestOrResolveLocation() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isLocating) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NaranjaZesta, strokeWidth = 2.dp)
                else Icon(Icons.Outlined.NearMe, contentDescription = null, tint = NaranjaZesta, modifier = Modifier.size(18.dp))
                Text(
                    text = if (isLocating) stringResource(R.string.inicio_sheet_localizando) else stringResource(R.string.inicio_sheet_ubicacion_actual),
                    style = MaterialTheme.typography.bodyMedium, color = NaranjaZesta, fontWeight = FontWeight.SemiBold
                )
            }

            addressError?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
            if (addressSuccess) Text(stringResource(R.string.gestionar_cuenta_guardado_ok), style = MaterialTheme.typography.bodySmall, color = NaranjaZesta)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                    .background(brush = Brush.horizontalGradient(colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)))
                    .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                    .clickable(enabled = !isSavingAddress) {
                        if (address.isBlank()) { addressError = errorDireccion; return@clickable }
                        isSavingAddress = true
                        authViewModel.updateProfile(
                            telefono = authState.currentUser?.telefono.orEmpty(), direccion = address,
                            onSuccess = { isSavingAddress = false; addressSuccess = true },
                            onError = { isSavingAddress = false; addressError = it }
                        )
                    }.padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSavingAddress) stringResource(R.string.carrito_guardando) else stringResource(R.string.gestionar_cuenta_guardar_direccion),
                    color = BlancoZesta, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold
                )
            }

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
                    // Cámara
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(FondoZesta).border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable {
                                showImageSourceDialog = false
                                val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                if (granted) cameraLauncher.launch(cameraImageUri)
                                else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.CameraAlt, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                        Text(stringResource(R.string.gestionar_cuenta_foto_camara), style = MaterialTheme.typography.bodyLarge, color = TextoPrincipalZesta, fontWeight = FontWeight.SemiBold)
                    }

                    // Galería
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

                    // Eliminar (solo si hay foto)
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
    } catch (e: Exception) {
        android.util.Log.e("ZESTA_PHOTO", "uriToBase64 error: ${e.message}")
        null
    }
}