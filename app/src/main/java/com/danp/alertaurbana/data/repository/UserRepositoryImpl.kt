package com.danp.alertaurbana.data.repository

import com.danp.alertaurbana.data.model.UserDto
import com.danp.alertaurbana.data.model.UserProfileUpsertDto
import com.danp.alertaurbana.data.network.SupabaseService
import com.danp.alertaurbana.domain.model.User
import com.danp.alertaurbana.domain.repository.UserRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class UserRepositoryImpl @Inject constructor(
    private val api: SupabaseService,
    @Named("supabaseApiKey") private val apiKey: String
) : UserRepository {

    override suspend fun getUser(userId: String, accessToken: String): Result<User> {
        return try {
            val response = api.getUserProfileById(
                apiKey = apiKey,
                auth = "Bearer $accessToken",
                userId = "eq.$userId"
            ).firstOrNull()
            if (response != null) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a Internet"))
        } catch (e: HttpException) {
            Result.failure(Exception("Error al obtener datos del usuario"))
        }
    }


    private fun UserDto.toDomain(): User = User(
        id = id,
        nombre = nombre,
        direccion = direccion,
        telefono = telefono,
        fotoUrl = fotoUrl
    )

    override suspend fun upsertUser(user: User, accessToken: String): Result<Unit> {
        return try {
            val profileData = UserProfileUpsertDto(
                userId = user.id,
                nombre = user.nombre,
                direccion = user.direccion,
                telefono = user.telefono,
                fotoUrl = user.fotoUrl
            )

            api.upsertUserProfile(
                apiKey = apiKey,
                auth = "Bearer $accessToken",
                profileData = profileData
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear el perfil: ${e.message}"))
        }
    }
}