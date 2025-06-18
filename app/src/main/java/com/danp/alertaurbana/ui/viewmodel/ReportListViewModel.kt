package com.danp.alertaurbana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danp.alertaurbana.data.repository.ReportRepository
import com.danp.alertaurbana.domain.model.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsListUiState(
    val reports: List<Report> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class ReportsListViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsListUiState())
    val uiState: StateFlow<ReportsListUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val reports = repository.getReports()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reports = reports,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar reportes: ${e.localizedMessage}"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchReports(query)
    }

    private fun searchReports(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val allReports = repository.getReports()
                val filteredReports = if (query.isBlank()) {
                    allReports
                } else {
                    allReports.filter { report ->
                        report.title.contains(query, ignoreCase = true) ||
                                report.description.contains(query, ignoreCase = true) ||
                                report.location.contains(query, ignoreCase = true)
                    }
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reports = filteredReports,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al buscar reportes: ${e.localizedMessage}"
                )
            }
        }
    }

    fun refreshReports() {
        loadReports()
    }
}
