package com.lexa.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // Inserta una NUEVA sesión de chat y nos devuelve su ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity): Long // Devuelve el nuevo ID

    // Obtiene TODAS las sesiones
    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    // Borra una sesión
    @Query("DELETE FROM chat_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: Long)


    // Inserta un mensaje
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    // Obtiene solo los mensajes de UNA sesión específica
    @Query("SELECT * FROM chat_history WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>>

    // Borra todos los mensajes
    @Query("DELETE FROM chat_history")
    suspend fun clearHistory() // Esta ya no es tan útil
}