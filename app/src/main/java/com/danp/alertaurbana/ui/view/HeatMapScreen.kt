package com.danp.alertaurbana.ui.view

import com.danp.alertaurbana.ui.viewmodel.HeatMapViewModel
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

import com.danp.alertaurbana.utils.toLatLngOrNull

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapProperties
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.compose.MapEffect

@Composable
fun HeatMapScreen(
    viewModel: HeatMapViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val reports by viewModel.reports.collectAsState()
    val topDistricts by viewModel.topDistricts.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(-12.0464, -77.0428),
            12f
        )
    }

    Column(modifier = modifier.fillMaxSize()) { // Usa el modifier que viene de arriba
        Box(modifier = Modifier.weight(1f)) { // Usa weight para respetar la barra inferior
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                properties = MapProperties(isMyLocationEnabled = true)
            ) {
                // Mostrar marcadores
                reports.forEach { report ->
                    val latLng = report.location.toLatLngOrNull() ?: return@forEach
                    Marker(
                        state = MarkerState(position = latLng),
                        title = report.title,
                        snippet = report.description
                    )
                }

                // Mostrar capa de calor
                MapEffect(reports) { googleMap ->
                    val latLngs = reports.mapNotNull { it.location.toLatLngOrNull() }
                    if (latLngs.isNotEmpty()) {
                        val provider = HeatmapTileProvider.Builder()
                            .data(latLngs)
                            .radius(50)
                            .build()
                        googleMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Top 3 distritos con más reportes:", style = MaterialTheme.typography.titleMedium)
            topDistricts.forEach { (district, count) ->
                Text("$district: $count reportes", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
