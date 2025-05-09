package com.example.replicateai.data.repository

import androidx.lifecycle.LiveData
import com.example.replicateai.data.api.ReplicateApi
import com.example.replicateai.data.db.VoiceRequestDao
import com.example.replicateai.data.db.entity.VoiceRequest
import com.example.replicateai.util.Constants
import com.example.replicateai.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class VoiceRepository @Inject constructor(
    private val voiceRequestDao: VoiceRequestDao,
    private val replicateApi: ReplicateApi
) {
    fun getAllVoiceRequests(): LiveData<List<VoiceRequest>> {
        return voiceRequestDao.getAllVoiceRequests()
    }

    suspend fun getVoiceRequestById(id: String): VoiceRequest? {
        return voiceRequestDao.getVoiceRequestById(id)
    }

    suspend fun generateVoice(
        modelId: String,
        modelName: String,
        text: String,
        voiceId: String,
        voiceName: String,
        emotion: String? = null,
        languageBoost: String? = null,
        speed: Float,
        volume: Float?,
        pitch: Int?
    ): Resource<VoiceRequest> = withContext(Dispatchers.IO) {
        try {
//            val response = when (modelId) {
//                Constants.MODEL_KOKORO -> replicateApi.generateKokoroVoice(text, voiceId)
//                Constants.MODEL_SPEECH_TURBO -> replicateApi.generateSpeechTurboVoice(
//                    text,
//                    voiceId,
//                    emotion ?: "neutral",
//                    languageBoost ?: "English"
//                )
//                else -> throw IllegalArgumentException("Unsupported model ID: $modelId")
//            }
            val response = when (modelId) {
                Constants.MODEL_SPEECH_TURBO -> {
                    replicateApi.generateSpeechTurboVoice(
                        text = text,
                        voiceId = voiceId,
                        emotion = emotion ?: "auto",
                        languageBoost = languageBoost ?: "English",
                        speed = speed,
                        volume = volume ?: 1.0f,
                        pitch = pitch ?: 0
                    )
                }
                Constants.MODEL_KOKORO -> {
                    replicateApi.generateKokoroVoice(
                        text = text,
                        voiceId = voiceId,
                        speed = speed
                    )
                }
                else -> throw IllegalArgumentException("Unknown model ID: $modelId")
            }


            if (response.isSuccessful) {
                val prediction = response.body()!!

                val voiceRequest = VoiceRequest(
                    id = prediction.id,
                    modelId = modelId,
                    modelName = modelName,
                    text = text,
                    voiceId = voiceId,
                    voiceName = voiceName,
                    emotion = emotion,
                    languageBoost = languageBoost,
                    outputUrl = prediction.output,
                    status = prediction.status,
                    createdAt = Date(),
                    error = prediction.error
                )

                voiceRequestDao.insertVoiceRequest(voiceRequest)
                Resource.Success(voiceRequest)
            } else {
                Resource.Error("Failed to generate voice: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error generating voice: ${e.message}")
        }
    }

    suspend fun checkPredictionStatus(requestId: String): Resource<VoiceRequest> = withContext(Dispatchers.IO) {
        try {
            val voiceRequest = voiceRequestDao.getVoiceRequestById(requestId)
                ?: return@withContext Resource.Error("Request not found")

            val response = replicateApi.checkPredictionStatus(requestId)

            if (response.isSuccessful) {
                val prediction = response.body()!!

                val updatedRequest = voiceRequest.copy(
                    status = prediction.status,
                    outputUrl = prediction.output,
                    error = prediction.error
                )

                voiceRequestDao.updateVoiceRequest(updatedRequest)
                Resource.Success(updatedRequest)
            } else {
                Resource.Error("Failed to check status: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error checking status: ${e.message}")
        }
    }

    suspend fun getPendingRequests(): List<VoiceRequest> {
        return withContext(Dispatchers.IO) {
            voiceRequestDao.getPendingRequests()
        }
    }
}