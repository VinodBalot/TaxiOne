package com.example.taxione.domain.repository

import com.example.taxione.domain.model.CachedLocation
import com.example.taxione.domain.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getAddress(latLng: LatLng): String
    suspend fun updateCachedNickname(latLng: LatLng, nickname: String)
    suspend fun updateCachedAqi(latLng: LatLng, aqi: Int)
    fun observeCachedLocations(): Flow<List<CachedLocation>>
}
