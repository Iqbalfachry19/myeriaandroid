package com.example.myeria.data


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingSpot(spot:SpotEntity)

    @Delete
    suspend fun deleteParkingSpot(spot: SpotEntity)
    @Query("UPDATE spotentity SET isOnEria = :onEria WHERE id = 1")
    suspend fun updateParkingSpot(onEria:Boolean)
    @Query("SELECT * FROM spotentity")
    fun getParkingSpots(): Flow<List<SpotEntity>>

}