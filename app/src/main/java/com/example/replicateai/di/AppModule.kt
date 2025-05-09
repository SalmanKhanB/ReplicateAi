package com.example.replicateai.di

import android.content.Context
import com.example.replicateai.data.api.ReplicateApi
import com.example.replicateai.data.db.AppDatabase
import com.example.replicateai.data.db.VoiceRequestDao
import com.example.replicateai.data.repository.VoiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = AppDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideVoiceRequestDao(database: AppDatabase) = database.voiceRequestDao()

    @Provides
    @Singleton
    fun provideVoiceRepository(
        voiceRequestDao: VoiceRequestDao,
        replicateApi: ReplicateApi
    ) = VoiceRepository(voiceRequestDao, replicateApi)
}
