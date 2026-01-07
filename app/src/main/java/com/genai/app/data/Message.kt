package com.genai.app.data

data class Message(
    val content: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    var isThinking: Boolean = false,
    var statusText: String? = null
)