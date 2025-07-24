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
import com.danp.alertaurbana.data.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull

@Singleton
class ReportRepository @Inject constructor(
    private val api: SupabaseService,
    private val dao: ReportDao,
    private val sessionManager: SessionManager,
    @Named("supabaseApiKey") private val supabaseApiKey: String
) {
    suspend fun getReports(): List<Report> {
        return try {
            val accessToken = sessionManager.getAccessToken().firstOrNull()
            val authorization = "Bearer $accessToken"

            val remoteDtos = api.getReports(supabaseApiKey, authorization)
            val remoteReports = remoteDtos.map { it.toDomain() }

            val localReportsMap = dao.getAllReports().associateBy { it.id }
            val toInsert = mutableListOf<ReportEntity>()

            for (remoteReport in remoteReports) {
                val local = localReportsMap[remoteReport.id]
                if (local == null || remoteReport.lastModified.time > local.lastModified) {
                    toInsert.add(remoteReport.toEntity())
                }
            }

            if (toInsert.isNotEmpty()) {
                dao.insertReports(toInsert)
            }

            dao.getAllReports().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error fetching reports from network", e)
            dao.getAllReports().map { it.toDomain() }
        }
    }

    suspend fun getReportById(id: String): Report? {
        return try {
            val accessToken = sessionManager.getAccessToken().firstOrNull()
            val authorization = "Bearer $accessToken"

            val dtos = api.getReportById(supabaseApiKey, authorization, "eq.$id")
            val report = dtos.firstOrNull()?.toDomain()

            if (report != null) {
                dao.insertReport(report.toEntity())
            }

            report
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error fetching report by id from network", e)
            dao.getReportById(id)?.toDomain()
        }
    }

    suspend fun createReport(report: Report): Result<Report> {
        return try {
            Log.d("ReportRepository", "Creating report: ${report.title}")
            val accessToken = sessionManager.getAccessToken().firstOrNull()

            if (accessToken.isNullOrBlank()) {
                return Result.failure(Exception("No se encontró access token del usuario"))
            }

            val authorization = "Bearer $accessToken"
            val reportDto = ReportDto.fromDomain(report)
            Log.d("ReportRepository", "Converted to DTO: $reportDto")

            val createdReportDtoList = api.createReport(
                apiKey = supabaseApiKey,
                auth = authorization,
                report = reportDto
            )

            val createdReportDto = createdReportDtoList.firstOrNull()

            if (createdReportDto == null) {
                return Result.failure(Exception("No se pudo crear el reporte en el servidor"))
            }

            val createdReport = createdReportDto.toDomain()
            dao.insertReport(createdReport.toEntity())

            Result.success(createdReport)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error creating report", e)

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
            val accessToken = sessionManager.getAccessToken().firstOrNull()
            val authorization = "Bearer $accessToken"

            val reportDto = ReportDto.fromDomain(report)
            val updatedReportDto = api.updateReport(
                apiKey = supabaseApiKey,
                authorization = authorization,
                id = "eq.${report.id}",
                report = reportDto
            )

            val updatedReport = updatedReportDto.toDomain()
            dao.insertReport(updatedReport.toEntity())

            Result.success(updatedReport)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error updating report", e)
            Result.failure(e)
        }
    }

    suspend fun deleteReport(reportId: String): Result<Boolean> {
        return try {
            val accessToken = sessionManager.getAccessToken().firstOrNull()
            val authorization = "Bearer $accessToken"

            api.deleteReport(
                apiKey = supabaseApiKey,
                authorization = authorization,
                id = "eq.$reportId"
            )

            dao.deleteReportById(reportId)
            Result.success(true)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error deleting report", e)
            Result.failure(e)
        }
    }
}