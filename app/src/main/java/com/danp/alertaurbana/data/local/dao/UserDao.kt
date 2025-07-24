package com.danp.alertaurbana.data.local.dao

import androidx.room.*
import com.danp.alertaurbana.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_profile WHERE isSynced = 0")
    suspend fun getPendingSyncUsers(): List<UserEntity>

    @Update
    suspend fun updateUser(user: UserEntity)
}