package com.danp.alertaurbana.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val phone: String? = null
)
