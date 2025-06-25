package com.danp.alertaurbana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.domain.model.ReportStatus
import com.danp.alertaurbana.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReportUiState())
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = null
        )
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(
            description = description,
            descriptionError = null
        )
    }

    fun onLocationChange(location: String) {
        _uiState.value = _uiState.value.copy(
            location = location,
            locationError = null
        )
    }

    fun onStatusSelected(status: ReportStatus) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
    }

    fun onStatusDropdownToggle(isExpanded: Boolean) {
        _uiState.value = _uiState.value.copy(isStatusDropdownExpanded = isExpanded)
    }

    fun createReport() {
        // Limpiar errores previos
        _uiState.value = _uiState.value.copy(
            titleError = null,
            descriptionError = null,
            locationError = null,
            generalError = null
        )

        // Validar campos
        val validationErrors = validateFields()
        if (validationErrors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                titleError = validationErrors["title"],
                descriptionError = validationErrors["description"],
                locationError = validationErrors["location"]
            )
            return
        }

        // Crear el reporte
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val currentState = _uiState.value
                val report = Report(
                    id = UUID.randomUUID().toString(),
                    title = currentState.title,
                    description = currentState.description,
                    location = currentState.location,
                    date = Date(),
                    status = currentState.selectedStatus,
                    userId = getCurrentUserId(),
                    images = emptyList(),
                    lastModified = Date()
                )

                val result = reportRepository.createReport(report)

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isReportCreated = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generalError = result.exceptionOrNull()?.message ?: "Error desconocido al crear el reporte"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    generalError = e.message ?: "Error desconocido"
                )
            }
        }
    }

    private fun validateFields(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val currentState = _uiState.value

        if (currentState.title.isBlank()) {
            errors["title"] = "El título es obligatorio"
        } else if (currentState.title.length < 5) {
            errors["title"] = "El título debe tener al menos 5 caracteres"
        }

        if (currentState.description.isBlank()) {
            errors["description"] = "La descripción es obligatoria"
        } else if (currentState.description.length < 10) {
            errors["description"] = "La descripción debe tener al menos 10 caracteres"
        }

        if (currentState.location.isBlank()) {
            errors["location"] = "La ubicación es obligatoria"
        }

        return errors
    }

    private fun getCurrentUserId(): String {
        // TODO: Implementar según tu sistema de autenticación
        // Por ahora retorna un ID temporal
        return "temp_user_id"
    }
}

data class CreateReportUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val selectedStatus: ReportStatus = ReportStatus.PENDING,
    val isStatusDropdownExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val isReportCreated: Boolean = false,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val locationError: String? = null,
    val generalError: String? = null
)