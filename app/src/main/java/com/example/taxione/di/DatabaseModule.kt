package com.example.taxione.di

import android.content.Context
import androidx.room.Room
import com.example.taxione.data.db.CachedLocationDao
import com.example.taxione.data.db.TaxiOneDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaxiOneDatabase =
        Room.databaseBuilder(context, TaxiOneDatabase::class.java, "taxiOne.db")
            .build()

    @Provides
    fun provideCachedLocationDao(db: TaxiOneDatabase): CachedLocationDao =
        db.cachedLocationDao()
}
