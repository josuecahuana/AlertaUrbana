package com.danp.alertaurbana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danp.alertaurbana.data.session.SessionManager
import com.danp.alertaurbana.domain.model.User
import com.danp.alertaurbana.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    sealed class UserUiState {
        object Idle : UserUiState()
        object Loading : UserUiState()
        data class Success(val user: User, val email: String) : UserUiState()
        data class Error(val message: String) : UserUiState()
    }

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun loadUser() {
        _uiState.value = UserUiState.Loading
        viewModelScope.launch {
            val token = sessionManager.getAccessToken().first() ?: ""
            val userId = sessionManager.getUserId().first() ?: ""
            val email = sessionManager.getEmail().first() ?: ""

            if (userId.isBlank()) {
                _uiState.value = UserUiState.Error("Usuario no identificado")
                return@launch
            }

            repository.getUser(userId, token)
                .onSuccess { user ->
                    _uiState.value = UserUiState.Success(user, email)
                }
                .onFailure { error ->
                    if (error.message?.contains("Usuario no encontrado") == true) {
                        val defaultUser = User(
                            id = userId,
                            nombre = "",
                            direccion = "",
                            telefono = "",
                            fotoUrl = ""
                        )
                        repository.upsertUser(defaultUser, token)
                            .onSuccess {
                                _uiState.value = UserUiState.Success(defaultUser, email)
                            }
                            .onFailure { createError ->
                                _uiState.value = UserUiState.Error("Error al crear el perfil: ${createError.message}")
                            }
                    } else {
                        _uiState.value = UserUiState.Error(error.message ?: "Error desconocido")
                    }
                }
        }
    }

    fun updateProfile(nombre: String, direccion: String, telefono: String) {
        val currentState = _uiState.value as? UserUiState.Success ?: return
        val currentUser = currentState.user
        val email = currentState.email
        val updatedUser = currentUser.copy(nombre = nombre, direccion = direccion, telefono = telefono)

        _uiState.value = UserUiState.Loading
        viewModelScope.launch {
            val token = sessionManager.getAccessToken().first() ?: ""
            repository.upsertUser(updatedUser, token)
                .onSuccess {
                    _uiState.value = UserUiState.Success(updatedUser, email)
                }
                .onFailure {
                    _uiState.value = UserUiState.Error("No se pudo actualizar el perfil")
                }
        }
    }

    fun updatePhoto(photoUrl: String) {
        val currentState = _uiState.value as? UserUiState.Success ?: return
        val currentUser = currentState.user
        val email = currentState.email
        val updatedUser = currentUser.copy(fotoUrl = photoUrl)

        _uiState.value = UserUiState.Loading
        viewModelScope.launch {
            val token = sessionManager.getAccessToken().first() ?: ""
            repository.upsertUser(updatedUser, token)
                .onSuccess {
                    _uiState.value = UserUiState.Success(updatedUser, email)
                }
                .onFailure {
                    _uiState.value = UserUiState.Error("No se pudo actualizar la foto de perfil")
                }
        }
    }

    fun saveNewProfile(user: User) {
        viewModelScope.launch {
            val email = sessionManager.getEmail().first() ?: ""
            _uiState.value = UserUiState.Loading
            val token = sessionManager.getAccessToken().first() ?: ""
            repository.upsertUser(user, token)
                .onSuccess {
                    _uiState.value = UserUiState.Success(user, email)
                }
                .onFailure {
                    _uiState.value = UserUiState.Error("No se pudo guardar el nuevo perfil")
                }
        }
    }

    fun createDefaultProfile() {
        viewModelScope.launch {
            val userId = sessionManager.getUserId().first() ?: run {
                _uiState.value = UserUiState.Error("Usuario no identificado")
                return@launch
            }

            val accessToken = sessionManager.getAccessToken().first() ?: run {
                _uiState.value = UserUiState.Error("Token no disponible")
                return@launch
            }

            val email = sessionManager.getEmail().first() ?: run {
                _uiState.value = UserUiState.Error("Correo no disponible")
                return@launch
            }

            val user = User(
                id = userId,
                nombre = "",
                direccion = "",
                telefono = "",
                fotoUrl = null
            )

            saveNewProfile(user)
        }
    }
}
