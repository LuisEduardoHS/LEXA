package com.lexa.app.data.di

import android.content.Context
import androidx.room.Insert
import androidx.room.Room
import com.lexa.app.data.local.ChatDao
import com.lexa.app.data.local.LexaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideLexaDatabase(
        @ApplicationContext context: Context
    ) : LexaDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = LexaDatabase::class.java,
            name = "lexa_app.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: LexaDatabase): ChatDao {
        return database.chatDao()
    }
}