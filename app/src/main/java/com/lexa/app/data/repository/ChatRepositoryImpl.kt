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

    // 1. Obtenemos la API Key de Gemini de forma segura
    private val apiKey = BuildConfig.GEMINI_API_KEY

    override suspend fun getChatResponse(prompt: String): String {

        // 2. Construimos el cuerpo (JSON) que Gemini espera
        // Es una estructura anidada: Request -> Content -> Part
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt)
                    )
                )
            )
        )

        try {
            // 3. Llamamos a la nueva función de nuestra API
            val response = apiService.generateContent(
                apiKey = apiKey,
                request = request
            )

            // 4. Analizamos (parseamos) la respuesta anidada de Gemini
            val textResponse = response.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()
                ?.text

            // 5. Manejamos una respuesta exitosa, una respuesta bloqueada, o un error
            return if (textResponse != null) {
                textResponse // ¡ÉXITO!
            } else if (response.promptFeedback?.blockReason != null) {
                // El prompt fue bloqueado por seguridad (ej. contenido de odio)
                "Error: Tu pregunta fue bloqueada por razones de seguridad. (${response.promptFeedback.blockReason})"
            } else {
                "Error: No se recibió una respuesta válida de la IA."
            }

        } catch (e: Exception) {
            // 6. Manejo de errores de red (ej. sin internet o API key inválida)
            e.printStackTrace()
            // Damos un error más específico si es un 400 (API Key mala)
            if (e.message?.contains("HTTP 400") == true) {
                return "Error: La API Key de Gemini es inválida o está mal configurada."
            } else {
                return "Error de red: ${e.message}"
            }
        }
    }
}