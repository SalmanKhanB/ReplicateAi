package com.example.replicateai.data.model


data class PredictionResponse(
    val id: String,
    val model: String,
    val version: String?,
    val input: Map<String, Any>,
    val logs: String?,
    val output: String?,
    val data_removed: Boolean,
    val error: String?,
    val status: String,
    val created_at: String,
    val urls: Urls
)

data class Urls(
    val cancel: String,
    val get: String,
    val stream: String
)

