package com.lexa.app.data.repository

import com.lexa.app.data.models.Content

interface ChatRepository {
    suspend fun getChatResponse(prompt: List<Content>): String
}