package com.danp.alertaurbana.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val location: String,
    val date: Long,
    val status: String,
    val userId: String,
    val images: String,

    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis(),
    val deletedLocally: Boolean = false
)
