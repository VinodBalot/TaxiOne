package com.example.taxione.di

import com.example.taxione.data.repository.AqiRepositoryImpl
import com.example.taxione.data.repository.BookingRepositoryImpl
import com.example.taxione.data.repository.LocationRepositoryImpl
import com.example.taxione.domain.repository.AqiRepository
import com.example.taxione.domain.repository.BookingRepository
import com.example.taxione.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAqiRepository(impl: AqiRepositoryImpl): AqiRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(impl: BookingRepositoryImpl): BookingRepository
}
