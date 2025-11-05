package com.lexa.app.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChatScreen(
    // 1. Hilt inyectará automáticamente el ViewModel
    viewModel: ChatViewModel = hiltViewModel()
) {
    // 2. Observamos el estado de la UI desde el ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // 3. Estado local para la barra de texto
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    // 4. Estado para autoscroll
    val listState = rememberLazyListState()

    // 5. Efecto para hacer autoscroll cuando llega un mensaje nuevo
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // 6. La lista de mensajes (ahora se alimenta del ViewModel)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            state = listState
        ) {
            // 7. Recorremos la lista de mensajes del ViewModel
            items(uiState.messages) { message ->
                if (message.isFromUser) {
                    UserMessageBubble(text = message.text)
                } else {
                    LexaMessageBubble(text = message.text)
                }
            }

            // 8. Muestra un indicador de carga si la IA está "pensando"
            if (uiState.isLoading) {
                item {
                    LexaMessageBubble(text = "...") // O un indicador de "escribiendo"
                }
            }
        }

        // 9. La barra de input
        ChatInputBar(
            text = textState,
            onTextChanged = { textState = it },
            onSendClick = {
                // 10. ¡Llama al ViewModel para enviar el mensaje!
                viewModel.sendMessage(textState.text)
                // Limpia el texto
                textState = TextFieldValue("")
            }
        )
    }
}