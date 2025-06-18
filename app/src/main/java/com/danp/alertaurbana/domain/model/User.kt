package com.danp.alertaurbana.domain.model

data class User(
    val id: String,
    val nombre: String?,
    val direccion: String?,
    val telefono: String?,
    val fotoUrl: String?
)
