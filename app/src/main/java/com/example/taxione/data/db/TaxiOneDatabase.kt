package com.example.taxione.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedLocationEntity::class], version = 1, exportSchema = false)
abstract class TaxiOneDatabase : RoomDatabase() {
    abstract fun cachedLocationDao(): CachedLocationDao
}
