package com.example.replicateai.data.api

import com.example.replicateai.data.model.PredictionResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("models/{modelId}/predictions")
    suspend fun createPrediction(
        @Path("modelId") modelId: String,
        @Body requestBody: Any,
        @Header("Authorization") authToken: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "wait"
    ): Response<PredictionResponse>

    @POST("predictions")
    suspend fun createPredictionDirect(
        @Body requestBody: Any,
        @Header("Authorization") authToken: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Prefer") prefer: String = "wait"
    ): Response<PredictionResponse>

    @GET("predictions/{predictionId}")
    suspend fun getPredictionStatus(
        @Path("predictionId") predictionId: String,
        @Header("Authorization") authToken: String
    ): Response<PredictionResponse>
}
