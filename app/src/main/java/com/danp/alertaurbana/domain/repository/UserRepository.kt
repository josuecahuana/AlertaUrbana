package com.danp.alertaurbana.domain.repository


import com.danp.alertaurbana.domain.model.User

interface UserRepository {
    suspend fun getUser(userId: String, accessToken: String): Result<User>
    suspend fun upsertUser(user: User, accessToken: String): Result<Unit>
}