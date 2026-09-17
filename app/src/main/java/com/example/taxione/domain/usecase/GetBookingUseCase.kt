package com.example.taxione.domain.usecase

import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.repository.BookingRepository
import java.util.Calendar
import javax.inject.Inject

class GetBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(): List<Booking> {
        val cal = Calendar.getInstance()
        return repository.getBookings(year = cal.get(Calendar.YEAR),
            month = cal.get(Calendar.MONTH) + 1)
    }
}