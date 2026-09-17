package com.example.taxione.domain.model

data class Booking(
    val id: String,
    val locationA: SelectedLocation,
    val locationB: SelectedLocation,
    val price: Double,
    val createdAt: Long,
)