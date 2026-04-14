package com.zesta.app.ui.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.zesta.app.R
import com.zesta.app.navigation.AppRoutes
import com.zesta.app.ui.components.ZestaBottomNavBar
import com.zesta.app.ui.theme.*
import com.zesta.app.viewmodel.AuthViewModel
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun ProfileScreen(
    userName: String = "",
    isGuest: Boolean = false,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onHelpClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAccessibilityClick: () -> Unit,
    onManageAccountClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val profileImageUrl by authViewModel.profileImageUrl.collectAsState()
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val displayName = when {
        isGuest -> stringResource(R.string.perfil_nombre_invitado)
        userName.isBlank() -> stringResource(R.string.perfil_nombre_placeholder)
        else -> userName
    }

    val cameraImageUri = remember {
        val file = File(context.cacheDir, "images/profile_photo_${System.currentTimeMillis()}.jpg")
            .also { it.parentFile?.mkdirs() }
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val base64 = uriToBase64(context, it)
            if (base64 != null) authViewModel.setProfileImageBase64(base64)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val base64 = uriToBase64(context, cameraImageUri)
            if (base64 != null) authViewModel.setProfileImageBase64(base64)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) cameraLauncher.launch(cameraImageUri) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoZesta)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 118.dp)
        ) {
            item {
                ProfileHeader(
                    displayName = displayName,
                    isGuest = isGuest,
                    profileImageUrl = profileImageUrl,
                    onAvatarClick = { if (!isGuest) showImageSourceDialog = true },
                    onFavoritesClick = onFavoritesClick,
                    onOrderHistoryClick = onOrderHistoryClick,
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick
                )
            }
            item {
                ProfileOptionsSection(
                    onHelpClick = onHelpClick,
                    onPrivacyClick = onPrivacyClick,
                    onAccessibilityClick = onAccessibilityClick,
                    onManageAccountClick = onManageAccountClick,
                    onAboutClick = onAboutClick
                )
            }
        }

        ZestaBottomNavBar(
            selectedRoute = AppRoutes.Profile.route,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onCartClick = onCartClick,
            onProfileClick = { }
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

                    DialogOption(
                        icon = { Icon(Icons.Outlined.CameraAlt, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp)) },
                        text = stringResource(R.string.gestionar_cuenta_foto_camara),
                        onClick = {
                            showImageSourceDialog = false
                            val granted = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) cameraLauncher.launch(cameraImageUri)
                            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )

                    DialogOption(
                        icon = { Icon(Icons.Outlined.PhotoLibrary, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp)) },
                        text = stringResource(R.string.gestionar_cuenta_foto_galeria),
                        onClick = {
                            showImageSourceDialog = false
                            galleryLauncher.launch("image/*")
                        }
                    )

                    // Solo se muestra si hay foto
                    if (!profileImageUrl.isNullOrBlank()) {
                        DialogOption(
                            icon = { Icon(Icons.Outlined.Delete, null, tint = NaranjaZesta, modifier = Modifier.size(20.dp)) },
                            text = stringResource(R.string.gestionar_cuenta_foto_eliminar),
                            onClick = {
                                showImageSourceDialog = false
                                authViewModel.clearProfileImage()
                            }
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

@Composable
private fun DialogOption(
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(FondoZesta)
            .border(1.dp, BordeCirculoZesta, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        icon()
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProfileHeader(
    displayName: String,
    isGuest: Boolean,
    profileImageUrl: String?,
    onAvatarClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.perfil_titulo),
            style = MaterialTheme.typography.headlineMedium,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(28.dp))

        ProfileAvatar(
            profileImageUrl = profileImageUrl,
            isGuest = isGuest,
            onAvatarClick = onAvatarClick
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineMedium,
            color = TextoPrincipalZesta,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileQuickActionCard(
                text = stringResource(R.string.perfil_favoritos),
                onClick = onFavoritesClick
            )
            Spacer(modifier = Modifier.width(14.dp))
            ProfileQuickActionCard(
                text = stringResource(R.string.perfil_historial_pedidos),
                onClick = onOrderHistoryClick
            )
        }

        if (isGuest) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.perfil_mensaje_invitado),
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPrincipalZesta
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(R.string.inicio_sesion_entrar),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoPrincipalZesta,
                    modifier = Modifier.clickable { onLoginClick() }
                )
                Text(
                    text = stringResource(R.string.inicio_sesion_registrarse),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoPrincipalZesta,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun ProfileAvatar(
    profileImageUrl: String?,
    isGuest: Boolean,
    onAvatarClick: () -> Unit
) {
    val profileBitmap by produceState<Bitmap?>(initialValue = null, key1 = profileImageUrl) {
        value = try {
            if (profileImageUrl.isNullOrBlank()) null
            else {
                val clean = profileImageUrl.replace("\\s".toRegex(), "")
                val bytes = Base64.decode(clean, Base64.NO_WRAP)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: Exception) { null }
    }

    Box(
        modifier = Modifier
            .size(122.dp)
            .clip(CircleShape)
            .background(Color.White)
            .then(if (!isGuest) Modifier.clickable { onAvatarClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        val bmp = profileBitmap
        if (bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                modifier = Modifier.size(122.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(R.drawable.logo_zesta),
                contentDescription = stringResource(R.string.accesibilidad_avatar_perfil),
                modifier = Modifier.size(122.dp).padding(14.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
@Composable
private fun ProfileQuickActionCard(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(NaranjaZesta)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = TextoOpcionZesta,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun ProfileOptionsSection(
    onHelpClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAccessibilityClick: () -> Unit,
    onManageAccountClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NaranjaZesta)
            .padding(horizontal = 28.dp, vertical = 22.dp)
    ) {
        ProfileOptionItem(stringResource(R.string.perfil_ayuda), onHelpClick)
        Spacer(modifier = Modifier.height(10.dp))
        ProfileOptionItem(stringResource(R.string.perfil_privacidad), onPrivacyClick)
        Spacer(modifier = Modifier.height(10.dp))
        ProfileOptionItem(stringResource(R.string.perfil_accesibilidad), onAccessibilityClick)
        Spacer(modifier = Modifier.height(10.dp))
        ProfileOptionItem(stringResource(R.string.perfil_gestionar_cuenta), onManageAccountClick)
        Spacer(modifier = Modifier.height(10.dp))
        ProfileOptionItem(stringResource(R.string.perfil_acerca_de), onAboutClick)
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun ProfileOptionItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = TextoOpcionZesta,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.clickable { onClick() }
    )
}