package com.example.taxione.data.repository

import com.example.taxione.BuildConfig
import com.example.taxione.data.api.AqiApi
import com.example.taxione.di.AQIApi
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.repository.AqiRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Qualifier

class AqiRepositoryImpl @Inject constructor(
    @AQIApi private val api: AqiApi,
) : AqiRepository {

    override suspend fun getAqi(latLng: LatLng): Int {
        val response = api.getAqi(
            lat = latLng.latitude,
            lng = latLng.longitude,
            token = BuildConfig.AQICN_TOKEN,
        )
        return response.data?.aqi ?: error("AQI data missing in response")
    }
}
