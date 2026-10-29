package com.utsav.kinetiq.domain.repository

import com.utsav.kinetiq.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit>
    suspend fun getUserProfile(userId: String): Result<UserProfile?>
    fun observeUserProfile(userId: String): Flow<UserProfile?>
}
