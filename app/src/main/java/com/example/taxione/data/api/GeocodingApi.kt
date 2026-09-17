package com.example.taxione.data.api

import com.example.taxione.data.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("data/reverse-geocode-client")
    suspend fun reverseGeocode(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") localityLanguage: String = "en",
    ): GeocodingResponseDto
}
