package com.lexa.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexa.app.data.repository.ChatRepository
import com.lexa.app.ui.navigation.AppScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.lexa.app.data.models.Content
import com.lexa.app.data.models.Part

private const val ROLE_USER = "user"
private const val ROLE_MODEL = "model"

// 1. Estado de la UI: Una lista de mensajes
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

// 2. Modulo de datos para la UI
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

// 3. Le decimos a Hilt que esto es un ViewModel
@HiltViewModel
class ChatViewModel @Inject constructor(
    // 4. Hilt inyectara el repositorio que definimos en RepositoryModule
    private val chatRepository: ChatRepository
) : ViewModel() {
    // 5. El prompt que daremos a la IA
    private val systemPromptContent = Content(
        parts = listOf(Part(text = """
            Eres Lexa, una asistente legal experta en leyes mexicanas. 
            Tu propósito es dar orientación clara y fácil de entender.

            REGLAS ESTRICTAS:
            1. Tu audiencia principal son ciudadanos en México. ASUME siempre 
               que las preguntas legales se refieren al sistema legal mexicano. 
               NO pidas confirmación de ubicación (ej. "No preguntes '¿Estás en México?').
            2. Eres amable, profesional y tus respuestas son concisas.
            3. Cuando sea relevante y estés segura, CITA la ley o artículo específico 
               (ejemplo: 'Según el Artículo 87 de la Ley Federal del Trabajo...').
            4. NUNCA inventes artículos o leyes. Es mejor ser general que incorrecta.
            5. NUNCA das consejos financieros, solo orientación legal.
        """.trimIndent())),
        role = ROLE_MODEL
    )

    // 6. El estado (StateFlow) que la UI observara
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        // 7. Agrega el mensaje inicial
        _uiState.update {
            it.copy(
                messages = listOf(
                    ChatMessage(
                        text = "¡Hola! Soy Lexa, tu asistente legal. ¿En qué puedo ayudarte hoy?",
                        isFromUser = false
                    )
                )
            )
        }
    }

    // 8. La funcion que la UI llamara para enviar un mensaje
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        _uiState.update {
            it.copy(
                isLoading = true,
                messages = it.messages + ChatMessage(text = userMessage, isFromUser = true)
            )
        }

        viewModelScope.launch {
            val uiHistory = _uiState.value.messages.map { chatMsg ->
                Content(
                    parts = listOf(Part(text = chatMsg.text)),
                    role = if (chatMsg.isFromUser) ROLE_USER else ROLE_MODEL
                )
            }
            val fullApiHistory = listOf(systemPromptContent) + uiHistory
            val response = chatRepository.getChatResponse(fullApiHistory)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    messages = it.messages + ChatMessage(text = response, isFromUser = false)
                )
            }
        }
    }

}