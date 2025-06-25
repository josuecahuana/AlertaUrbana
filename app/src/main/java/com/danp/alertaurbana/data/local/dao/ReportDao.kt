package com.danp.alertaurbana.data.local.dao

import androidx.room.*
import com.danp.alertaurbana.data.local.entities.ReportEntity

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports")
    fun getAllReports(): List<ReportEntity>

    @Query("SELECT * FROM reports WHERE id = :reportId LIMIT 1")
    fun getReportById(reportId: String): ReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReports(reports: List<ReportEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReport(report: ReportEntity)

    @Query("DELETE FROM reports")
    fun clearAll()

    @Query("SELECT COUNT(*) FROM reports")
    fun count(): Int
}
