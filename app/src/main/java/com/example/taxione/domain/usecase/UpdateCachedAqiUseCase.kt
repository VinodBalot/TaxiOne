package com.example.taxione.domain.usecase

import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateCachedAqiUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(latLng: LatLng, aqi: Int): Result<Unit> {
        return try {
            locationRepository.updateCachedAqi(latLng, aqi)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}