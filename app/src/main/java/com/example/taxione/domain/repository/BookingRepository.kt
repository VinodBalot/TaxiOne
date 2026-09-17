package com.example.taxione.domain.repository

import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.model.SelectedLocation

interface BookingRepository {
    suspend fun createBooking(locationA: SelectedLocation, locationB: SelectedLocation): Booking
    suspend fun getBookings(year: Int, month: Int): List<Booking>
}
