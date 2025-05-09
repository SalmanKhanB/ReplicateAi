package com.example.replicateai.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.replicateai.data.db.entity.VoiceRequest
import com.example.replicateai.data.repository.VoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val voiceRepository: VoiceRepository
) : ViewModel() {

    val voiceRequests: LiveData<List<VoiceRequest>> = voiceRepository.getAllVoiceRequests()
}