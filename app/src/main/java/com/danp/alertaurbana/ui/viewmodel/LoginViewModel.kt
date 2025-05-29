package com.danp.alertaurbana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Simular llamada de red
            delay(2000)

            val currentState = _uiState.value
            if (currentState.email.isBlank() || currentState.password.isBlank()) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Por favor completa todos los campos"
                )
            } else if (currentState.email == "test@example.com" && currentState.password == "123456") {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isLoginSuccessful = true
                )
            } else {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Credenciales incorrectas"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}