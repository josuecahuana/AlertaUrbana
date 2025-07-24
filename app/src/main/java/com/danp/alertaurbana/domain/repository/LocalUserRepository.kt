package com.danp.alertaurbana.domain.repository


import com.danp.alertaurbana.data.local.entity.UserEntity

interface LocalUserRepository {
    suspend fun getUserById(userId: String): UserEntity?
    suspend fun saveUserLocally(user: UserEntity)
    suspend fun updateUserLocally(user: UserEntity)
}