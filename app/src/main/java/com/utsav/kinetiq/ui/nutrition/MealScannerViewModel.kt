package com.utsav.kinetiq.ui.nutrition

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utsav.kinetiq.data.local.GeminiPrefs
import com.utsav.kinetiq.data.remote.GeminiRepository
import com.utsav.kinetiq.domain.model.MealAnalysisResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface MealScannerUiState {
    object Idle : MealScannerUiState
    object Analyzing : MealScannerUiState
    data class Success(val result: MealAnalysisResult) : MealScannerUiState
    data class Error(val message: String) : MealScannerUiState
}

class MealScannerViewModel(
    private val geminiRepository: GeminiRepository,
    private val geminiPrefs: GeminiPrefs? = null
) : ViewModel() {

    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _uiState = MutableStateFlow<MealScannerUiState>(MealScannerUiState.Idle)
    val uiState: StateFlow<MealScannerUiState> = _uiState.asStateFlow()

    init {
        if (geminiPrefs != null) {
            viewModelScope.launch {
                geminiPrefs.apiKey.collectLatest { key ->
                    if (!key.isNullOrBlank()) {
                        geminiRepository.updateApiKey(key)
                    }
                }
            }
        }
    }

    fun onImageSelected(bitmap: Bitmap?) {
        _selectedBitmap.value = bitmap
        _uiState.value = MealScannerUiState.Idle
    }

    fun analyzeMeal() {
        val bitmap = _selectedBitmap.value ?: return
        viewModelScope.launch {
            _uiState.value = MealScannerUiState.Analyzing
            try {
                val result = geminiRepository.analyzeMealImage(bitmap)
                if (result != null) {
                    _uiState.value = MealScannerUiState.Success(result)
                } else {
                    _uiState.value = MealScannerUiState.Error("Gemini returned empty results. Please check your API key.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = e.localizedMessage ?: e.message ?: "Analysis failed"
                _uiState.value = MealScannerUiState.Error(msg)
            }
        }
    }

    fun analyzeSpokenMeal(spokenText: String) {
        if (spokenText.isBlank()) return
        viewModelScope.launch {
            _uiState.value = MealScannerUiState.Analyzing
            try {
                val result = geminiRepository.analyzeSpokenMeal(spokenText)
                if (result != null) {
                    _uiState.value = MealScannerUiState.Success(result)
                } else {
                    _uiState.value = MealScannerUiState.Error("Could not analyze spoken description with AI.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = MealScannerUiState.Error(e.localizedMessage ?: "Analysis failed")
            }
        }
    }

    fun reset() {
        _selectedBitmap.value = null
        _uiState.value = MealScannerUiState.Idle
    }

    companion object {
        fun provideFactory(
            geminiRepository: GeminiRepository,
            geminiPrefs: GeminiPrefs? = null
        ): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MealScannerViewModel(geminiRepository, geminiPrefs) as T
                }
            }
    }
}
