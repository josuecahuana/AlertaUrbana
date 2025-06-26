package com.danp.alertaurbana.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "auth_prefs")

object TokenStorage {
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")

    suspend fun saveAccessToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun getAccessToken(context: Context): String {
        return context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN_KEY] ?: ""
        }.first()
    }
}
