package com.example.replicateai.ui.generator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.replicateai.data.db.entity.VoiceRequest
import com.example.replicateai.data.repository.VoiceRepository
import com.example.replicateai.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    private val voiceRepository: VoiceRepository
) : ViewModel() {

    private val _voiceRequest = MutableLiveData<Resource<VoiceRequest>>()
    val voiceRequest: LiveData<Resource<VoiceRequest>> = _voiceRequest

    fun generateVoice(
        modelId: String,
        modelName: String,
        text: String,
        voiceId: String,
        voiceName: String,
        emotion: String?,
        languageBoost: String?,
        speed: Float = 1.0f,
        volume: Float? = null,
        pitch: Int? = null
    ) {
        viewModelScope.launch {
            _voiceRequest.value = Resource.Loading()

            val result = voiceRepository.generateVoice(
                modelId = modelId,
                modelName = modelName,
                text = text,
                voiceId = voiceId,
                voiceName = voiceName,
                emotion = emotion,
                languageBoost = languageBoost,
                speed = speed,
                volume = volume,
                pitch = pitch
            )

            if (result is Resource.Success) {
                var request = result.data!!
                var attempt = 0

                while (request.status !in listOf("succeeded", "failed") && attempt < 30) {
                    delay(2000)
                    val statusResult = voiceRepository.checkPredictionStatus(request.id)
                    if (statusResult is Resource.Success) {
                        request = statusResult.data!!
                    }
                    attempt++
                }

                _voiceRequest.value = Resource.Success(request)
            } else {
                _voiceRequest.value = result
            }
        }
  }
    fun getVoiceRequestById(id: String) {
        viewModelScope.launch {
            _voiceRequest.value = Resource.Loading()

            try {
                val request = voiceRepository.getVoiceRequestById(id)
                if (request != null) {
                    _voiceRequest.value = Resource.Success(request)
                } else {
                    _voiceRequest.value = Resource.Error("Request not found")
                }
            } catch (e: Exception) {
                _voiceRequest.value = Resource.Error("Error loading request: ${e.message}")
            }
        }
    }
}