package com.danp.alertaurbana.data.repository

import com.danp.alertaurbana.data.local.dao.UserDao
import com.danp.alertaurbana.data.local.entity.UserEntity
import com.danp.alertaurbana.domain.model.User
import com.danp.alertaurbana.domain.repository.LocalUserRepository
import javax.inject.Inject

class LocalUserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : LocalUserRepository {

    override suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    override suspend fun saveUserLocally(user: UserEntity) {
        userDao.updateUser(user)
    }

    override suspend fun updateUserLocally(user: UserEntity) {
        userDao.updateUser(user.copy(isSynced = false))
    }

}