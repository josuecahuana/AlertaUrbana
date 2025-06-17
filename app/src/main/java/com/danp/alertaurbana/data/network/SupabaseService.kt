package com.danp.alertaurbana.data.network

import com.danp.alertaurbana.data.model.ReportDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface SupabaseService {

    @Headers(
        "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14aGl1YXFobXBsa2dxeWZseGJhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwMzk5NDMsImV4cCI6MjA2NDYxNTk0M30.RWO7nnHWEqYUgbnnqdwVdVwdxLGzGcyoK0mgkGImAVo",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14aGl1YXFobXBsa2dxeWZseGJhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwMzk5NDMsImV4cCI6MjA2NDYxNTk0M30.RWO7nnHWEqYUgbnnqdwVdVwdxLGzGcyoK0mgkGImAVo" // o Bearer token si es sesión
    )
    @GET("reports?select=*")
    suspend fun getReports(): List<ReportDto>


    // Fabián:
    @Headers(
        "apikey: mi clave",
        "Authorization: Bearer mi token",
        "Content-Type: application/json",
        "Prefer: return=representation"
    )
    @POST("reports")
    suspend fun createReport(@Body report: ReportDto): ReportDto

    @Headers(
        "apikey: mi clave",
        "Authorization: Bearer mi token",
        "Content-Type: application/json",
        "Prefer: return=representation"
    )
    @PATCH("reports?id=eq.{id}")
    suspend fun updateReport(@Path("id") id: String, @Body report: ReportDto): ReportDto

    @Headers(
        "apikey: mi clave",
        "Authorization: Bearer mi token"
    )
    @DELETE("reports?id=eq.{id}")
    suspend fun deleteReport(@Path("id") id: String)
}
