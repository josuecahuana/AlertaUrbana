package com.danp.alertaurbana.utils

import com.google.android.gms.maps.model.LatLng

fun String.toLatLngOrNull(): LatLng? {
    return try {
        val parts = this.split(",")
        LatLng(parts[0].toDouble(), parts[1].toDouble())
    } catch (e: Exception) {
        null
    }
}