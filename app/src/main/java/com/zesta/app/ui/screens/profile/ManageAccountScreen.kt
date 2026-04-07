package com.zesta.app.ui.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.zesta.app.R
import com.zesta.app.ui.components.PrimaryGradientButton
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import java.util.Locale
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
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
    val errorDireccion = stringResource(R.string.carrito_error_direccion)
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val profileImageUri by authViewModel.profileImageUri.collectAsState()
    var showImageSourceDialog by remember { mutableStateOf(false) }

// foto de cámara
    val cameraImageUri = remember {
        val file = File(context.cacheDir, "images/profile_photo_${System.currentTimeMillis()}.jpg")
            .also { it.parentFile?.mkdirs() }
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

// galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { authViewModel.setProfileImageUri(it)} }

// cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) authViewModel.setProfileImageUri(cameraImageUri) }

// permiso cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(cameraImageUri)
    }


    val displayName = if (isGuest || userName.isBlank()) {
        stringResource(R.string.perfil_nombre_invitado)
    } else {
        userName
    }

    var phone by remember(authState.currentUser) {
        mutableStateOf(authState.currentUser?.telefono ?: "")
    }
    var address by remember(authState.currentUser) {
        mutableStateOf(authState.currentUser?.direccion ?: "")
    }

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
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                            address = addresses.firstOrNull()?.getAddressLine(0) ?: ""
                            addressError = null
                            addressSuccess = false
                            isLocating = false
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        address = addresses?.firstOrNull()?.getAddressLine(0) ?: ""
                        addressError = null
                        addressSuccess = false
                        isLocating = false
                    }
                } else {
                    isLocating = false
                }
            }.addOnFailureListener {
                isLocating = false
            }
        } catch (e: SecurityException) {
            isLocating = false
        }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) resolveLocation()
    }

    fun requestOrResolveLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) resolveLocation()
        else locationLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
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
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(BlancoZesta)
                    .border(1.dp, BordeIconoZesta, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
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

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(FondoPlaceholderZesta)
                .clickable { showImageSourceDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                    tint = NegroZesta,
                    modifier = Modifier.size(60.dp)
                )
            }

            // Icono de editar encima
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(NaranjaZesta),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = BlancoZesta,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (isGuest) {
            Text(
                text = stringResource(R.string.gestionar_cuenta_descripcion_invitado),
                style = MaterialTheme.typography.bodyLarge,
                color = TextoPrincipalZesta
            )
            Spacer(modifier = Modifier.height(36.dp))
            PrimaryGradientButton(
                text = stringResource(R.string.inicio_sesion_entrar),
                onClick = onLoginClick
            )
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryGradientButton(
                text = stringResource(R.string.inicio_sesion_registrarse),
                onClick = onRegisterClick
            )
        } else {
            Text(
                text = stringResource(R.string.gestionar_cuenta_datos_entrega),
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipalZesta,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Telefono
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = null
                    phoneSuccess = false
                },
                label = {
                    Text(
                        stringResource(R.string.carrito_campo_telefono),
                        color = TextoSecundarioZesta
                    )
                },
                trailingIcon = {
                    if (phone.isNotBlank()) {
                        IconButton(onClick = {
                            authViewModel.clearProfileField(
                                field = "telefono",
                                onSuccess = {
                                    phone = ""
                                    phoneSuccess = false
                                },
                                onError = { phoneError = it }
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
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
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (phoneSuccess) {
                Text(
                    text = stringResource(R.string.gestionar_cuenta_guardado_ok),
                    style = MaterialTheme.typography.bodySmall,
                    color = NaranjaZesta
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)
                        )
                    )
                    .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                    .clickable(enabled = !isSavingPhone) {
                        if (phone.trim().length < 9 || !phone.trim().all { it.isDigit() }) {
                            phoneError = errorTelefono
                            return@clickable
                        }
                        isSavingPhone = true
                        authViewModel.updateProfile(
                            telefono = phone,
                            direccion = authState.currentUser?.direccion.orEmpty(),
                            onSuccess = {
                                isSavingPhone = false
                                phoneSuccess = true
                            },
                            onError = {
                                isSavingPhone = false
                                phoneError = it
                            }
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

            // Direccion
            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                    addressError = null
                    addressSuccess = false
                },
                label = {
                    Text(
                        stringResource(R.string.carrito_campo_direccion),
                        color = TextoSecundarioZesta
                    )
                },
                trailingIcon = {
                    if (address.isNotBlank()) {
                        IconButton(onClick = {
                            authViewModel.clearProfileField(
                                field = "direccion",
                                onSuccess = {
                                    address = ""
                                    addressSuccess = false
                                },
                                onError = { addressError = it }
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
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

            // Botón usar ubicación actual
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FondoTarjetaRestauranteZesta)
                    .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                    .clickable(enabled = !isLocating) { requestOrResolveLocation() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isLocating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = NaranjaZesta,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.NearMe,
                        contentDescription = null,
                        tint = NaranjaZesta,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = if (isLocating) stringResource(R.string.inicio_sheet_localizando)
                    else stringResource(R.string.inicio_sheet_ubicacion_actual),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NaranjaZesta,
                    fontWeight = FontWeight.SemiBold
                )
            }

            addressError?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (addressSuccess) {
                Text(
                    text = stringResource(R.string.gestionar_cuenta_guardado_ok),
                    style = MaterialTheme.typography.bodySmall,
                    color = NaranjaZesta
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)
                        )
                    )
                    .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                    .clickable(enabled = !isSavingAddress) {
                        if (address.isBlank()) {
                            addressError = errorDireccion
                            return@clickable
                        }
                        isSavingAddress = true
                        authViewModel.updateProfile(
                            telefono = authState.currentUser?.telefono.orEmpty(),
                            direccion = address,
                            onSuccess = {
                                isSavingAddress = false
                                addressSuccess = true
                            },
                            onError = {
                                isSavingAddress = false
                                addressError = it
                            }
                        )
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSavingAddress) stringResource(R.string.carrito_guardando)
                    else stringResource(R.string.gestionar_cuenta_guardar_direccion),
                    color = BlancoZesta,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            HorizontalDivider(color = BordeCirculoZesta)

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryGradientButton(
                text = stringResource(R.string.perfil_cerrar_sesion),
                onClick = onLogoutClick
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.gestionar_cuenta_volver_perfil),
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta,
            modifier = Modifier.clickable { onBackClick() }
        )
    }
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            containerColor = FondoTarjetaRestauranteZesta,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = stringResource(R.string.gestionar_cuenta_foto_titulo),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipalZesta,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Opción cámara
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(FondoZesta)
                            .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable {
                                showImageSourceDialog = false
                                val granted = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (granted) cameraLauncher.launch(cameraImageUri)
                                else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = NaranjaZesta,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.gestionar_cuenta_foto_camara),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Opción galería
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(FondoZesta)
                            .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable {
                                showImageSourceDialog = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = null,
                            tint = NaranjaZesta,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.gestionar_cuenta_foto_galeria),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextoPrincipalZesta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text(
                        text = stringResource(R.string.inicio_sheet_cancelar),
                        color = TextoSecundarioZesta
                    )
                }
            }
        )
    }

}
