package com.danp.alertaurbana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danp.alertaurbana.data.repository.ReportRepository
import com.danp.alertaurbana.domain.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportDetailViewModel : ViewModel() {

    private val repository = ReportRepository()

    private val _uiState = MutableStateFlow(ReportDetailUiState())
    val uiState: StateFlow<ReportDetailUiState> = _uiState.asStateFlow()

    fun loadReportDetail(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val report = repository.getReportById(id)
                if (report != null) {
                    _uiState.value = _uiState.value.copy(
                        report = report,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Reporte no encontrado",
                        isLoading = false,
                        report = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar el reporte: ${e.localizedMessage}",
                    isLoading = false,
                    report = null
                )
            }
        }
    }
}