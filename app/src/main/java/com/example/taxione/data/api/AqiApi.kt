package com.example.taxione.data.api

import com.example.taxione.data.dto.AqiResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AqiApi {
    @GET("feed/geo:{lat};{lng}/")
    suspend fun getAqi(
        @Path("lat") lat: Double,
        @Path("lng") lng: Double,
        @Query("token") token: String,
    ): AqiResponseDto
}
