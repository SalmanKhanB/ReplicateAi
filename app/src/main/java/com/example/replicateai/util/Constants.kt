package com.example.replicateai.util

import com.example.replicateai.BuildConfig


object Constants {
    const val BASE_URL = "https://api.replicate.com/v1/"
    const val AUTH_TOKEN = "Bearer ${BuildConfig.API_KEY}"


    // Model IDs
    const val MODEL_KOKORO = "jaaari/kokoro-82m"
    const val MODEL_SPEECH_TURBO = "minimax/speech-02-turbo"

    // Database
    const val DATABASE_NAME = "aivoice_database"
    const val KOKORO_VERSION = "f559560eb822dc509045f3921a1921234918b91739db4bf3daab2169b71c7a13"

    // WorkManager
    const val VOICE_GENERATION_WORK = "voice_generation_work"
    const val KEY_REQUEST_ID = "key_request_id"
    const val KEY_MODEL_ID = "key_model_id"
    const val KEY_TEXT = "key_text"
    const val KEY_VOICE_ID = "key_voice_id"
    const val KEY_EMOTION = "key_emotion"
    const val KEY_LANGUAGE_BOOST = "key_language_boost"
}