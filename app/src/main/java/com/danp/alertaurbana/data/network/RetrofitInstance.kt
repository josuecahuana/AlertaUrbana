package com.danp.alertaurbana.data.network

import android.content.Context
import com.danp.alertaurbana.utils.KeyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import javax.inject.Named


@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://mxhiuaqhmplkgqyflxba.supabase.co/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSupabaseService(retrofit: Retrofit): SupabaseService {
        return retrofit.create(SupabaseService::class.java)
    }

    @Provides
    @Singleton
    @Named("supabaseApiKey")
    fun provideSupabaseApiKey(
        @ApplicationContext context: Context
    ): String {
        return KeyProvider.getSupabaseApiKey(context)
    }
}
