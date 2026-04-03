package com.zesta.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zesta.app.data.model.User
import com.zesta.app.data.repository.AuthRepository
import com.zesta.app.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeGuestMode()
        restoreSessionIfNeeded()
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
                    errorMessage = null,
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

            val result = authRepository.login(
                email = state.email.trim(),
                password = state.password
            )

            if (result.isSuccess) {
                val user = result.getOrNull()

                preferencesRepository.clearGuestMode()

                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    isGuest = false,
                    currentUser = user,
                    userName = user?.nombre.orEmpty(),
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
