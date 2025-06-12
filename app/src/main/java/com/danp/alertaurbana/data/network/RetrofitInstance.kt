package com.danp.alertaurbana.data.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    val api: SupabaseService by lazy {
        Retrofit.Builder()
            .baseUrl("https://mxhiuaqhmplkgqyflxba.supabase.co/rest/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SupabaseService::class.java)
    }
}
