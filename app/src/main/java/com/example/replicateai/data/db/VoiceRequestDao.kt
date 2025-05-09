package com.example.replicateai.data.db

import com.example.replicateai.data.db.entity.VoiceRequest

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface VoiceRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceRequest(voiceRequest: VoiceRequest)

    @Update
    suspend fun updateVoiceRequest(voiceRequest: VoiceRequest)

    @Query("SELECT * FROM voice_requests ORDER BY createdAt DESC")
    fun getAllVoiceRequests(): LiveData<List<VoiceRequest>>

    @Query("SELECT * FROM voice_requests WHERE id = :id")
    suspend fun getVoiceRequestById(id: String): VoiceRequest?

    @Query("SELECT * FROM voice_requests WHERE status = 'processing'")
    suspend fun getPendingRequests(): List<VoiceRequest>
}
