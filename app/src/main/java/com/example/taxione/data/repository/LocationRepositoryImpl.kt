package com.example.taxione.data.repository

import com.example.taxione.data.api.GeocodingApi
import com.example.taxione.data.db.CachedLocationDao
import com.example.taxione.data.db.CachedLocationEntity
import com.example.taxione.data.db.quantize
import com.example.taxione.di.BigDataCloudApi
import com.example.taxione.di.BookApi
import com.example.taxione.domain.model.CachedLocation
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class LocationRepositoryImpl @Inject constructor(
    @BigDataCloudApi private val api: GeocodingApi,
    private val dao: CachedLocationDao,
) : LocationRepository {

    override suspend fun getAddress(latLng: LatLng): String {
        val latKey = quantize(latLng.latitude)
        val lngKey = quantize(latLng.longitude)

        dao.find(latKey, lngKey)?.let { return it.address }

        val response = api.reverseGeocode(latLng.latitude, latLng.longitude)
        val address = response.toDisplayAddress()

        dao.insert(
            CachedLocationEntity(
                latKey = latKey,
                lngKey = lngKey,
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                address = address,
                savedAt = System.currentTimeMillis(),
            )
        )
        return address
    }

    override suspend fun updateCachedNickname(latLng: LatLng, nickname: String) {
        val latKey = quantize(latLng.latitude)
        val lngKey = quantize(latLng.longitude)
        dao.updateNickname(latKey, lngKey, nickname)
    }

    override suspend fun updateCachedAqi(latLng: LatLng, aqi: Int) {
        val latKey = quantize(latLng.latitude)
        val lngKey = quantize(latLng.longitude)
        dao.updateAqi(latKey, lngKey, aqi)
    }

    override fun observeCachedLocations(): Flow<List<CachedLocation>> =
        dao.getAllData().map { entities ->
            entities.map { it.toDomain() }
        }

    private fun CachedLocationEntity.toDomain() = CachedLocation(
        latLng = LatLng(latitude, longitude),
        address = address,
        nickname = nickname,
        aqi = aqi,
    )
}
