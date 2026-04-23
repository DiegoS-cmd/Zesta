package com.zesta.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.zesta.app.R
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressBottomSheet(
    currentUser: com.zesta.app.data.model.User?,
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddField by remember { mutableStateOf(false) }
    var newAddress by remember { mutableStateOf("") }
    var editingAddress by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var isLocating by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val errorDireccion = stringResource(R.string.carrito_error_direccion)
    val errorMaximo = stringResource(R.string.inicio_sheet_maximo)

    val direcciones = currentUser?.direcciones ?: emptyList()
    val activa = currentUser?.direccion ?: ""
    val context = LocalContext.current

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
                                newAddress = addresses.firstOrNull()?.getAddressLine(0) ?: ""
                                isLocating = false
                                showAddField = true
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            newAddress = addresses?.firstOrNull()?.getAddressLine(0) ?: ""
                            isLocating = false
                            showAddField = true
                        }
                    } else isLocating = false
                }.addOnFailureListener { isLocating = false }
        } catch (e: SecurityException) { isLocating = false }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FondoZesta,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.inicio_sheet_titulo),
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipalZesta,
                fontWeight = FontWeight.SemiBold
            )

            if (direcciones.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.inicio_sheet_guardadas),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundarioZesta,
                    fontWeight = FontWeight.SemiBold
                )
                direcciones.forEach { dir ->
                    val isActiva = dir == activa
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isActiva) FondoSeleccionNaranjaZesta else FondoTarjetaRestauranteZesta)
                            .border(
                                width = if (isActiva) 1.5.dp else 1.dp,
                                color = if (isActiva) NaranjaZesta else BordeCirculoZesta,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(enabled = !isActiva) {
                                authViewModel.setDireccionActiva(dir)
                                onDismiss()
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, tint = if (isActiva) NaranjaZesta else TextoSecundarioZesta, modifier = Modifier.size(20.dp))
                        Text(
                            text = dir,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isActiva) NaranjaZesta else TextoPrincipalZesta,
                            fontWeight = if (isActiva) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { newAddress = dir; editingAddress = dir; showAddField = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Outlined.Edit, null, tint = TextoSecundarioZesta, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { authViewModel.deleteDireccion(dir, onSuccess = {}, onError = {}) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Outlined.Delete, stringResource(R.string.gestionar_cuenta_eliminar), tint = TextoSecundarioZesta, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            if (direcciones.size < 3 || showAddField) {
                if (!showAddField) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(FondoTarjetaRestauranteZesta)
                            .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isLocating) { requestOrResolveLocation() }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isLocating) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NaranjaZesta, strokeWidth = 2.dp)
                        else Icon(Icons.Outlined.NearMe, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (isLocating) stringResource(R.string.inicio_sheet_localizando) else stringResource(R.string.inicio_sheet_ubicacion_actual),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NaranjaZesta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(FondoTarjetaRestauranteZesta)
                            .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
                            .clickable { showAddField = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Outlined.Add, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp))
                        Text(stringResource(R.string.inicio_sheet_anadir), style = MaterialTheme.typography.bodyLarge, color = NaranjaZesta, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    OutlinedTextField(
                        value = newAddress,
                        onValueChange = { newAddress = it; errorMsg = null },
                        label = { Text(stringResource(R.string.carrito_campo_direccion), color = TextoSecundarioZesta) },
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = if (newAddress.isNotBlank()) NaranjaZesta else TextoSecundarioZesta) },
                        trailingIcon = { if (newAddress.isNotBlank()) IconButton(onClick = { newAddress = "" }) { Icon(Icons.Outlined.Close, null, tint = TextoSecundarioZesta) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FondoTarjetaRestauranteZesta, unfocusedContainerColor = FondoTarjetaRestauranteZesta,
                            focusedBorderColor = NaranjaZesta, unfocusedBorderColor = BordeCirculoZesta,
                            cursorColor = NegroZesta, focusedTextColor = TextoPrincipalZesta, unfocusedTextColor = TextoPrincipalZesta
                        )
                    )
                    errorMsg?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(28.dp))
                                .background(FondoTarjetaRestauranteZesta)
                                .border(1.dp, BordeCirculoZesta, RoundedCornerShape(28.dp))
                                .clickable { showAddField = false; newAddress = ""; errorMsg = null; editingAddress = null }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.inicio_sheet_cancelar), color = TextoPrincipalZesta, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(28.dp))
                                .background(brush = Brush.horizontalGradient(listOf(AzulInicioGradienteZesta, AzulFinGradienteZesta)))
                                .border(2.dp, BordeBotonZesta, RoundedCornerShape(28.dp))
                                .clickable(enabled = !isSaving) {
                                    if (newAddress.isBlank()) { errorMsg = errorDireccion; return@clickable }
                                    isSaving = true
                                    if (editingAddress != null) {
                                        authViewModel.deleteDireccion(editingAddress!!, onSuccess = {
                                            authViewModel.addDireccion(newAddress, onSuccess = { isSaving = false; showAddField = false; newAddress = ""; editingAddress = null }, onError = { isSaving = false; errorMsg = it })
                                        }, onError = { isSaving = false; errorMsg = it })
                                    } else {
                                        if (direcciones.size >= 3) { errorMsg = errorMaximo; isSaving = false; return@clickable }
                                        authViewModel.addDireccion(newAddress, onSuccess = { isSaving = false; showAddField = false; newAddress = "" }, onError = { isSaving = false; errorMsg = it })
                                    }
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isSaving) stringResource(R.string.carrito_guardando) else stringResource(R.string.inicio_sheet_guardar),
                                color = BlancoZesta, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else {
                Text(stringResource(R.string.inicio_sheet_maximo), style = MaterialTheme.typography.bodySmall, color = TextoSecundarioZesta)
            }
        }
    }
}