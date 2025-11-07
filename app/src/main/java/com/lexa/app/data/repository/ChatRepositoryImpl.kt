package com.lexa.app.data.repository

import com.lexa.app.BuildConfig
import com.lexa.app.data.local.ChatDao
import com.lexa.app.data.local.ChatMessageEntity
import com.lexa.app.data.local.ChatSessionEntity
import com.lexa.app.data.models.Content
import com.lexa.app.data.models.GeminiRequest
import com.lexa.app.data.models.Part
import com.lexa.app.data.network.AIService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val apiService: AIService,
    private val chatDao: ChatDao
) : ChatRepository {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    override suspend fun createNewSession(title: String): Long {
        val newSession = ChatSessionEntity(
            title = title,
            timestamp = System.currentTimeMillis()
        )
        return chatDao.insertSession(newSession)
    }

    override fun getAllSessions(): Flow<List<ChatSessionEntity>> {
        return chatDao.getAllSessions()
    }

    override suspend fun deleteSession(sessionId: Long) {
        chatDao.deleteSession(sessionId)
    }

    override suspend fun saveMessage(message: ChatMessageEntity) {
        chatDao.insertMessage(message)
    }

    override fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForSession(sessionId)
    }

    override suspend fun getApiResponse(history: List<Content>): String {
        val request = GeminiRequest(contents = history)
        try {
            val response = apiService.generateContent(apiKey = apiKey, request = request)
            val textResponse = response.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()
                ?.text

            return if (textResponse != null) {
                textResponse
            } else if (response.promptFeedback?.blockReason != null) {
                "Error: Tu pregunta fue bloqueada (${response.promptFeedback.blockReason})"
            } else {
                "Error: No se recibió una respuesta válida de la IA."
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e.message?.contains("HTTP 400") == true) {
                return "Error: La API Key de Gemini es inválida."
            } else {
                return "Error de red: ${e.message}"
            }
        }
    }

}