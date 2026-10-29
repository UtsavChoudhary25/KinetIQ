package com.utsav.kinetiq.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utsav.kinetiq.data.remote.FirebaseAuthRepositoryImpl
import com.utsav.kinetiq.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class AuthEvent {
    object Success : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

class AuthViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
) : ViewModel() {

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(email, password)
            _isLoading.value = false
            result.onSuccess {
                _authEvent.emit(AuthEvent.Success)
            }.onFailure {
                _authEvent.emit(AuthEvent.Error(it.message ?: "Login failed"))
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signUp(email, password, name)
            _isLoading.value = false
            result.onSuccess {
                _authEvent.emit(AuthEvent.Success)
            }.onFailure {
                _authEvent.emit(AuthEvent.Error(it.message ?: "Sign up failed"))
            }
        }
    }
}
