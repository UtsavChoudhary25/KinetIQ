package com.utsav.kinetiq.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceCoachHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isReady = true
            }
        }
    }

    fun speak(text: String) {
        if (isReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kinetiq_coach_voice")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
