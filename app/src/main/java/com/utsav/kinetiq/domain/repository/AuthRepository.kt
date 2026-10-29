package com.utsav.kinetiq.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, name: String): Result<Unit>
    suspend fun signOut()
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}
