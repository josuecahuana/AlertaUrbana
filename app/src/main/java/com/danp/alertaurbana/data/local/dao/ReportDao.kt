package com.danp.alertaurbana.data.local.dao

import androidx.room.*
import com.danp.alertaurbana.data.local.entities.ReportEntity

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports")
    suspend fun getAllReports(): List<ReportEntity>

    @Query("SELECT * FROM reports WHERE id = :reportId LIMIT 1")
    suspend fun getReportById(reportId: String): ReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<ReportEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Delete
    suspend fun deleteReport(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :reportId")
    suspend fun deleteReportById(reportId: String)

    @Query("DELETE FROM reports")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM reports")
    suspend fun count(): Int
}
