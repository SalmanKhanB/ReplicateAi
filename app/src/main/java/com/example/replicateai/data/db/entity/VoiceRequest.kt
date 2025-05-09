package com.example.replicateai.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "voice_requests")
data class VoiceRequest(
    @PrimaryKey val id: String,
    val modelId: String,
    val modelName: String,
    val text: String,
    val voiceId: String,
    val voiceName: String,
    val emotion: String? = null,
    val languageBoost: String? = null,
    val outputUrl: String? = null,
    val status: String,
    val createdAt: Date,
    val error: String? = null
)

