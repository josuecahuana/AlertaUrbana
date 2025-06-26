package com.danp.alertaurbana.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.danp.alertaurbana.data.local.dao.ReportDao
import com.danp.alertaurbana.data.local.entities.ReportEntity

@Database(
    entities = [ReportEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ReportDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
}