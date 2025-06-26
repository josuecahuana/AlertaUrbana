package com.danp.alertaurbana.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.domain.model.ReportStatus
import com.danp.alertaurbana.ui.viewmodel.ReportsListViewModel
import java.text.SimpleDateFormat
import java.util.*

import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onNavigateToDetail: (Int) -> Unit = {},
    viewModel: ReportsListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    Column(modifier = modifier.fillMaxSize()) {
        // Barra de búsqueda
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            label = { Text("Buscar reportes...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = viewModel::refreshReports,
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = viewModel::refreshReports) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.reports) { report ->
                            ReportCard(
                                report = report,
                                onClick = {
                                    navController.navigate("report_detail/${report.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    report: Report,
    onClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = report.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                StatusBadge(status = report.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = report.location,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = dateFormatter.format(report.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: ReportStatus) {
    val (color, text) = when (status) {
        ReportStatus.PENDING -> MaterialTheme.colorScheme.secondary to "Pendiente"
        ReportStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary to "En proceso"
        ReportStatus.RESOLVED -> MaterialTheme.colorScheme.tertiary to "Resuelto"
        ReportStatus.REJECTED -> MaterialTheme.colorScheme.error to "Rechazado"
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
