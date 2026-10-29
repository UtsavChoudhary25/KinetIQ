package com.utsav.kinetiq.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.utsav.kinetiq.domain.model.WorkoutSession
import kotlinx.coroutines.tasks.await

class FirestoreWorkoutRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    companion object {
        private const val SESSIONS_COLLECTION = "workout_sessions"
    }

    suspend fun syncSession(userId: String, session: WorkoutSession): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection(SESSIONS_COLLECTION)
                .document(session.id.ifEmpty { System.currentTimeMillis().toString() })
                .set(session)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
