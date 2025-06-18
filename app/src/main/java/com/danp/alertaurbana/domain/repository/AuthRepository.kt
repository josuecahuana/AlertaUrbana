package com.danp.alertaurbana.domain.repository

import com.danp.alertaurbana.data.model.AuthResponse

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<AuthResponse>
    suspend fun signUp(email: String, password: String): Result<AuthResponse>
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse>
}
