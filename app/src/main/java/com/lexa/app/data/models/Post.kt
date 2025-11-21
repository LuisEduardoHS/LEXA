package com.lexa.app.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Post(
    @DocumentId
    val id: String = "",

    val author: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val commentCount: Int = 0,

    @ServerTimestamp
    val timestamp: Date? = null
)