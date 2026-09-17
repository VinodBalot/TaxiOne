package com.example.taxione.data.db

import androidx.room.Entity

@Entity(tableName = "cached_locations", primaryKeys = ["latKey", "lngKey"])
data class CachedLocationEntity(
    val latKey: Long,
    val lngKey: Long,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val nickname: String = "",
    val aqi: Int? = null,
    val savedAt: Long,
)
