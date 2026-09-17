package com.example.taxione.domain.usecase

import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.domain.repository.BookingRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(
        locationA: SelectedLocation?,
        locationB: SelectedLocation?
    ): Result<Booking> {
        if (locationA == null || locationB == null) {
            return Result.failure(IllegalArgumentException("Both pickup and drop-off locations are required."))
        }
        return try {
            val booking = bookingRepository.createBooking(locationA, locationB)
            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}