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
import android.util.Log

@Singleton
class ReportRepository @Inject constructor(
    private val api: SupabaseService,
    private val dao: ReportDao,
    @Named("supabaseApiKey") private val supabaseApiKey: String
) {
    private val authorization = "Bearer $supabaseApiKey"

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
            Log.e("ReportRepository", "Error fetching reports from network", e)
            // Error de red → usar local
            dao.getAllReports().map { it.toDomain() }
        }
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
            Log.e("ReportRepository", "Error fetching report by id from network", e)
            // 3. Si hay error, intenta desde Room
            dao.getReportById(id)?.toDomain()
        }
    }

    suspend fun createReport(report: Report): Result<Report> {
        return try {
            Log.d("ReportRepository", "Creating report: ${report.title}")

            // Convertir el Report domain a DTO para enviar a la API
            val reportDto = ReportDto.fromDomain(report)
            Log.d("ReportRepository", "Converted to DTO: $reportDto")

            // Llamar a la API para crear el reporte
            val createdReportDto = api.createReport(
                apiKey = supabaseApiKey,
                authorization = authorization,
                report = reportDto
            )

            Log.d("ReportRepository", "API response: $createdReportDto")

            // Convertir la respuesta de vuelta a domain model
            val createdReport = createdReportDto.toDomain()

            // Guardar también en local
            dao.insertReport(createdReport.toEntity())

            Result.success(createdReport)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error creating report", e)

            // Si falla la red, intentar guardar solo localmente
            try {
                dao.insertReport(report.toEntity())
                Result.success(report)
            } catch (localException: Exception) {
                Log.e("ReportRepository", "Error saving report locally", localException)
                Result.failure(e)
            }
        }
    }

    suspend fun updateReport(report: Report): Result<Report> {
        return try {
            val reportDto = ReportDto.fromDomain(report)
            val updatedReportDto = api.updateReport(
                apiKey = supabaseApiKey,
                authorization = authorization,
                id = "eq.${report.id}",
                report = reportDto
            )
            val updatedReport = updatedReportDto.toDomain()

            // Actualizar en local también
            dao.insertReport(updatedReport.toEntity())

            Result.success(updatedReport)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error updating report", e)
            Result.failure(e)
        }
    }

    suspend fun deleteReport(reportId: String): Result<Boolean> {
        return try {
            api.deleteReport(
                apiKey = supabaseApiKey,
                authorization = authorization,
                id = "eq.$reportId"
            )

            // Eliminar de local también
            dao.deleteReportById(reportId)

            Result.success(true)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error deleting report", e)
            Result.failure(e)
        }
    }
}