package com.danp.alertaurbana.utils

import android.content.Context
import com.danp.alertaurbana.R
import org.json.JSONObject

object KeyProvider {
    fun getSupabaseApiKey(context: Context): String {
        val inputStream = context.resources.openRawResource(R.raw.keys)
        val json = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)
        return jsonObject.getString("supabase_api_key")
    }
}