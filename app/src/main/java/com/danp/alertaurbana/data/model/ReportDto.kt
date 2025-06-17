package com.danp.alertaurbana.data.model

import com.danp.alertaurbana.domain.model.Report
import com.danp.alertaurbana.domain.model.ReportStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class ReportDto(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val status: String,
    @Json(name = "user_id") val userId: String,
    val images: List<String> = emptyList()
) {
    fun toDomain(): Report {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val parsedDate = try {
            format.parse(date) ?: Date()
        } catch (e: Exception) {
            Date()
        }

        return Report(
            id = id,
            title = title,
            description = description,
            location = location,
            date = parsedDate,
            status = ReportStatus.valueOf(status),
            userId = userId,
            images = images
        )
    }

    companion object {
        fun fromDomain(report: Report): ReportDto {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

            return ReportDto(
                id = report.id,
                title = report.title,
                description = report.description,
                location = report.location,
                date = format.format(report.date),
                status = report.status.name,
                userId = report.userId,
                images = report.images
            )
        }
    }
}
