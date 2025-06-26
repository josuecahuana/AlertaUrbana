package com.danp.alertaurbana.domain.model

import java.util.Date

data class Report(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val date: Date,
    val status: ReportStatus,
    val userId: String,
    val images: List<String> = emptyList(),
    val lastModified: Date, // última modificación en el servidor
    val isSynced: Boolean = false,
    val deletedLocally: Boolean = false
)

enum class ReportStatus {
    PENDING, IN_PROGRESS, RESOLVED, REJECTED
}