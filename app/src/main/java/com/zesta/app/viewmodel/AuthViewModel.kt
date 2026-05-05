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

/**
 * Estado de la UI de autenticación.
 * Es inmutable (data class) para garantizar recomposiciones predecibles en Compose.
 *
 * @param fullName        Nombre completo introducido en el formulario de registro.
 * @param email           Email del formulario de login/registro.
 * @param password        Contraseña del formulario (nunca se persiste).
 * @param phone           Teléfono del formulario.
 * @param address         Dirección del formulario.
 * @param isLoggedIn      True si hay sesión activa de usuario registrado.
 * @param isGuest         True si el usuario está navegando como invitado.
 * @param currentUser     Datos completos del usuario autenticado, o null si no hay sesión.
 * @param userName        Nombre del usuario para mostrar en la UI (atajo de currentUser.nombre).
 * @param errorMessage    Mensaje de error a mostrar en pantalla, null si no hay error.
 * @param isLoading       True mientras hay una operación de red en curso.
 * @param isSessionChecked True cuando ya se ha comprobado si existe sesión guardada.
 *                         Evita mostrar la pantalla de login antes de tiempo (flash).
 * @param favoritos       Lista de IDs de restaurantes marcados como favoritos.
 */
data class AuthUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val address: String = "",
    val isLoggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val currentUser: User? = null,
    val userName: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSessionChecked: Boolean = false,
    val favoritos: List<Int> = emptyList()
)

/**
 * ViewModel central de autenticación y perfil de usuario.
 *
 * Gestiona:
 * - Registro, login (email/contraseña y Google) y cierre de sesión.
 * - Restauración de sesión al arrancar la app.
 * - Perfil del usuario: teléfono, dirección, foto.
 * - Favoritos de restaurantes.
 * - Deshabilitación de cuenta (soft delete: no borra datos, solo marca isDisabled = true).
 *
 * @param authRepository         Repositorio de autenticación (Firebase Auth + Firestore).
 * @param preferencesRepository  Repositorio de preferencias locales (modo invitado).
 * @param storageRepository      Repositorio de almacenamiento de fotos de perfil.
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val storageRepository: StorageRepository = StorageRepository()
) : ViewModel() {

    companion object {
        /**
         * Factory de conveniencia para crear el ViewModel desde un contexto
         * donde no se dispone de [AuthViewModelFactory].
         */
        fun factory(
            authRepository: AuthRepository,
            preferencesRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(authRepository, preferencesRepository) as T
            }
        }
    }

    // Imagen de perfil del usuario almacenada en Firestore como cadena Base64
    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl.asStateFlow()

    // Estado principal de la UI; toda la pantalla reacciona a sus cambios
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeGuestMode()
        restoreSessionIfNeeded()
        // Log de depuración: registra cada cambio en la foto de perfil
        viewModelScope.launch {
            profileImageUrl.collect { value ->
                android.util.Log.d(
                    "ZESTA_PHOTO",
                    "profileImageUrl cambió: ${value?.take(20) ?: "NULL"}"
                )
            }
        }
    }

    /**
     * Carga los datos actualizados del usuario desde Firestore y actualiza el estado.
     * Solo sobreescribe la foto si aún no hay ninguna cargada en memoria,
     * evitando parpadeos al recargar el perfil.
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            if (result.isSuccess) {
                val user = result.getOrNull()
                // No sobreescribir la foto si ya está en memoria
                if (_profileImageUrl.value.isNullOrBlank()) {
                    _profileImageUrl.value = user?.profilePhotoUrl
                }
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

    /**
     * Observa el estado del modo invitado en DataStore.
     * Actualiza [AuthUiState.isGuest] cada vez que cambia.
     */
    private fun observeGuestMode() {
        viewModelScope.launch {
            preferencesRepository.isGuestFlow.collect { isGuest ->
                _uiState.value = _uiState.value.copy(isGuest = isGuest)
            }
        }
    }

    /**
     * Restaura la sesión del usuario si Firebase Auth tiene un token activo.
     * Se ejecuta al crear el ViewModel (en el init).
     *
     * Cuando termina (con éxito o no), pone [AuthUiState.isSessionChecked] = true
     * para que la UI deje de mostrar la pantalla de carga inicial.
     */
    private fun restoreSessionIfNeeded() {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(isSessionChecked = true)
            return
        }
        viewModelScope.launch {
            // Primero comprobar si la cuenta sigue activa
            val activeCheck = authRepository.checkAccountStillActive()
            if (activeCheck.isFailure) {
                // Cuenta deshabilitada o token inválido: mandar al login
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = false,
                    currentUser = null,
                    userName = "",
                    isSessionChecked = true  // <-- solo aquí, después del check
                )
                return@launch
            }

            // Solo si la cuenta está activa, cargar el perfil
            val result = authRepository.getCurrentUser()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (_profileImageUrl.value.isNullOrBlank()) {
                    _profileImageUrl.value = user?.profilePhotoUrl
                }
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    isGuest = false,
                    currentUser = user,
                    userName = user?.nombre.orEmpty(),
                    phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(),
                    errorMessage = null,
                    favoritos = user?.favoritos ?: emptyList(),
                    isSessionChecked = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = false,
                    currentUser = null,
                    userName = "",
                    isSessionChecked = true
                )
            }
        }
    }

    /**
     * Sube una foto de perfil a partir de una [Uri] local (galería o cámara).
     * Delega la subida al [StorageRepository] y actualiza el StateFlow de la imagen.
     */
    fun setProfileImageUri(context: Context, uri: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val result = storageRepository.uploadProfilePhoto(context, uid, uri)
            if (result.isSuccess) {
                _profileImageUrl.value = result.getOrNull()
            } else {
                android.util.Log.e(
                    "ZESTA_PHOTO",
                    "Error subiendo foto: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Sube una foto de perfil ya convertida a Base64.
     * Se usa cuando la imagen viene de la cámara o galería tras pasar por [uriToBase64].
     */
    fun setProfileImageBase64(base64: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val result = storageRepository.uploadProfilePhotoFromBase64(uid, base64)
            if (result.isSuccess) {
                _profileImageUrl.value = result.getOrNull()
            } else {
                android.util.Log.e("ZESTA_PHOTO", "Error: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    /**
     * Elimina la foto de perfil del usuario tanto en Firestore como en el estado local.
     */
    fun clearProfileImage() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            storageRepository.deleteProfilePhoto(uid)
            _profileImageUrl.value = null
        }
    }

    // Actualizaciones de campos del formulario
    // Cada función limpia el error al empezar a escribir para no confundir al usuario

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, errorMessage = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value, errorMessage = null)
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(address = value, errorMessage = null)
    }

    // Gestión de direcciones

    /** Añade una nueva dirección a la lista de direcciones del usuario en Firestore. */
    fun addDireccion(direccion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.addDireccion(direccion)
            if (result.isSuccess) {
                loadCurrentUser(); onSuccess()
            } else onError(result.exceptionOrNull()?.message ?: "Error al guardar")
        }
    }

    /** Cambia la dirección de entrega activa (la que se usa por defecto al pedir). */
    fun setDireccionActiva(
        direccion: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = authRepository.setDireccionActiva(direccion)
            if (result.isSuccess) {
                loadCurrentUser(); onSuccess()
            } else onError(result.exceptionOrNull()?.message ?: "Error")
        }
    }

    /** Elimina una dirección concreta de la lista de direcciones del usuario. */
    fun deleteDireccion(direccion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.deleteDireccion(direccion)
            if (result.isSuccess) {
                loadCurrentUser(); onSuccess()
            } else onError(result.exceptionOrNull()?.message ?: "Error al eliminar")
        }
    }

    // Favoritos

    /**
     * Alterna el estado favorito de un restaurante.
     * Si ya estaba en favoritos lo quita; si no estaba lo añade.
     * Actualiza el estado optimistamente con el resultado del repositorio.
     */
    fun toggleFavorito(restaurantId: Int) {
        viewModelScope.launch {
            val result = authRepository.toggleFavorito(restaurantId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    favoritos = result.getOrDefault(emptyList()),
                    currentUser = _uiState.value.currentUser?.copy(
                        favoritos = result.getOrDefault(emptyList())
                    )
                )
            }
        }
    }

    // Autenticación

    /**
     * Registra un nuevo usuario con email y contraseña.
     * Valida que los campos obligatorios no estén vacíos antes de llamar al repositorio.
     * Al tener éxito limpia el modo invitado y establece la sesión.
     */
    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.fullName.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Completa los campos obligatorios")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.register(
                nombre = state.fullName.trim(),
                email = state.email.trim(),
                password = state.password,
                telefono = state.phone.trim(),
                direccion = state.address.trim()
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
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo registrar el usuario"
                )
            }
        }
    }

    /**
     * Inicia sesión con email y contraseña.
     * Valida campos localmente antes de llamar a Firebase Auth.
     * Al tener éxito limpia el modo invitado y carga el perfil del usuario.
     */
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

    /**
     * Inicia sesión con Google usando el token de ID obtenido por el cliente de Google Sign-In.
     * Al tener éxito limpia el modo invitado y establece la sesión igual que el login normal.
     */
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

    /**
     * Actualiza el teléfono y la dirección activa del usuario en Firestore.
     * Si la dirección es nueva, la añade al historial de direcciones (máximo 3).
     */
    fun updateProfile(
        telefono: String,
        direccion: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = authRepository.updateProfile(
                telefono = telefono.trim(),
                direccion = direccion.trim()
            )
            if (result.isSuccess) {
                val listaActual = _uiState.value.currentUser?.direcciones ?: emptyList()
                val nuevaLista = if (direccion.isNotBlank() && !listaActual.contains(direccion)) {
                    (listaActual + direccion).takeLast(3)
                } else listaActual
                _uiState.value = _uiState.value.copy(
                    phone = telefono.trim(),
                    address = direccion.trim(),
                    currentUser = _uiState.value.currentUser?.copy(
                        telefono = telefono.trim(),
                        direccion = direccion.trim(),
                        direcciones = nuevaLista
                    )
                )
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al guardar")
            }
        }
    }

    /**
     * Borra un campo concreto del perfil del usuario ("telefono" o "direccion").
     * Si se borra la dirección, también la elimina del historial de direcciones.
     */
    fun clearProfileField(field: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.clearProfileField(field)
            if (result.isSuccess) {
                val listaActual = _uiState.value.currentUser?.direcciones ?: emptyList()
                val direccionBorrada = _uiState.value.currentUser?.direccion.orEmpty()
                val nuevaLista =
                    if (field == "direccion") listaActual.filter { it != direccionBorrada }
                    else listaActual
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
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al eliminar")
            }
        }
    }

    /**
     * Comprueba si el usuario tiene el perfil mínimo completo para realizar pedidos.
     * Se considera completo si tiene teléfono y dirección de entrega.
     */
    fun hasCompleteProfile(): Boolean {
        val user = _uiState.value.currentUser
        return !user?.telefono.isNullOrBlank() && !user?.direccion.isNullOrBlank()
    }

    /** Establece el modo invitado y navega al flujo principal sin iniciar sesión. */
    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.continueAsGuest()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = false, isGuest = true,
                currentUser = null, userName = "", errorMessage = null
            )
            onSuccess()
        }
    }

    /** Cierra la sesión del usuario y resetea todo el estado de la UI. */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            preferencesRepository.clearGuestMode()
            _profileImageUrl.value = null
            _uiState.value = AuthUiState()
        }
    }

    // Deshabilitación de cuenta (soft delete)

    /**
     * Guarda la valoración del usuario en la colección "app_feedback" de Firestore
     * y a continuación deshabilita la cuenta.
     *
     * Si guardar la valoración falla, se registra el error pero se continúa
     * con la deshabilitación igualmente, para no bloquear al usuario.
     *
     * @param rating   Puntuación enviada por el usuario (1-5 estrellas como String).
     * @param onSuccess Callback ejecutado tras deshabilitar correctamente.
     * @param onError   Callback con mensaje de error si falla la deshabilitación.
     */
    fun sendRatingAndDisableAccount(
        rating: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (rating.isNotBlank() && uid != null) {
                try {
                    FirebaseFirestore.getInstance()
                        .collection("app_feedback")
                        .add(
                            mapOf(
                                "uid" to uid,
                                "valoracion" to rating.trim(),
                                "creadoEn" to FieldValue.serverTimestamp()
                            )
                        )
                        .await()
                } catch (e: Exception) {
                    // Error no crítico: la valoración es opcional, la deshabilitación sigue adelante
                    android.util.Log.w(
                        "ZESTA_DISABLE",
                        "No se pudo guardar valoración: ${e.message}"
                    )
                }
            }
            disableAccount(onSuccess = onSuccess, onError = onError)
        }
    }

    /**
     * Deshabilita la cuenta del usuario de forma segura (soft delete).
     *
     * En lugar de borrar el documento de Firestore (lo que destruiría el historial
     * de pedidos y facturas asociadas), marca el campo [isDisabled] = true y
     * registra la fecha en [disabledAt]. El usuario no podrá volver a iniciar sesión
     * porque el repositorio comprueba este campo durante el login.
     *
     * Tras marcar la cuenta, cierra sesión localmente y resetea el estado.
     *
     * @param onSuccess Callback ejecutado tras deshabilitar correctamente.
     * @param onError   Callback con mensaje de error legible para mostrar en la UI.
     */
    fun disableAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onError("Usuario no autenticado")
            return
        }

        val uid = user.uid

        viewModelScope.launch {
            try {
                // Marcar cuenta como deshabilitada en Firestore (no se borra el documento)
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update(
                        mapOf(
                            "isDisabled" to true,
                            "disabledAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                // Cerrar sesión de Firebase Auth (el documento queda intacto)
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
    fun reactivateAccount(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.reactivateAccount(email, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Cuenta reactivada. Ya puedes iniciar sesión."
                )
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

    /**
     * Factory secundaria para crear el ViewModel desde un [ViewModelProvider].
     * Se usa en los puntos donde no está disponible el [factory].
     */
    class AuthViewModelFactory(
        private val authRepository: AuthRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(authRepository, preferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}