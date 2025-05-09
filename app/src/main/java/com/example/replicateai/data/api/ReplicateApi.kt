package com.example.replicateai.data.api

import com.example.replicateai.data.model.*
import com.example.replicateai.util.Constants
import retrofit2.Response
import javax.inject.Inject

class ReplicateApi @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun generateKokoroVoice(
        text: String,
        voiceId: String,
        speed: Float = 1.0f
    ): Response<PredictionResponse> {
        val requestBody = mapOf(
            "version" to Constants.KOKORO_VERSION,
            "input" to KokoroInput(
                text = text,
                voice = voiceId,
                speed = speed
            )
        )
        return apiService.createPredictionDirect(
            requestBody = requestBody,
            authToken = Constants.AUTH_TOKEN
        )
    }

    suspend fun generateSpeechTurboVoice(
        text: String,
        voiceId: String,
        emotion: String,
        languageBoost: String,
        speed: Float = 1.0f,
        volume: Float = 1.0f,
        pitch: Int = 0,
        englishNormalization: Boolean = true
    ): Response<PredictionResponse> {
        val requestBody = SpeechTurboRequestBody(
            input = SpeechTurboInput(
                text = text,
                voice_id = voiceId,
                emotion = emotion,
                language_boost = languageBoost,
                english_normalization = englishNormalization,
                speed = speed,
                volume = volume,
                pitch = pitch
            )
        )
        return apiService.createPrediction(
            modelId = Constants.MODEL_SPEECH_TURBO,
            requestBody = requestBody,
            authToken = Constants.AUTH_TOKEN
        )
    }

    suspend fun checkPredictionStatus(predictionId: String): Response<PredictionResponse> {
        return apiService.getPredictionStatus(
            predictionId = predictionId,
            authToken = Constants.AUTH_TOKEN
        )
    }
}
