package com.genai.app.data

data class Message(
    val content: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)