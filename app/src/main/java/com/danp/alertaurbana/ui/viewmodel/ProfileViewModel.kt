package com.danp.alertaurbana.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danp.alertaurbana.data.session.SessionManager
import com.danp.alertaurbana.domain.model.User
import com.danp.alertaurbana.domain.repository.UserRepository
import com.danp.alertaurbana.domain.repository.LocalUserRepository
import com.danp.alertaurbana.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.danp.alertaurbana.data.local.mappers.toDomain
import com.danp.alertaurbana.data.local.mappers.toEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import com.danp.alertaurbana.data.repository.ReportRepository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.danp.alertaurbana.data.work.UserSyncWorker
import com.danp.alertaurbana.domain.model.Report


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sessionManager: SessionManager,
    private val localRepository: LocalUserRepository,
    private val reportRepository: ReportRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    sealed class UserUiState {
        object Idle : UserUiState()
        object Loading : UserUiState()
        data class Success(val user: User, val email: String) : UserUiState()
        data class Error(val message: String) : UserUiState()
    }

    private val _userReports = MutableStateFlow<List<Report>>(emptyList())
    val userReports: StateFlow<List<Report>> = _userReports.asStateFlow()

    fun loadUserReports() {
        viewModelScope.launch {
            _userReports.value = reportRepository.getReports().filter {
                it.userId == sessionManager.getUserId().firstOrNull()
            }
        }
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

            val localUser = localRepository.getUserById(userId)// 👈 local db

            if (localUser != null && !localUser.isSynced) {
                // 👈 Tenemos datos locales no sincronizados, los usamos
                _uiState.value = UserUiState.Success(localUser.toDomain(), email)
                return@launch
            }

            if (NetworkUtils.isConnected(context)) {
                // 🔄 Descarga del servidor solo si no hay conflictos
                repository.getUser(userId, token)
                    .onSuccess { user ->
                        _uiState.value = UserUiState.Success(user, email)
                        localRepository.saveUserLocally(user.toEntity(isSynced = true)) // 👈 Actualiza local también
                    }
                    .onFailure { error ->
                        _uiState.value = UserUiState.Error(error.message ?: "Error desconocido")
                    }
            } else {
                if (localUser != null) {
                    _uiState.value = UserUiState.Success(localUser.toDomain(), email)
                } else {
                    _uiState.value = UserUiState.Error("Sin conexión. No hay datos disponibles.")
                }
            }
        }
    }

    fun updateProfile(nombre: String, direccion: String, telefono: String) {
        val currentState = _uiState.value as? UserUiState.Success ?: return
        val currentUser = currentState.user
        val email = currentState.email

        val updatedUser = currentUser.copy(
            nombre = nombre,
            direccion = direccion,
            telefono = telefono
        )

        _uiState.value = UserUiState.Loading

        viewModelScope.launch {
            // Guardamos solo localmente con isSynced = false
            localRepository.saveUserLocally(updatedUser.toEntity(isSynced = false))

            // Opcional: solicitar al Worker que sincronice
            if (NetworkUtils.isConnected(context)) {
                UserSyncWorker.triggerImmediateSync(context)
            }

            _uiState.value = UserUiState.Success(updatedUser, email)
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
