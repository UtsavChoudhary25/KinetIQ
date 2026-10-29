package com.utsav.kinetiq.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.utsav.kinetiq.domain.model.UserProfile
import com.utsav.kinetiq.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreUserRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    override suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(userProfile.id)
                .set(userProfile)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            Result.success(snapshot.toObject<UserProfile>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val listener = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                try {
                    trySend(snapshot?.toObject<UserProfile>())
                } catch (e: Exception) {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }
}
