package com.example.taxione.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BookRequestDto(
    @SerialName("a")
    val a: LocationDto,
    @SerialName("b")
    val b: LocationDto
)
@Serializable
data class LocationDto(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("aqi")
    val aqi: Int,
    @SerialName("name")
    val name: String
)


@Serializable
data class BookingResponseDto(
    val id: String,
    @SerialName("a")
    val a: LocationDto,
    @SerialName("b")
    val b: LocationDto,
    val price: Double,
    @SerialName("created_at") val createdAt: Long,
)

@Serializable
data class BookingListResponseDto(
    val bookings: List<BookingResponseDto>,
)
