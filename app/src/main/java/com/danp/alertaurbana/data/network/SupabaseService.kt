package com.danp.alertaurbana.data.network

import com.danp.alertaurbana.data.model.ReportDto
import retrofit2.http.GET
import retrofit2.http.Headers

interface SupabaseService {

    @Headers(
        "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14aGl1YXFobXBsa2dxeWZseGJhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwMzk5NDMsImV4cCI6MjA2NDYxNTk0M30.RWO7nnHWEqYUgbnnqdwVdVwdxLGzGcyoK0mgkGImAVo",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14aGl1YXFobXBsa2dxeWZseGJhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwMzk5NDMsImV4cCI6MjA2NDYxNTk0M30.RWO7nnHWEqYUgbnnqdwVdVwdxLGzGcyoK0mgkGImAVo" // o Bearer token si es sesión
    )
    @GET("reports?select=*")
    suspend fun getReports(): List<ReportDto>

}
