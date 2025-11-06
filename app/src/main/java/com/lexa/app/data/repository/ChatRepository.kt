package com.lexa.app.data.repository

import com.lexa.app.data.local.ChatMessageEntity
import com.lexa.app.data.models.Content
import com.lexa.app.ui.chat.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChatResponse(prompt: List<Content>): String

    suspend fun saveMessage(message: ChatMessageEntity)
    fun getChatHistory(): Flow<List<ChatMessageEntity>>
}