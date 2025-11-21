package com.lexa.app.data.repository

import com.lexa.app.data.models.Post
import kotlinx.coroutines.flow.Flow

interface ForumRepository {
    fun getPosts(): Flow<List<Post>>

    suspend fun createPost(content: String, author: String): Result<Unit>
}