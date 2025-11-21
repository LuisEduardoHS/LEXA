package com.lexa.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexa.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthEvent {
    data object AuthSuccess : AuthEvent
    data class AuthError(val message: String) : AuthEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _authEvent = Channel<AuthEvent>()
    val authEvent = _authEvent.receiveAsFlow()

    val userLoggedInFlow = authRepository.getAuthState()

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(email, pass)
            _isLoading.value = false

            if (result.isSuccess) {
                _authEvent.send(AuthEvent.AuthSuccess)
            } else {
                _authEvent.send(AuthEvent.AuthError(result.exceptionOrNull()?.message ?: "Error"))
            }
        }
    }

    fun signUp(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signUp(email, pass)
            _isLoading.value = false

            if (result.isSuccess) {
                _authEvent.send(AuthEvent.AuthSuccess)
            } else {
                _authEvent.send(AuthEvent.AuthError(result.exceptionOrNull()?.message ?: "Error"))
            }
        }
    }
}