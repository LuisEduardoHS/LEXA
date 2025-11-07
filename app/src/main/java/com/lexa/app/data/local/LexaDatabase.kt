package com.lexa.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChatSessionEntity::class, ChatMessageEntity::class],
    version = 2
)
abstract class LexaDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}