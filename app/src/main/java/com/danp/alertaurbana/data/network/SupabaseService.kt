package com.danp.alertaurbana.data.network

import com.danp.alertaurbana.data.model.AuthResponse
import com.danp.alertaurbana.data.model.ReportDto
import com.danp.alertaurbana.data.model.UserDto
import com.danp.alertaurbana.data.model.UserProfileUpsertDto
import retrofit2.http.*

interface SupabaseService {

    @GET("rest/v1/reports?select=*")
    suspend fun getReports(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String
    ): List<ReportDto>

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Query("grant_type") grantType: String = "password",
        @Body requestBody: Map<String, String>
    ): AuthResponse

    @POST("auth/v1/signup")
    @Headers("Content-Type: application/json")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body requestBody: Map<String, String>
    ): AuthResponse

    @POST("auth/v1/token")
    @Headers("Content-Type: application/json")
    suspend fun refreshToken(
        @Header("apikey") apiKey: String,
        @Query("grant_type") grantType: String = "refresh_token",
        @Body requestBody: Map<String, String>
    ): AuthResponse

    @GET("rest/v1/reports")
    suspend fun getReportById(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("id") id: String
    ): List<ReportDto>

    @GET("rest/v1/profiles")
    suspend fun getUserProfileById(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("user_id") userId: String
    ): List<UserDto>

    @POST("rest/v1/profiles")
    @Headers(
        "Content-Type: application/json",
        "Prefer: resolution=merge-duplicates"
    )
    suspend fun upsertUserProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Body profileData: UserProfileUpsertDto
    )
    @GET("reports?select=*")
    suspend fun getReports(): List<ReportDto>


    // Fabián:
    @POST("rest/v1/reports")
    @Headers(
        "Content-Type: application/json",
        "Prefer: return=representation"
    )
    suspend fun createReport(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Body report: ReportDto
    ): ReportDto

    @PATCH("rest/v1/reports")
    @Headers(
        "Content-Type: application/json",
        "Prefer: return=representation"
    )
    suspend fun updateReport(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Body report: ReportDto
    ): ReportDto

    @DELETE("rest/v1/reports")
    suspend fun deleteReport(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("id") id: String
    )
}
