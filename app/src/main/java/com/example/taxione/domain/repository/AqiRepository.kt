package com.example.taxione.domain.repository

import com.example.taxione.domain.model.LatLng

interface AqiRepository {
    suspend fun getAqi(latLng: LatLng): Int
}
