package com.danp.alertaurbana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: String,
    val nombre: String?,
    val direccion: String?,
    val telefono: String?,
    val fotoUrl: String?,
    val isSynced: Boolean = false
)