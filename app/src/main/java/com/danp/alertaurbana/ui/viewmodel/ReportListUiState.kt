package com.danp.alertaurbana.ui.viewmodel

import com.danp.alertaurbana.domain.model.Report

data class ReportsListUiState(
    val reports: List<Report> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)