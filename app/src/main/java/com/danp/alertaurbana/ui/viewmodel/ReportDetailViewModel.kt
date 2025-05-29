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

class ReportDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportDetailUiState())
    val uiState: StateFlow<ReportDetailUiState> = _uiState.asStateFlow()

    fun loadReportDetail(reportId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Simular llamada de red
            delay(1000)

            // Datos de prueba - simulando obtener el reporte por ID
            val mockReport = when (reportId) {
                "1" -> Report(
                    id = "1",
                    title = "Robo de celular en el centro",
                    description = "Aproximadamente a las 3:00 PM del día de ayer, mientras caminaba por la Plaza de Armas, un sujeto se acercó y me arrebató mi celular Samsung Galaxy. El individuo tenía aproximadamente 25 años, vestía polera negra y jeans azules. Corrió hacia la calle Mercaderes. Solicito apoyo para la investigación.",
                    location = "Plaza de Armas, Arequipa",
                    date = Date(System.currentTimeMillis() - 86400000),
                    status = ReportStatus.PENDING,
                    userId = "user1",
                    images = listOf("image1.jpg", "image2.jpg")
                )
                "2" -> Report(
                    id = "2",
                    title = "Asalto en transporte público",
                    description = "En el bus de la ruta Paucarpata-Centro, tres sujetos armados asaltaron a los pasajeros. Se llevaron celulares, carteras y dinero en efectivo. El conductor no pudo hacer nada. Ocurrió cerca del puente Grau.",
                    location = "Av. La Marina, Arequipa",
                    date = Date(System.currentTimeMillis() - 172800000),
                    status = ReportStatus.IN_PROGRESS,
                    userId = "user1"
                )
                else -> null
            }

            if (mockReport != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    report = mockReport
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No se pudo cargar el reporte"
                )
            }
        }
    }
}
