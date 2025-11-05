package com.lexa.app.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun ChatScreen(){
    // 1. Estados para manejar el texto del input
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    // 2. Estructura principal de la pantalla
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        // 3. Lista de mensajes
        LazyColumn (
            modifier = Modifier.weight(1f)
        ) {
            // 4. Datos de prueba
            item {
                LexaMessageBubble(
                    text = "¡Hola! Soy Lexa, tu asistente legal. ¿En qué puedo ayudarte hoy?"
                )
            }
            item {
                UserMessageBubble(
                    text = "Hola Lexa, tengo una duda sobre mi contrato de arrendamiento."
                )
            }
            item {
                LexaMessageBubble(
                    text = "Claro, con gusto. ¿Cuál es tu duda específica sobre el contrato?"
                )
            }
        }

        // 5. Espaciador
        Spacer(modifier = Modifier.weight(0.01f))

        // 6. Barra de input
        ChatInputBar(
            text = textState,
            onTextChanged = {
                textState = it
            },
            onSendClick = {
                textState = TextFieldValue("") // Por ahora solo limpia el texto
            }
        )
    }
}