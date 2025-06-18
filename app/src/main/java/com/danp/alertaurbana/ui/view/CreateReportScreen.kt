@file:OptIn(ExperimentalMaterial3Api::class)

package com.danp.alertaurbana.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danp.alertaurbana.domain.model.ReportStatus
import com.danp.alertaurbana.ui.viewmodel.CreateReportViewModel

@Composable
fun CreateReportScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: CreateReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isReportCreated) {
        if (uiState.isReportCreated) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Reporte") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Título del reporte") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it) } }
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descripción detallada") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                isError = uiState.descriptionError != null,
                supportingText = uiState.descriptionError?.let { { Text(it) } }
            )

            OutlinedTextField(
                value = uiState.location,
                onValueChange = viewModel::onLocationChange,
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.locationError != null,
                supportingText = uiState.locationError?.let { { Text(it) } }
            )

            ExposedDropdownMenuBox(
                expanded = uiState.isStatusDropdownExpanded,
                onExpandedChange = viewModel::onStatusDropdownToggle
            ) {
                OutlinedTextField(
                    value = getStatusDisplayName(uiState.selectedStatus),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de incidente") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = uiState.isStatusDropdownExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = uiState.isStatusDropdownExpanded,
                    onDismissRequest = { viewModel.onStatusDropdownToggle(false) }
                ) {
                    ReportStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(getStatusDisplayName(status)) },
                            onClick = {
                                viewModel.onStatusSelected(status)
                                viewModel.onStatusDropdownToggle(false)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::createReport,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Crear Reporte")
            }

            uiState.generalError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun getStatusDisplayName(status: ReportStatus): String {
    return when (status) {
        ReportStatus.PENDING -> "Pendiente"
        ReportStatus.IN_PROGRESS -> "En Progreso"
        ReportStatus.RESOLVED -> "Resuelto"
        ReportStatus.REJECTED -> "Rechazado"
        else -> status.name
    }
}
