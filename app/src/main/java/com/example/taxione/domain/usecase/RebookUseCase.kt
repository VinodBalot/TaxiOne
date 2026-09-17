package com.example.taxione.domain.usecase

import com.example.taxione.domain.SelectionStore
import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.repository.AqiRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RebookUseCase @Inject constructor(
    private val store: SelectionStore,
    private val aqiRepository: AqiRepository,
) {
    suspend operator fun invoke(booking: Booking) = coroutineScope {
        val aqiADeferred = async { runCatching { aqiRepository.getAqi(booking.locationA.latLng) } }
        val aqiBDeferred = async { runCatching { aqiRepository.getAqi(booking.locationB.latLng) } }

        val aqiAResult = aqiADeferred.await()
        val aqiBResult = aqiBDeferred.await()

        // Atomic: both must succeed; if either fails, propagate the exception
        val aqiA = aqiAResult.getOrThrow()
        val aqiB = aqiBResult.getOrThrow()

        store.setBothSlots(
            locationA = booking.locationA.copy(aqi = aqiA),
            locationB = booking.locationB.copy(aqi = aqiB),
        )
    }
}
