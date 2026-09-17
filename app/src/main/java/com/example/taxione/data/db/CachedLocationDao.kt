package com.example.taxione.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedLocationDao {
    @Query("SELECT * FROM cached_locations WHERE latKey = :latKey AND lngKey = :lngKey LIMIT 1")
    suspend fun find(latKey: Long, lngKey: Long): CachedLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CachedLocationEntity)

    @Query("UPDATE cached_locations SET nickname = :nickname WHERE latKey = :latKey AND lngKey = :lngKey")
    suspend fun updateNickname(latKey: Long, lngKey: Long, nickname: String)

    @Query("UPDATE cached_locations SET aqi = :aqi WHERE latKey = :latKey AND lngKey = :lngKey")
    suspend fun updateAqi(latKey: Long, lngKey: Long, aqi: Int)

    @Query("SELECT * FROM cached_locations ORDER BY savedAt DESC")
    fun getAllData(): Flow<List<CachedLocationEntity>>
}
