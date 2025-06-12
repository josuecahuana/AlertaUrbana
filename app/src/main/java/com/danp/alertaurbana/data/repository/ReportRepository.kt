package com.danp.alertaurbana.data.repository

import com.danp.alertaurbana.data.network.RetrofitInstance
import com.danp.alertaurbana.data.network.RetrofitInstance.api
import com.danp.alertaurbana.domain.model.Report

class ReportRepository {
    suspend fun fetchReports(): List<Report> {
        val dtos = RetrofitInstance.api.getReports()
        return dtos.map { it.toDomain() }
    }

    suspend fun getReportById(id: String): Report? {
        return getReports().find { it.id == id }
    }

    suspend fun getReports(): List<Report> {
        return api.getReports().map { it.toDomain() }
    }
}
