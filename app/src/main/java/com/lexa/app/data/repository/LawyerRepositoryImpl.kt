package com.lexa.app.data.repository

import androidx.room.Insert
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.lexa.app.data.models.Lawyer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

// Inyectamos Firestore
class LawyerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LawyerRepository {
    override fun getAllLawyers(): Flow<List<Lawyer>> {
        return callbackFlow {
            val listener = firestore.collection("lawyers")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val lawyers = snapshot.toObjects<Lawyer>()
                        trySend(lawyers)
                    }
                }

            awaitClose { listener.remove() }
        }
    }
}