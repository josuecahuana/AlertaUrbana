package com.danp.alertaurbana.data.repository

import com.danp.alertaurbana.data.network.SupabaseService
import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.data.model.ReportDto
import javax.inject.Inject
import javax.inject.Named
import com.danp.alertaurbana.data.local.dao.ReportDao
import com.danp.alertaurbana.data.local.mappers.toDomain
import com.danp.alertaurbana.data.local.mappers.toEntity
import com.danp.alertaurbana.data.session.SessionManager
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportRepository @Inject constructor(
    private val api: SupabaseService,
    private val dao: ReportDao,
    private val sessionManager: SessionManager,
    @Named("supabaseApiKey") private val supabaseApiKey: String
) {
    private suspend fun getAuthorization(): String {
        val token = sessionManager.getAccessToken().first() ?: ""
        return "Bearer $token"
    }

    suspend fun getReportById(id: String): Report? {
        return try {
            // 1. Intenta desde la red
            val dtos = api.getReportById(supabaseApiKey, getAuthorization(), "eq.$id")
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

    suspend fun syncReports() {
        try {
            // 1. Subir cambios locales pendientes
            val localChanges = dao.getPendingSyncReports()

            for (local in localChanges) {
                if (local.deletedLocally) {
                    // Si ya fue sincronado, se elimina también remotamente
                    try {
                        api.deleteReport(local.id)
                        // Lo quitamos de Room
                        dao.deleteReportById(local.id)
                    } catch (_: Exception) {
                        // Si falla, no lo borramos aún
                    }
                } else {
                    val domainReport = local.toDomain()
                    val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault()).format(Date())
                    val reportDto = ReportDto.fromDomain(domainReport).copy(lastSynced = now)

                    val response = if (dao.getReportById(local.id) == null) {
                        api.createReport(supabaseApiKey, getAuthorization(), reportDto)
                    } else {
                        api.updateReport(domainReport.id, reportDto)
                    }

                    // Marcamos como sincronizado
                    dao.insertReport(
                        response.toDomain().toEntity().copy(
                            isSynced = true,
                            lastModified = System.currentTimeMillis(),
                            deletedLocally = false
                        )
                    )
                }
            }

            // 2. Bajar cambios nuevos del servidor
            val remoteReports = api.getReports(supabaseApiKey, getAuthorization())

            for (remoteDto in remoteReports) {
                val remote = remoteDto.toDomain()
                val local = dao.getReportById(remote.id)

                // Si no existe localmente o el remoto es más reciente
                if (
                    local == null ||
                    local.lastModified < remote.date.time
                ) {
                    dao.insertReport(
                        remote.toEntity().copy(
                            isSynced = true,
                            lastModified = remote.date.time,
                            deletedLocally = false
                        )
                    )
                }
            }

            // 3. Borrar lo que fue eliminado localmente y sincronizado
            dao.deleteLocallyDeletedSyncedReports()

        } catch (e: Exception) {
            // Log o manejo de error
            println("Error en sincronización: ${e.message}")
        }
    }


    suspend fun getReports(): List<Report> {
        return try {
            syncReports() // Sincroniza primero

            // Devuelve datos actualizados
            dao.getAllReports().map { it.toDomain() }
        } catch (e: Exception) {
            // Si falla la red o la sincronización, usa los locales
            dao.getAllReports().map { it.toDomain() }
        }
    }



    //Fabián
    suspend fun createReport(report: Report): Result<Report> {
        return try {
            // Convertir el Report domain a DTO para enviar a la API
            val reportDto = ReportDto.fromDomain(report)

            // Llamar a la API para crear el reporte
            val createdReportDto = api.createReport(supabaseApiKey, getAuthorization(), reportDto)

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
