package com.danp.alertaurbana.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.danp.alertaurbana.data.local.dao.UserDao
import com.danp.alertaurbana.data.local.dao.ReportDao
import com.danp.alertaurbana.data.local.entity.UserEntity
import com.danp.alertaurbana.data.local.entities.ReportEntity

@Database(
    entities = [UserEntity::class, ReportEntity::class],
    version = 3, // Aumenta la versión si estás migrando desde versiones anteriores
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao
}