package com.lexa.app.data.network

import com.lexa.app.data.models.HuggingFaceRequest
import com.lexa.app.data.models.HuggingFaceResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AIService {
    // 1. Hacemos un POST a la ruta del modelo que usaremos
    @POST("models/google/gemma-7b-it")
    suspend fun getChatResponse(
        // 2. El token
        @Header("Authorization") token: String,
        @Body request: HuggingFaceRequest
    ): List<HuggingFaceRequest>
}