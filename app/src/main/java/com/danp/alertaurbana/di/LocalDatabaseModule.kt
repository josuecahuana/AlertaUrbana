package com.danp.alertaurbana.di

import android.content.Context
import androidx.room.Room
import com.danp.alertaurbana.data.local.ReportDatabase
import com.danp.alertaurbana.data.local.dao.ReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ReportDatabase {
        return Room.databaseBuilder(
            context,
            ReportDatabase::class.java,
            "report_database"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideReportDao(db: ReportDatabase): ReportDao = db.reportDao()
}
