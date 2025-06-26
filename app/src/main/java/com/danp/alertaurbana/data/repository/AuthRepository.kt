package com.danp.alertaurbana.data.repository

/*
import com.danp.alertaurbana.data.model.LoginRequest
import com.danp.alertaurbana.data.model.LoginResponse
import com.danp.alertaurbana.data.model.RegisterRequest*/
import com.danp.alertaurbana.data.network.RetrofitInstance
import com.danp.alertaurbana.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
/*
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val loginRequest = LoginRequest(email = email, password = password)
            val response = RetrofitInstance.api.login(loginRequest)

            // Convertir la respuesta a modelo de dominio
            val user = User(
                id = response.user.id,
                email = response.user.email,
                name = response.user.name,
                token = response.accessToken
            )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val registerRequest = RegisterRequest(
                email = email,
                password = password,
                name = name
            )
            val response = RetrofitInstance.api.register(registerRequest)

            val user = User(
                id = response.user.id,
                email = response.user.email,
                name = response.user.name,
                token = response.accessToken
            )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Boolean> {
        return try {
            RetrofitInstance.api.logout()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Para obtener el usuario actual (si tienes token guardado)
    suspend fun getCurrentUser(token: String): Result<User> {
        return try {
            val response = RetrofitInstance.api.getCurrentUser("Bearer $token")
            val user = User(
                id = response.id,
                email = response.email,
                name = response.name,
                token = token
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }*/
}