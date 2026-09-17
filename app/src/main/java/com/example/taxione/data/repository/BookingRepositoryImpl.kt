package com.example.taxione.data.repository

import com.example.taxione.data.api.BooksApi
import com.example.taxione.data.dto.BookRequestDto
import com.example.taxione.data.dto.LocationDto
import com.example.taxione.di.BookApi
import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.domain.repository.BookingRepository
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    @BookApi private val api: BooksApi,
) : BookingRepository {

    override suspend fun createBooking(
        locationA: SelectedLocation,
        locationB: SelectedLocation,
    ): Booking {

        val locationADto = LocationDto(
            latitude = locationA.latLng.latitude,
            longitude = locationA.latLng.longitude,
            name = locationA.address,
            aqi = locationA.aqi ?: 0
        )
        val locationBDto = LocationDto(
            latitude = locationB.latLng.latitude,
            longitude = locationB.latLng.longitude,
            name = locationB.address,
            aqi = locationB.aqi ?: 0
        )
        val response = api.createBooking(BookRequestDto(locationADto, locationBDto))
        return Booking(
            id = response.id,
            locationA = SelectedLocation(
                latLng = LatLng(response.a.latitude, response.a.longitude),
                aqi = locationA.aqi,
                address = response.a.name,
                nickname = locationA.nickname,
            ),
            locationB = SelectedLocation(
                latLng = LatLng(response.b.latitude, response.b.longitude),
                aqi = locationB.aqi,
                address = response.b.name,
                nickname = locationB.nickname,
            ),
            price = response.price,
            createdAt = response.createdAt,
        )
    }

    override suspend fun getBookings(year: Int, month: Int): List<Booking> {
        val response = api.getBookings(year, month)
        return response.bookings.map { dto ->
            Booking(
                id = dto.id,
                locationA = SelectedLocation(
                    latLng = LatLng(dto.a.latitude, dto.a.longitude),
                    aqi = null,
                    address = dto.a.name,
                ),
                locationB = SelectedLocation(
                    latLng = LatLng(dto.b.latitude, dto.b.longitude),
                    aqi = null,
                    address = dto.b.name,
                ),
                price = dto.price,
                createdAt = dto.createdAt,
            )
        }
    }
}
