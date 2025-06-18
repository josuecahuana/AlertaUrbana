package com.danp.alertaurbana.ui.viewmodel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danp.alertaurbana.data.session.SessionManager
import com.danp.alertaurbana.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class RegisterSuccess(val message: String) : AuthState()
    data class LoginSuccess(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    init {
        Log.d("AuthViewModel", "✅ ViewModel creado correctamente")
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email, password)
            _authState.value = result.fold(
                onSuccess = {
                    val accessToken = it.accessToken ?: ""
                    val refreshToken = it.refreshToken ?: ""
                    val user = it.user
                    val userId = user?.id ?: ""



                    // Guardamos los datos en SessionManager
                    sessionManager.saveTokens(accessToken, refreshToken)
                    sessionManager.saveUserId(userId)
                    sessionManager.saveEmail(user?.email ?: "")


                    return@fold AuthState.LoginSuccess(accessToken)
                },
                onFailure = {
                    return@fold AuthState.Error(it.message ?: "Error desconocido")
                }
            )
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUp(email, password)
            _authState.value = result.fold(
                onSuccess = {
                    AuthState.RegisterSuccess("Registro exitoso. Verifique su correo para iniciar sesión.")
                },
                onFailure = {
                    AuthState.Error(it.message ?: "Error desconocido")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearTokens()
            _authState.value = AuthState.Idle
        }
    }

    fun checkLogin() {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken().first()
            if (!token.isNullOrEmpty()) {
                refreshSessionIfNeeded()
            } else {
                _authState.value = AuthState.Idle
            }
        }
    }

    fun clearAuthState() {
        _authState.value = AuthState.Idle
    }

    fun refreshSessionIfNeeded() {
        viewModelScope.launch {
            val refreshToken = sessionManager.getRefreshToken().first()
            if (!refreshToken.isNullOrEmpty()) {
                val result = repository.refreshToken(refreshToken)
                result.fold(
                    onSuccess = {
                        val newAccessToken = it.accessToken ?: return@fold
                        val newRefreshToken = it.refreshToken ?: refreshToken
                        sessionManager.saveTokens(newAccessToken, newRefreshToken)
                        _authState.value = AuthState.LoginSuccess(newAccessToken)
                    },
                    onFailure = {
                        sessionManager.clearTokens()
                        _authState.value = AuthState.Error("Sesión expirada. Vuelve a iniciar sesión.")
                    }
                )
            } else {
                _authState.value = AuthState.Idle
            }
        }
    }
}

