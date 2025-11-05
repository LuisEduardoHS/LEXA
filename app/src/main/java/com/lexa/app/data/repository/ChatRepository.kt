package com.lexa.app.data.repository

interface ChatRepository {
    suspend fun getChatResponse(prompt: String): String
}