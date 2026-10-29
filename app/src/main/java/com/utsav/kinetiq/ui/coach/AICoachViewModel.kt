package com.utsav.kinetiq.ui.coach

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utsav.kinetiq.data.local.GeminiPrefs
import com.utsav.kinetiq.data.remote.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AICoachViewModel(
    private val geminiRepository: GeminiRepository,
    private val geminiPrefs: GeminiPrefs
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Hello! I'm your KINETIQ AI Coach. How can I help you reach your fitness goals today?", false)
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _isApiKeyMissing = mutableStateOf(false)
    val isApiKeyMissing: State<Boolean> = _isApiKeyMissing

    init {
        viewModelScope.launch {
            geminiPrefs.apiKey.collectLatest { key ->
                if (!key.isNullOrBlank()) {
                    _isApiKeyMissing.value = false
                    geminiRepository.updateApiKey(key)
                } else {
                    _isApiKeyMissing.value = true
                }
            }
        }
    }

    fun updateApiKey(key: String) {
        viewModelScope.launch {
            geminiPrefs.saveApiKey(key)
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        if (_isApiKeyMissing.value) {
            _messages.value = _messages.value + ChatMessage("Please set your Gemini API Key in settings first.", false)
            return
        }

        val userMessage = ChatMessage(text, true)
        _messages.value = _messages.value + userMessage
        
        viewModelScope.launch {
            _isLoading.value = true
            val response = geminiRepository.getCoachResponse(text)
            _messages.value = _messages.value + ChatMessage(response, false)
            _isLoading.value = false
        }
    }

    companion object {
        fun provideFactory(
            geminiRepository: GeminiRepository,
            geminiPrefs: GeminiPrefs
        ): androidx.lifecycle.ViewModelProvider.Factory = 
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AICoachViewModel(geminiRepository, geminiPrefs) as T
                }
            }
    }
}
