package com.lexa.app.data.repository

import com.lexa.app.BuildConfig
import com.lexa.app.data.models.Content
import com.lexa.app.data.models.GeminiRequest
import com.lexa.app.data.models.Part
import com.lexa.app.data.network.AIService
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val apiService: AIService
) : ChatRepository {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    override suspend fun getChatResponse(history: List<Content>): String {

        val request = GeminiRequest(
            contents = history
        )

        try {
            val response = apiService.generateContent(
                apiKey = apiKey,
                request = request
            )
            val textResponse = response.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()
                ?.text

            return if (textResponse != null) {
                textResponse
            } else if (response.promptFeedback?.blockReason != null) {
                // El prompt fue bloqueado por seguridad (ej. contenido de odio)
                "Error: Tu pregunta fue bloqueada por razones de seguridad. (${response.promptFeedback.blockReason})"
            } else {
                "Error: No se recibió una respuesta válida de la IA."
            }

        } catch (e: Exception) {
            // 6. Manejo de errores de red
            e.printStackTrace()
            if (e.message?.contains("HTTP 400") == true) {
                return "Error: La API Key de Gemini es inválida o está mal configurada."
            } else {
                return "Error de red: ${e.message}"
            }
        }
    }
}