package com.example.taxione.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AqiResponseDto(
    val status: String,
    val data: AqiDataDto? = null,
)

@Serializable
data class AqiDataDto(
    val aqi: Int,
)
