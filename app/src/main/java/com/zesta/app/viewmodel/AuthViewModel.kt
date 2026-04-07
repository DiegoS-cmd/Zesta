package com.zesta.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zesta.app.data.model.User
import com.zesta.app.data.repository.AuthRepository
import com.zesta.app.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
    val isSessionChecked: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    companion object {
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

    // ── Foto de perfil ─────────────────────────────────────────────────────

    val profileImageUri: StateFlow<Uri?> = preferencesRepository.profileImageUri
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setProfileImageUri(uri: Uri?) {
        viewModelScope.launch {
            if (uri != null) preferencesRepository.saveProfileImageUri(uri)
            else preferencesRepository.clearProfileImageUri()
        }
    }

    // ── Estado UI ──────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeGuestMode()
        restoreSessionIfNeeded()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            if (result.isSuccess) {
                val user = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    userName = user?.nombre.orEmpty(),
                    phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty()
                )
            }
        }
    }

    private fun observeGuestMode() {
        viewModelScope.launch {
            preferencesRepository.isGuestFlow.collect { isGuest ->
                _uiState.value = _uiState.value.copy(isGuest = isGuest)
            }
        }
    }

    private fun restoreSessionIfNeeded() {
        if (!authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(isSessionChecked = true)
            return
        }

        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            _uiState.value = if (result.isSuccess) {
                val user = result.getOrNull()
                _uiState.value.copy(
                    isLoggedIn = true,
                    isGuest = false,
                    currentUser = user,
                    userName = user?.nombre.orEmpty(),
                    phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(),
                    errorMessage = null,
                    isSessionChecked = true
                )
            } else {
                _uiState.value.copy(
                    isLoggedIn = false,
                    currentUser = null,
                    userName = "",
                    errorMessage = null,
                    isSessionChecked = true
                )
            }
        }
    }

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
                _uiState.value = AuthUiState(
                    isLoggedIn = true,
                    isGuest = false,
                    currentUser = user,
                    userName = user?.nombre.orEmpty(),
                    phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(),
                    isLoading = false
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
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    isGuest = false,
                    currentUser = user,
                    userName = user?.nombre.orEmpty(),
                    phone = user?.telefono.orEmpty(),
                    address = user?.direccion.orEmpty(),
                    password = "",
                    errorMessage = null,
                    isLoading = false
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    currentUser = null,
                    userName = "",
                    errorMessage = result.exceptionOrNull()?.message ?: "Credenciales incorrectas"
                )
            }
        }
    }

    fun updateProfile(telefono: String, direccion: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.updateProfile(telefono = telefono.trim(), direccion = direccion.trim())
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

    fun clearProfileField(field: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.clearProfileField(field)
            if (result.isSuccess) {
                val listaActual = _uiState.value.currentUser?.direcciones ?: emptyList()
                val direccionBorrada = _uiState.value.currentUser?.direccion.orEmpty()
                val nuevaLista = if (field == "direccion") listaActual.filter { it != direccionBorrada }
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

    fun hasCompleteProfile(): Boolean {
        val user = _uiState.value.currentUser
        return !user?.telefono.isNullOrBlank() && !user?.direccion.isNullOrBlank()
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            preferencesRepository.continueAsGuest()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = false,
                isGuest = true,
                currentUser = null,
                userName = "",
                errorMessage = null
            )
            onSuccess()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            preferencesRepository.clearGuestMode()
            _uiState.value = AuthUiState()
        }
    }
}

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
