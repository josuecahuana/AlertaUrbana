package com.danp.alertaurbana.data.repository

import com.danp.alertaurbana.data.network.SupabaseService
import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.data.model.ReportDto
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named
import com.danp.alertaurbana.data.local.dao.ReportDao
import com.danp.alertaurbana.data.local.entities.ReportEntity
import com.danp.alertaurbana.data.local.mappers.toDomain
import com.danp.alertaurbana.data.local.mappers.toEntity

class ReportRepository @Inject constructor(
    private val api: SupabaseService,
    private val dao: ReportDao,
    @Named("supabaseApiKey") private val supabaseApiKey: String
) {
    private val authorization = "Bearer $supabaseApiKey"

    suspend fun fetchReports(): List<Report> {
        val dtos = api.getReports(supabaseApiKey, authorization)
        return dtos.map { it.toDomain() }
    }

    suspend fun getReportById(id: String): Report? {
        return try {
            // 1. Intenta desde la red
            val dtos = api.getReportById(supabaseApiKey, authorization, "eq.$id")
            val report = dtos.firstOrNull()?.toDomain()

            // 2. Si lo encuentra, actualiza Room
            if (report != null) {
                dao.insertReport(report.toEntity())
            }

            report
        } catch (e: Exception) {
            // 3. Si hay error, intenta desde Room
            dao.getReportById(id)?.toDomain()
        }
    }


    suspend fun getReports(): List<Report> {
        return try {
            val remoteDtos = api.getReports(supabaseApiKey, authorization)
            val remoteReports = remoteDtos.map { it.toDomain() }

            // Obtener local
            val localReportsMap = dao.getAllReports().associateBy { it.id }

            val toInsert = mutableListOf<ReportEntity>()

            for (remoteReport in remoteReports) {
                val local = localReportsMap[remoteReport.id]

                // Si no existe localmente o el remoto es más nuevo
                if (local == null || remoteReport.lastModified.time > local.lastModified) {
                    toInsert.add(remoteReport.toEntity())
                }
            }

            // Guardar los que cambiaron
            if (toInsert.isNotEmpty()) {
                dao.insertReports(toInsert)
            }

            // Retornar la data sincronizada
            dao.getAllReports().map { it.toDomain() }

        } catch (e: Exception) {
            // Error de red → usar local
            dao.getAllReports().map { it.toDomain() }
        }
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
