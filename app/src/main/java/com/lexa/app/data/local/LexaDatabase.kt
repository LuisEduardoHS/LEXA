package com.lexa.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChatMessageEntity::class],
    version = 1
)
abstract class LexaDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}