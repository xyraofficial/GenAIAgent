package com.genai.app.data
import com.genai.app.BuildConfig

object OpenAIClient {
    val API_KEY = if (BuildConfig.OPENAI_API_KEY.isEmpty()) "dummy_key" else BuildConfig.OPENAI_API_KEY
}