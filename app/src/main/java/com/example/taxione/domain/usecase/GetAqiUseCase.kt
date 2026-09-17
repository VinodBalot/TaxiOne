package com.example.taxione.domain.usecase

import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.repository.AqiRepository
import javax.inject.Inject

class GetAqiUseCase @Inject constructor(
    private val repository: AqiRepository
) {

    suspend operator fun invoke(latLng: LatLng): Result<Int> {
        return try {
            Result.success(repository.getAqi(latLng))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}