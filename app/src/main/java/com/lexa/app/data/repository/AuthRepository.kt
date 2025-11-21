package com.lexa.app.data.repository
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>

    fun logout()

    fun getCurrentUserEmail(): String?
    fun isUserLoggedIn(): Boolean

    fun getAuthState(): Flow<Boolean>

}