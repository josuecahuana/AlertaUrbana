package com.danp.alertaurbana.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "user_id") val id: String,
    val nombre: String?,
    val direccion: String?,
    val telefono: String?,
    @Json(name = "foto_url") val fotoUrl: String?
)