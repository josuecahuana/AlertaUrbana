package com.danp.alertaurbana.di


import android.content.Context
import androidx.room.Room
import com.danp.alertaurbana.data.local.AppDatabase
import com.danp.alertaurbana.data.local.dao.UserDao
import com.danp.alertaurbana.data.local.dao.ReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "alertaurbana.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideReportDao(db: AppDatabase): ReportDao = db.reportDao()
}
