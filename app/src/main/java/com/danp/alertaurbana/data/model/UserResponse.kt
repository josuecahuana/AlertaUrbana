package com.danp.alertaurbana.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "id") val id: String?,
    @Json(name = "email") val email: String?

)