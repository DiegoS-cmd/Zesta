package com.zesta.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.zesta.app.data.model.User
import com.zesta.app.data.repository.AuthRepository
import com.zesta.app.data.repository.StorageRepository
import com.zesta.app.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Estado inmutable de la UI de autenticación.
data class AuthUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",       // nunca se persiste
    val phone: String = "",
    val address: String = "",
    val isLoggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val currentUser: User? = null,
    val userName: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSessionChecked: Boolean = false, // evita flash de login al arrancar
    val favoritos: List<Int> = emptyList()
)

///ViewModel central de autenticación, perfil, favoritos y gestión de cuenta.
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val storageRepository: StorageRepository = StorageRepository()
) : ViewModel() {

    companion object {
        // Factory que se usa cuando no se dispone de AuthViewModelFactory por que no es composable
        fun factory(
            authRepository: AuthRepository,
            preferencesRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AuthViewModel(authRepository, preferencesRepository) as T
        }
    }

    // Foto de perfil en Base64; separada del uiState para evitar recomposiciones innecesarias
    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl.asStateFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeGuestMode()
        restoreSessionIfNeeded()
        viewModelScope.launch {
            profileImageUrl.collect { value ->
                android.util.Log.d("ZESTA_PHOTO", "profileImageUrl cambió: ${value?.take(20) ?: "NULL"}")
            }
        }
    }

    // Recarga el perfil desde Firestore sin sobreescribir la foto si ya está en memoria
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (_profileImageUrl.value.isNullOrBlank()) _profileImageUrl.value = user?.profilePhotoUrl
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    userName = user?.nombre.orEmpty(),
                    phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(),
                    favoritos = user?.favoritos ?: emptyList()
                )
            }
        }
    }

    // Escucha cambios en el modo invitado guardado en DataStore
    private fun observeGuestMode() {
        viewModelScope.launch {
            preferencesRepository.isGuestFlow.collect { isGuest ->
                _uiState.value = _uiState.value.copy(isGuest = isGuest)
            }
        }
    }

    // Restaura la sesión al arrancar; marca isSessionChecked cuando termina
    private fun restoreSessionIfNeeded() {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(isSessionChecked = true)
            return
        }
        viewModelScope.launch {
            val activeCheck = authRepository.checkAccountStillActive()
            if (activeCheck.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = false, currentUser = null, userName = "", isSessionChecked = true
                )
                return@launch
            }
            val result = authRepository.getCurrentUser()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (_profileImageUrl.value.isNullOrBlank()) _profileImageUrl.value = user?.profilePhotoUrl
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true, isGuest = false, currentUser = user,
                    userName = user?.nombre.orEmpty(), phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(), errorMessage = null,
                    favoritos = user?.favoritos ?: emptyList(), isSessionChecked = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = false, currentUser = null, userName = "", isSessionChecked = true
                )
            }
        }
    }

    // Foto de perfil

    // Sube foto desde Uri local (galería o cámara).
    fun setProfileImageUri(context: Context, uri: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val result = storageRepository.uploadProfilePhoto(context, uid, uri)
            if (result.isSuccess) _profileImageUrl.value = result.getOrNull()
            else android.util.Log.e("ZESTA_PHOTO", "Error subiendo foto: ${result.exceptionOrNull()?.message}")
        }
    }

    // Sube foto ya convertida a Base64 (p.ej. desde cámara).
    fun setProfileImageBase64(base64: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val result = storageRepository.uploadProfilePhotoFromBase64(uid, base64)
            if (result.isSuccess) _profileImageUrl.value = result.getOrNull()
            else android.util.Log.e("ZESTA_PHOTO", "Error: ${result.exceptionOrNull()?.message}")
        }
    }

    // Elimina la foto de perfil de Firestore y limpia el estado local.
    fun clearProfileImage() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            storageRepository.deleteProfilePhoto(uid)
            _profileImageUrl.value = null
        }
    }

    // Campos del formulario — limpian el error al escribir

    fun onFullNameChange(value: String) { _uiState.value = _uiState.value.copy(fullName = value, errorMessage = null) }
    fun onEmailChange(value: String)    { _uiState.value = _uiState.value.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) { _uiState.value = _uiState.value.copy(password = value, errorMessage = null) }
    fun onPhoneChange(value: String)    { _uiState.value = _uiState.value.copy(phone = value, errorMessage = null) }
    fun onAddressChange(value: String)  { _uiState.value = _uiState.value.copy(address = value, errorMessage = null) }

    // Direcciones

    fun addDireccion(direccion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.addDireccion(direccion)
            if (result.isSuccess) { loadCurrentUser(); onSuccess() }
            else onError(result.exceptionOrNull()?.message ?: "Error al guardar")
        }
    }

    fun setDireccionActiva(direccion: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val result = authRepository.setDireccionActiva(direccion)
            if (result.isSuccess) { loadCurrentUser(); onSuccess() }
            else onError(result.exceptionOrNull()?.message ?: "Error")
        }
    }

    fun deleteDireccion(direccion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.deleteDireccion(direccion)
            if (result.isSuccess) { loadCurrentUser(); onSuccess() }
            else onError(result.exceptionOrNull()?.message ?: "Error al eliminar")
        }
    }


    /// Añade o quita el restaurante de favoritos y actualiza el estado
    fun toggleFavorito(restaurantId: Int) {
        viewModelScope.launch {
            val result = authRepository.toggleFavorito(restaurantId)
            if (result.isSuccess) {
                val nuevaLista = result.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    favoritos = nuevaLista,
                    currentUser = _uiState.value.currentUser?.copy(favoritos = nuevaLista)
                )
            }
        }
    }

    // Autenticación

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.fullName.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Completa los campos obligatorios")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.register(
                nombre = state.fullName.trim(), email = state.email.trim(),
                password = state.password, telefono = state.phone.trim(), direccion = state.address.trim()
            )
            if (result.isSuccess) {
                val user = result.getOrNull()
                preferencesRepository.clearGuestMode()
                _profileImageUrl.value = user?.profilePhotoUrl
                _uiState.value = AuthUiState(
                    isLoggedIn = true, isGuest = false, currentUser = user,
                    userName = user?.nombre.orEmpty(), phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(), isLoading = false
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "No se pudo registrar el usuario"
                )
            }
        }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Introduce email y contraseña")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.login(email = state.email.trim(), password = state.password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                preferencesRepository.clearGuestMode()
                _profileImageUrl.value = user?.profilePhotoUrl
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true, isGuest = false, currentUser = user,
                    userName = user?.nombre.orEmpty(), phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(), password = "",
                    errorMessage = null, isLoading = false
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, isLoggedIn = false, currentUser = null, userName = "",
                    errorMessage = result.exceptionOrNull()?.message ?: "Credenciales incorrectas"
                )
            }
        }
    }

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.loginWithGoogle(idToken)
            if (result.isSuccess) {
                val user = result.getOrNull()
                preferencesRepository.clearGuestMode()
                _profileImageUrl.value = user?.profilePhotoUrl
                _uiState.value = AuthUiState(
                    isLoggedIn = true, isGuest = false, currentUser = user,
                    userName = user?.nombre.orEmpty(), phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(), isLoading = false
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error con Google"
                )
                onError(_uiState.value.errorMessage ?: "Error")
            }
        }
    }


    // Actualiza teléfono y dirección activa; añade la dirección al historial si es nueva (máx. 3).
    fun updateProfile(telefono: String, direccion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.updateProfile(telefono = telefono.trim(), direccion = direccion.trim())
            if (result.isSuccess) {
                val listaActual = _uiState.value.currentUser?.direcciones ?: emptyList()
                val nuevaLista = if (direccion.isNotBlank() && !listaActual.contains(direccion))
                    (listaActual + direccion).takeLast(3) else listaActual
                _uiState.value = _uiState.value.copy(
                    phone = telefono.trim(), address = direccion.trim(),
                    currentUser = _uiState.value.currentUser?.copy(
                        telefono = telefono.trim(), direccion = direccion.trim(), direcciones = nuevaLista
                    )
                )
                onSuccess()
            } else onError(result.exceptionOrNull()?.message ?: "Error al guardar")
        }
    }

    // Borra "telefono" o "direccion"; si es dirección también la elimina del historial.
    fun clearProfileField(field: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.clearProfileField(field)
            if (result.isSuccess) {
                val listaActual = _uiState.value.currentUser?.direcciones ?: emptyList()
                val direccionBorrada = _uiState.value.currentUser?.direccion.orEmpty()
                val nuevaLista = if (field == "direccion") listaActual.filter { it != direccionBorrada } else listaActual
                _uiState.value = _uiState.value.copy(
                    phone = if (field == "telefono") "" else _uiState.value.phone,
                    address = if (field == "direccion") "" else _uiState.value.address,
                    currentUser = _uiState.value.currentUser?.copy(
                        telefono = if (field == "telefono") "" else _uiState.value.currentUser?.telefono.orEmpty(),
                        direccion = if (field == "direccion") "" else _uiState.value.currentUser?.direccion.orEmpty(),
                        direcciones = nuevaLista
                    )
                )
                onSuccess()
            } else onError(result.exceptionOrNull()?.message ?: "Error al eliminar")
        }
    }

    // Devuelve true si el usuario tiene teléfono y dirección para poder realizar pedidos.
    fun hasCompleteProfile(): Boolean {
        val user = _uiState.value.currentUser
        return !user?.telefono.isNullOrBlank() && !user?.direccion.isNullOrBlank()
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.continueAsGuest()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = false, isGuest = true, currentUser = null, userName = "", errorMessage = null
            )
            onSuccess()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            preferencesRepository.clearGuestMode()
            _profileImageUrl.value = null
            _uiState.value = AuthUiState()
        }
    }


    /** Guarda valoración en app_feedback y deshabilita la cuenta. Si la valoración falla, continúa igualmente. */
    fun sendRatingAndDisableAccount(rating: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (rating.isNotBlank() && uid != null) {
                try {
                    FirebaseFirestore.getInstance().collection("app_feedback")
                        .add(mapOf("uid" to uid, "valoracion" to rating.trim(), "creadoEn" to FieldValue.serverTimestamp()))
                        .await()
                } catch (e: Exception) {
                    android.util.Log.w("ZESTA_DISABLE", "No se pudo guardar valoración: ${e.message}")
                }
            }
            disableAccount(onSuccess = onSuccess, onError = onError)
        }
    }

    /**
     * marca isDisabled=true en Firestore (no borra el documento)
     * para preservar historial de pedidos, y cierra sesión localmente.
     */
    fun disableAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) { onError("Usuario no autenticado"); return }
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance().collection("users").document(user.uid)
                    .update(mapOf("isDisabled" to true, "disabledAt" to FieldValue.serverTimestamp()))
                    .await()
                authRepository.logout()
                preferencesRepository.clearGuestMode()
                _profileImageUrl.value = null
                _uiState.value = AuthUiState()
                onSuccess()
            } catch (e: Exception) {
                val mensaje = when {
                    e.message?.contains("recent", ignoreCase = true) == true ->
                        "Por seguridad, cierra sesión, vuelve a iniciar sesión y repite la operación."
                    else -> e.localizedMessage ?: "No se pudo deshabilitar la cuenta."
                }
                onError(mensaje)
            }
        }
    }

    fun reactivateAccount(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.reactivateAccount(email, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Cuenta reactivada. Ya puedes iniciar sesión.")
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "No se pudo reactivar la cuenta"
                )
                onError(_uiState.value.errorMessage ?: "")
            }
        }
    }

    class AuthViewModelFactory(
        private val authRepository: AuthRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java))
                return AuthViewModel(authRepository, preferencesRepository) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}