package com.danp.alertaurbana.data.repository

import com.danp.alertaurbana.data.network.SupabaseService
import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.data.model.ReportDto
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named

class ReportRepository @Inject constructor(
    private val api: SupabaseService,
    @Named("supabaseApiKey") private val supabaseApiKey: String
) {
    private val authorization = "Bearer $supabaseApiKey"

    suspend fun fetchReports(): List<Report> {
        val dtos = api.getReports(supabaseApiKey, authorization)
        return dtos.map { it.toDomain() }
    }

    suspend fun getReportById(id: String): Report? {
        val dtos = api.getReportById(supabaseApiKey, authorization, "eq.$id")
        return dtos.firstOrNull()?.toDomain()
    }

    suspend fun getReports(): List<Report> {
        return api.getReports(supabaseApiKey, authorization).map { it.toDomain() }
    }
    //Fabián
    suspend fun createReport(report: Report): Result<Report> {
        return try {
            // Convertir el Report domain a DTO para enviar a la API
            val reportDto = ReportDto.fromDomain(report)

            // Llamar a la API para crear el reporte
            val createdReportDto = api.createReport(reportDto)

            // Convertir la respuesta de vuelta a domain model
            val createdReport = createdReportDto.toDomain()

            Result.success(createdReport)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReport(report: Report): Result<Report> {
        return try {
            val reportDto = ReportDto.fromDomain(report)
            val updatedReportDto = api.updateReport(report.id, reportDto)
            val updatedReport = updatedReportDto.toDomain()

            Result.success(updatedReport)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReport(reportId: String): Result<Boolean> {
        return try {
            api.deleteReport(reportId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
