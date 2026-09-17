package com.example.taxione.domain.model

data class CachedLocation(
    val latLng: LatLng,
    val address: String,
    val nickname: String,
    val aqi: Int? = null,
)
