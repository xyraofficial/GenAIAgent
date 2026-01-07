package com.genai.app.data
import com.genai.app.BuildConfig

object SupabaseClient {
    val URL = if (BuildConfig.SUPABASE_URL.isNullOrEmpty()) "https://placeholder.supabase.co" else BuildConfig.SUPABASE_URL
    val ANON_KEY = if (BuildConfig.SUPABASE_ANON_KEY.isNullOrEmpty()) "dummy_anon_key" else BuildConfig.SUPABASE_ANON_KEY
}