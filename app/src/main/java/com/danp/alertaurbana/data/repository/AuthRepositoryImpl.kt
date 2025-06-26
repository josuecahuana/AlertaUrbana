package com.danp.alertaurbana.data.repository

import com.danp.alertaurbana.data.model.AuthResponse
import com.danp.alertaurbana.data.network.SupabaseService
import com.danp.alertaurbana.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    private val api: SupabaseService,
    @Named("supabaseApiKey") private val apiKey: String
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<AuthResponse> {
        return try {
            val body = mapOf("email" to email, "password" to password)
            val response = api.signIn(apiKey = apiKey, requestBody = body)
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a Internet"))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = when {
                errorBody?.contains("Email not confirmed", ignoreCase = true) == true ->
                    "Debe confirmar su correo electrónico antes de iniciar sesión"
                errorBody?.contains("Invalid login credentials", ignoreCase = true) == true ->
                    "Correo o contraseña incorrectos"
                else -> "Error del servidor: $errorBody"
            }
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun signUp(email: String, password: String): Result<AuthResponse> {
        return try {
            val body = mapOf("email" to email, "password" to password)
            val response = api.signUp(apiKey, body)

            Result.success(response)
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a Internet"))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = when {
                errorBody?.contains("User already registered", ignoreCase = true) == true ->
                    "Este correo ya está registrado. Intente recuperar su contraseña."
                else -> "Error del servidor: $errorBody"
            }
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
    override suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            val body = mapOf("refresh_token" to refreshToken)
            val response = api.refreshToken(
                apiKey = apiKey,
                grantType = "refresh_token",
                requestBody = body
            )
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a Internet"))
        } catch (e: HttpException) {
            Result.failure(Exception("Token de sesión inválido o expirado"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al renovar token: ${e.message}"))
        }
    }
}
