package com.danp.alertaurbana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.domain.model.ReportStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ReportsListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsListUiState())
    val uiState: StateFlow<ReportsListUiState> = _uiState.asStateFlow()

    // Datos de prueba
    private val mockReports = listOf(
        Report(
            id = "1",
            title = "Robo de celular en el centro",
            description = "Me robaron mi celular en la Plaza de Armas",
            location = "Plaza de Armas, Arequipa",
            date = Date(System.currentTimeMillis() - 86400000), // 1 día atrás
            status = ReportStatus.PENDING,
            userId = "user1"
        ),
        Report(
            id = "2",
            title = "Asalto en transporte público",
            description = "Asaltaron el bus en la Av. La Marina",
            location = "Av. La Marina, Arequipa",
            date = Date(System.currentTimeMillis() - 172800000), // 2 días atrás
            status = ReportStatus.IN_PROGRESS,
            userId = "user1"
        ),
        Report(
            id = "3",
            title = "Robo de bicicleta",
            description = "Robaron mi bicicleta del estacionamiento",
            location = "Centro Comercial Real Plaza",
            date = Date(System.currentTimeMillis() - 259200000), // 3 días atrás
            status = ReportStatus.RESOLVED,
            userId = "user2"
        ),
        Report(
            id = "4",
            title = "Hurto en mercado",
            description = "Me sustrajeron la billetera en el Mercado San Camilo",
            location = "Mercado San Camilo",
            date = Date(System.currentTimeMillis() - 345600000), // 4 días atrás
            status = ReportStatus.PENDING,
            userId = "user3"
        )
    )

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Simular llamada de red
            delay(1500)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                reports = mockReports
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchReports(query)
    }

    private fun searchReports(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(reports = mockReports)
            } else {
                val filteredReports = mockReports.filter { report ->
                    report.title.contains(query, ignoreCase = true) ||
                            report.description.contains(query, ignoreCase = true) ||
                            report.location.contains(query, ignoreCase = true)
                }
                _uiState.value = _uiState.value.copy(reports = filteredReports)
            }
        }
    }

    fun refreshReports() {
        loadReports()
    }
}