package com.lexa.app.data.network

import com.lexa.app.data.models.GeminiRequest
import com.lexa.app.data.models.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AIService {

    // 1. Apuntamos al endpoint "generateContent" de Google.
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        // 2. La API Key de Gemini se pasa como un parámetro en la URL
        @Query("key") apiKey: String,

        // 3. El cuerpo (body) de la petición ahora usa nuestro GeminiRequest
        @Body request: GeminiRequest

    ): GeminiResponse // 4. La respuesta que esperamos es un GeminiResponse
}