package com.example.taxione.domain.usecase

import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateCachedNicknameUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(latLng: LatLng, nickname: String): Result<Unit> {
        return try {
            locationRepository.updateCachedNickname(latLng, nickname)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}