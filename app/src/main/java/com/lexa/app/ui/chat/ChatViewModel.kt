package com.lexa.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexa.app.data.local.ChatMessageEntity
import com.lexa.app.data.models.Content
import com.lexa.app.data.models.Part
import com.lexa.app.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Roles para la API de Gemini
private const val ROLE_USER = "user"
private const val ROLE_MODEL = "model"

// "Cerebro" de Lexa (System Prompt)
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

// Estado de la UI
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

// Modelo de datos para la UI
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        // Lógica de Room
        viewModelScope.launch {
            chatRepository.getChatHistory()
                .map { entities ->
                    entities.map { entity ->
                        ChatMessage(
                            text = entity.text,
                            isFromUser = entity.role == ROLE_USER
                        )
                    }
                }
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
        }
    }

    // Función de envío
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // 1. Crear entidad de USUARIO
        val userMessageUi = ChatMessage(text = userMessage, isFromUser = true) // <-- Modelo de UI
        val userMessageEntity = ChatMessageEntity( // <-- Modelo de DB
            text = userMessage,
            role = ROLE_USER,
            timestamp = System.currentTimeMillis()
        )

        // 2. Actualiza la UI INMEDIATAMENTE con el mensaje del usuario
        _uiState.update {
            it.copy(
                isLoading = true,
                messages = it.messages + userMessageUi
            )
        }

        viewModelScope.launch {
            // 3. Guardar mensaje de USUARIO en Room
            chatRepository.saveMessage(userMessageEntity)

            // 4. Construir historial de la UI
            val uiHistory = _uiState.value.messages.map { chatMsg ->
                Content(
                    parts = listOf(Part(text = chatMsg.text)),
                    role = if (chatMsg.isFromUser) ROLE_USER else ROLE_MODEL
                )
            }

            // 5. Combinar el "cerebro" + el historial
            val fullApiHistory = listOf(systemPromptContent) + uiHistory

            // 6. Llama al repositorio con el historial COMPLETO
            val response = chatRepository.getChatResponse(fullApiHistory)

            // 7. Crear entidad de MODELO
            val modelMessageEntity = ChatMessageEntity(
                text = response,
                role = ROLE_MODEL,
                timestamp = System.currentTimeMillis()
            )
            // 8. Guardar respuesta de IA en Room
            chatRepository.saveMessage(modelMessageEntity)

            // 9. Desactivar "Cargando"
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}