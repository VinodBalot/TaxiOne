package com.example.taxione.domain.usecase

import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.repository.LocationRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(latLng: LatLng): Result<String> {
        return try {
            val address = locationRepository.getAddress(latLng)
            Result.success(address)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}