package com.danp.alertaurbana.data.local.mappers

import com.danp.alertaurbana.data.local.entity.UserEntity
import com.danp.alertaurbana.data.model.UserDto
import com.danp.alertaurbana.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    nombre = nombre ?: "",
    direccion = direccion ?: "",
    telefono = telefono ?: "",
    fotoUrl = fotoUrl
)

fun User.toEntity(isSynced: Boolean = false): UserEntity = UserEntity(
    id = id,
    nombre = nombre,
    direccion = direccion,
    telefono = telefono,
    fotoUrl = fotoUrl,
    isSynced = isSynced
)