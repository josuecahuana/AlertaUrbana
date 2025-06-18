package com.danp.alertaurbana.di

import com.danp.alertaurbana.data.repository.AuthRepositoryImpl
import com.danp.alertaurbana.data.repository.UserRepositoryImpl
import com.danp.alertaurbana.domain.repository.AuthRepository
import com.danp.alertaurbana.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
