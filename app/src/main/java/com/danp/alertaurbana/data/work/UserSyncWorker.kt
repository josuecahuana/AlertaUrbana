package com.danp.alertaurbana.data.work


import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.danp.alertaurbana.data.local.dao.UserDao
import com.danp.alertaurbana.data.model.UserProfileUpsertDto
import com.danp.alertaurbana.data.network.SupabaseService
import com.danp.alertaurbana.data.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Named

@HiltWorker
class UserSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userDao: UserDao,
    private val sessionManager: SessionManager,
    private val supabaseService: SupabaseService,
    @Named("supabaseApiKey") private val apiKey: String // ✅ Añadir esto
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("UserSyncWorker", "⏳ Iniciando sincronización...")

        val usersToSync = userDao.getPendingSyncUsers()
        val token = sessionManager.getAccessToken().first()

        if (token.isNullOrEmpty()) {
            Log.w("UserSyncWorker", "❌ Token no disponible")
            return Result.retry()
        }

        for (user in usersToSync) {
            try {
                Log.d("UserSyncWorker", "🔄 Enviando usuario: ${user.id}")

                // ✅ AÑADIR AQUÍ
                val dto = UserProfileUpsertDto(
                    userId = user.id,
                    nombre = user.nombre,
                    direccion = user.direccion,
                    telefono = user.telefono,
                    fotoUrl = user.fotoUrl
                )

                supabaseService.upsertUserProfile(
                    apiKey = apiKey,
                    auth = "Bearer $token",
                    profileData = dto
                )

                // ✅ Actualiza como sincronizado solo si fue exitoso
                userDao.updateUser(user.copy(isSynced = true))
                Log.d("UserSyncWorker", "✅ Usuario sincronizado: ${user.id}")

            } catch (e: Exception) {
                Log.e("UserSyncWorker", "❌ Error al sincronizar ${user.id}", e)
                return Result.retry()
            }
        }

        return Result.success()
    }


    companion object {
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<UserSyncWorker>(
                1, java.util.concurrent.TimeUnit.HOURS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "user_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
        fun triggerImmediateSync(context: Context) {
            val request = OneTimeWorkRequestBuilder<UserSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "user_sync_now",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

}