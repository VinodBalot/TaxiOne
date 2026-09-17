package com.example.taxione.data.mock

import com.example.taxione.data.dto.BookRequestDto
import com.example.taxione.data.dto.BookingListResponseDto
import com.example.taxione.data.dto.BookingResponseDto
import kotlinx.serialization.json.Json
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * In-memory mock for the /books endpoint.
 *
 * Storage: LinkedHashMap(capacity=50, accessOrder=true) gives LRU semantics —
 * access-ordered means reads count as use, so recently-read bookings stay alive
 * longer than merely recently-written ones. The 51st insert evicts the entry
 * that was least recently *accessed*, not created.
 */
@Singleton
class FakeBooksServer @Inject constructor() {

    private var nextId = 1

    private val store: LinkedHashMap<String, BookingResponseDto> =
        object : LinkedHashMap<String, BookingResponseDto>(50, 0.75f, true) {
            override fun removeEldestEntry(eldest: Map.Entry<String, BookingResponseDto>): Boolean =
                size > 50
        }

    @Synchronized
    fun createBooking(request: BookRequestDto): String {
        val id = nextId++.toString()
        val price = haversineKm(
            lat1 = request.a.latitude, lng1 = request.a.longitude,
            lat2 = request.b.latitude, lng2 = request.b.longitude,
        )  * PRICE_PER_KM
        val dto = BookingResponseDto(
            id = id,
            a = request.a,
            b = request.b,
            price = price,
            createdAt = System.currentTimeMillis(),
        )
        store[id] = dto
        return Json.encodeToString(dto)
    }

    @Synchronized
    fun getBookings(year: Int, month: Int): String {
        val list = store.values.filter { dto ->
            val cal = Calendar.getInstance().apply { timeInMillis = dto.createdAt }
            cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 == month
        }.toList()
        return Json.encodeToString(BookingListResponseDto(list))
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    companion object {
        private const val PRICE_PER_KM = 2.5
    }
}
