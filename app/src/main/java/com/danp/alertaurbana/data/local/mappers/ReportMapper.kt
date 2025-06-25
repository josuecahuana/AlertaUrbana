package com.danp.alertaurbana.data.local.mappers

import com.danp.alertaurbana.data.local.entities.ReportEntity
import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.domain.model.ReportStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.Date

fun Report.toEntity(): ReportEntity {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))

    return ReportEntity(
        id = id,
        title = title,
        description = description,
        location = location,
        date = date.time,
        status = status.name,
        userId = userId,
        images = adapter.toJson(images),
        lastModified = lastModified.time,
        lastSynced = System.currentTimeMillis() // cuando se sincroniza localmente
    )
}

fun ReportEntity.toDomain(): Report {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))

    return Report(
        id = id,
        title = title,
        description = description,
        location = location,
        date = Date(date),
        status = ReportStatus.valueOf(status),
        userId = userId,
        images = adapter.fromJson(images) ?: emptyList(),
        lastModified = Date(lastModified)
    )
}
