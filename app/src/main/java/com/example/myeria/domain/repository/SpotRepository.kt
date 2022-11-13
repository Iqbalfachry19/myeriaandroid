package com.example.myeria.domain.repository

import androidx.lifecycle.LiveData
import com.example.myeria.domain.model.Spot
import kotlinx.coroutines.flow.Flow

interface SpotRepository {
    suspend fun  insertParkingSpot(spot:Spot)
    suspend fun  deleteParkingSpot(spot: Spot)
    suspend fun  updateParkingSpot(onEria:Boolean)
    fun getParkingSpot(): Flow<List<Spot>>

}