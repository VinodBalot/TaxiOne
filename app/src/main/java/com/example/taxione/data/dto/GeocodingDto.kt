package com.example.taxione.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    val localityInfo: LocalityInfoDto? = null,
)

@Serializable
data class LocalityInfoDto(
    val administrative: List<AdministrativeDto> = emptyList(),
)

@Serializable
data class AdministrativeDto(
    val name: String = "",
    val order: Int = 0,
)
