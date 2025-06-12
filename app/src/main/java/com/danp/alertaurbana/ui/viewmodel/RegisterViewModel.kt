package com.danp.alertaurbana.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone, errorMessage = null)
    }

    fun register() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            delay(2000)

            val currentState = _uiState.value
            when {
                currentState.name.isBlank() || currentState.email.isBlank() ||
                        currentState.password.isBlank() || currentState.confirmPassword.isBlank() -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "Por favor completa todos los campos obligatorios"
                    )
                }
                currentState.password != currentState.confirmPassword -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "Las contraseñas no coinciden"
                    )
                }
                currentState.password.length < 6 -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    )
                }
                else -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}