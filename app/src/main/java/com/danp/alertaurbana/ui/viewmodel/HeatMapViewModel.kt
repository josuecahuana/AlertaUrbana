package com.danp.alertaurbana.ui.viewmodel
import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.danp.alertaurbana.data.repository.ReportRepository
import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.utils.toLatLngOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@HiltViewModel
class HeatMapViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val _topDistricts = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val topDistricts = _topDistricts.asStateFlow()

    private val geocoder = Geocoder(application, Locale.getDefault())

    init {
        viewModelScope.launch {
            val reportList = reportRepository.getReports()
            _reports.value = reportList
            computeTopDistricts(reportList)
        }
    }

    private suspend fun computeTopDistricts(reportList: List<Report>) {
        withContext(Dispatchers.IO) {
            val districtCounts = mutableMapOf<String, Int>()
            for (report in reportList) {
                val latLng = report.location.toLatLngOrNull() ?: continue
                try {
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    val district = addresses?.firstOrNull()?.subLocality ?: "Desconocido"
                    districtCounts[district] = districtCounts.getOrDefault(district, 0) + 1
                } catch (_: Exception) {
                    continue
                }
            }

            val top3 = districtCounts.entries
                .sortedByDescending { it.value }
                .take(3)
                .map { it.key to it.value }

            _topDistricts.value = top3
        }
    }
}
