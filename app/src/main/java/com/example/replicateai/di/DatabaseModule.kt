package com.example.replicateai.di
import com.example.replicateai.data.api.ApiService
import com.example.replicateai.data.api.ReplicateApi
import com.example.replicateai.data.api.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService = RetrofitClient.retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideReplicateApi(apiService: ApiService): ReplicateApi = ReplicateApi(apiService)
}

