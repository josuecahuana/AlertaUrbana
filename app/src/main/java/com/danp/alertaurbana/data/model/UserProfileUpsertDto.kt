package com.danp.alertaurbana.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileUpsertDto(
    @Json(name = "user_id") val userId: String,
    val nombre: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    @Json(name = "foto_url") val fotoUrl: String? = null
)