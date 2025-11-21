package com.lexa.app.data.di

import com.lexa.app.data.repository.AuthRepository
import com.lexa.app.data.repository.AuthRepositoryImpl
import com.lexa.app.data.repository.ChatRepository
import com.lexa.app.data.repository.ChatRepositoryImpl
import com.lexa.app.data.repository.ForumRepository
import com.lexa.app.data.repository.ForumRepositoryImpl
import com.lexa.app.data.repository.LawyerRepository
import com.lexa.app.data.repository.LawyerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindLawyerRepository(
        impl: LawyerRepositoryImpl
    ): LawyerRepository

    @Binds
    @Singleton
    abstract fun bindForumRepository(
        impl: ForumRepositoryImpl
    ): ForumRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}