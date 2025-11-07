package com.lexa.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// 1. @Entity le dice a Room que esta data class es una tabla
@Entity(
    tableName = "chat_history",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class ChatMessageEntity (
    // 2. Crea un ID unico
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val sessionId: Long,

    val text: String,
    val role: String,
    val timestamp: Long
)