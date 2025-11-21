package com.lexa.app.ui.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexa.app.data.models.Post
import com.lexa.app.data.repository.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ForumUiState {
    data object Loading : ForumUiState
    data class Success(val posts: List<Post>) : ForumUiState
    data class Error(val message: String) : ForumUiState
}

sealed interface ForumEvent {
    data object PostCreated : ForumEvent
    data class PostError(val error: String) : ForumEvent
}

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val repository: ForumRepository
) : ViewModel() {
    val uiState: StateFlow<ForumUiState> = repository.getPosts()
        .map { ForumUiState.Success(it) as ForumUiState }
        .catch { emit(ForumUiState.Error(it.message ?: "Error desconocido")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ForumUiState.Loading
        )

    private val _events = Channel<ForumEvent>()
    val events = _events.receiveAsFlow()

    fun createPost(content: String) {
        viewModelScope.launch {
            val authorName = "Usuario Anonimo"

            val result = repository.createPost(content, authorName)

            if (result.isSuccess) {
                _events.send(ForumEvent.PostCreated)
            } else {
                _events.send(ForumEvent.PostError("Error al publicar"))
            }
        }
    }
}