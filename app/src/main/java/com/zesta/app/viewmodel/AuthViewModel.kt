package com.zesta.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zesta.app.data.model.User
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
    val userName: String = "",
    val errorMessage: String? = null
)

class AuthViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeSession()
        observeGuest()
        observeUser()
    }

    private fun observeSession() {
        viewModelScope.launch {
            repository.isLoggedInFlow.collect { loggedIn ->
                _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn)
            }
        }
    }

    private fun observeGuest() {
        viewModelScope.launch {
            repository.isGuestFlow.collect { guest ->
                _uiState.value = _uiState.value.copy(isGuest = guest)
            }
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            repository.userFlow.collect { user ->
                _uiState.value = _uiState.value.copy(
                    userName = user?.fullName.orEmpty()
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

        if (
            state.fullName.isBlank() ||
            state.email.isBlank() ||
            state.password.isBlank()
        ) {
            _uiState.value = state.copy(errorMessage = "Completa los campos obligatorios")
            return
        }

        viewModelScope.launch {
            repository.registerUser(
                User(
                    fullName = state.fullName.trim(),
                    email = state.email.trim(),
                    password = state.password,
                    phone = state.phone.trim(),
                    address = state.address.trim()
                )
            )
            clearForm()
            onSuccess()
        }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Introduce email y contraseña")
            return
        }

        viewModelScope.launch {
            val success = repository.login(
                email = state.email.trim(),
                password = state.password
            )

            if (success) {
                _uiState.value = _uiState.value.copy(errorMessage = null)
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Credenciales incorrectas"
                )
            }
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.continueAsGuest()
            _uiState.value = _uiState.value.copy(errorMessage = null)
            onSuccess()
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    private fun clearForm() {
        _uiState.value = _uiState.value.copy(
            fullName = "",
            email = "",
            password = "",
            phone = "",
            address = "",
            errorMessage = null
        )
    }
}

class AuthViewModelFactory(
    private val repository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
