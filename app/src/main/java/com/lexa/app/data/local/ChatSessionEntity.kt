package com.lexa.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,
    val title: String,
    val timestamp: Long
)
