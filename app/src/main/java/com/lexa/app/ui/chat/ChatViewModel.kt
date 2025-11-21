package com.lexa.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexa.app.data.local.ChatMessageEntity
import com.lexa.app.data.local.ChatSessionEntity
import com.lexa.app.data.models.Content
import com.lexa.app.data.models.Part
import com.lexa.app.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ROLE_USER = "user"
private const val ROLE_MODEL = "model"

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
    val allSessions: List<ChatSessionEntity> = emptyList(),
    val currentSessionId: Long? = null, // Inicia en null (chat nuevo)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val messagesFlow = _uiState.flatMapLatest { state ->
        if (state.currentSessionId != null) {
            chatRepository.getMessagesForSession(state.currentSessionId)
                .map { entities ->
                    entities.map { ChatMessage(it.text, it.role == ROLE_USER) }
                }
        } else {
            // Si el ID es null (chat nuevo), devuelve una lista vacía
            flowOf(emptyList())
        }
    }

    init {
        // Al iniciar, SÓLO cargamos la lista de chats
        viewModelScope.launch {
            chatRepository.getAllSessions().collect { sessions ->
                _uiState.update { it.copy(allSessions = sessions) }
            }
        }

        viewModelScope.launch {
            messagesFlow.collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    // Función para crear un chat
    fun createNewSession() {
        _uiState.update { it.copy(currentSessionId = null) }
    }

    // Función para cambiar de chat
    fun loadSession(sessionId: Long) {
        _uiState.update { it.copy(currentSessionId = sessionId) }
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank() || _uiState.value.isLoading) return

        viewModelScope.launch {

            val sessionId = if (_uiState.value.currentSessionId == null) {

                val newId = chatRepository.createNewSession(userMessage.take(30) + "...")

                _uiState.update { it.copy(currentSessionId = newId) }
                newId
            } else {
                _uiState.value.currentSessionId!!
            }

            val userMessageEntity = ChatMessageEntity(
                sessionId = sessionId,
                text = userMessage,
                role = ROLE_USER,
                timestamp = System.currentTimeMillis()
            )

            // Guardar mensaje de USUARIO en Room
            chatRepository.saveMessage(userMessageEntity)

            // Activar "Cargando"
            _uiState.update { it.copy(isLoading = true) }

            // Construir historial de la UI
            val uiHistory = messagesFlow.first().map { chatMsg ->
                Content(
                    parts = listOf(Part(text = chatMsg.text)),
                    role = if (chatMsg.isFromUser) ROLE_USER else ROLE_MODEL
                )
            }
            val fullApiHistory = listOf(systemPromptContent) + uiHistory

            // Llama al repositorio (API)
            val response = chatRepository.getApiResponse(fullApiHistory)

            // Crear entidad de MODELO (IA)
            val modelMessageEntity = ChatMessageEntity(
                sessionId = sessionId,
                text = response,
                role = ROLE_MODEL,
                timestamp = System.currentTimeMillis()
            )
            // Guardar respuesta de IA en Room
            chatRepository.saveMessage(modelMessageEntity)

            // Desactivar "Cargando"
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}