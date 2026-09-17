package com.example.taxione.domain.model


data class SelectedLocation(
    val latLng: LatLng,
    val aqi: Int?,
    val address: String,
    val nickname: String = "",
) {
    val displayName: String get() = nickname.trim().ifEmpty { address }
}