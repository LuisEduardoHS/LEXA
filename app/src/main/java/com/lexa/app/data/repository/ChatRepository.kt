package com.lexa.app.data.repository

import com.lexa.app.data.local.ChatMessageEntity
import com.lexa.app.data.local.ChatSessionEntity
import com.lexa.app.data.models.Content
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun createNewSession(title: String): Long
    fun getAllSessions(): Flow<List<ChatSessionEntity>>
    suspend fun deleteSession(sessionId: Long)

    suspend fun getApiResponse(history: List<Content>): String
    suspend fun saveMessage(message: ChatMessageEntity)
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>>
}