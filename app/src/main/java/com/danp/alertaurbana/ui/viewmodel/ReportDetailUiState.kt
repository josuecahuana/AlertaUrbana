package com.danp.alertaurbana.ui.viewmodel

import com.danp.alertaurbana.domain.model.Report

data class ReportDetailUiState(
    val report: Report? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)