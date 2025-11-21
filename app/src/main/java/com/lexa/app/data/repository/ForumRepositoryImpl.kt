package com.lexa.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.lexa.app.data.models.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ForumRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ForumRepository {
    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        val query = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close (error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val posts = snapshot.toObjects<Post>()
                trySend(posts)
            }
        }
        awaitClose { listener.remove() }
    }
}