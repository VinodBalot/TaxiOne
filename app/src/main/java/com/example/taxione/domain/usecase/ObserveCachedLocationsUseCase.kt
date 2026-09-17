package com.example.taxione.domain.usecase

import com.example.taxione.domain.model.CachedLocation
import com.example.taxione.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCachedLocationsUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<List<CachedLocation>> {
        return locationRepository.observeCachedLocations()
    }
}