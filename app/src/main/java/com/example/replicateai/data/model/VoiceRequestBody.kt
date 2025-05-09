package com.example.replicateai.data.model


data class KokoroRequestBody(
    val input: KokoroInput
)
data class SpeechTurboRequestBody(
    val input: SpeechTurboInput
)

// Update SpeechTurboInput to include new parameters
data class SpeechTurboInput(
    val text: String,
    val voice_id: String,
    val emotion: String,
    val language_boost: String,
    val english_normalization: Boolean = true,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val pitch: Int = 0
)

// Update KokoroInput to include speed parameter
data class KokoroInput(
    val text: String,
    val voice: String,
    val speed: Float = 1.0f
)