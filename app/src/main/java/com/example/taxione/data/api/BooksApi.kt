package com.example.taxione.data.api

import com.example.taxione.data.dto.BookRequestDto
import com.example.taxione.data.dto.BookingListResponseDto
import com.example.taxione.data.dto.BookingResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BooksApi {
    @POST("books")
    suspend fun createBooking(@Body request: BookRequestDto): BookingResponseDto

    @GET("books")
    suspend fun getBookings(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): BookingListResponseDto
}
