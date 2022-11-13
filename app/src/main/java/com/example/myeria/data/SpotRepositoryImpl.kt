package com.example.myeria.data


import com.example.myeria.domain.model.Spot
import com.example.myeria.domain.repository.SpotRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SpotRepositoryImpl(private val dao:SpotDao): SpotRepository {
    override suspend fun insertParkingSpot(spot: Spot) {
        spot.toSpotEntity().let { dao.insertParkingSpot(it) }
    }

    override suspend fun deleteParkingSpot(spot: Spot) {
        spot.toSpotEntity().let { dao.deleteParkingSpot(it) }
    }

    override suspend fun updateParkingSpot(onEria:Boolean) {
         dao.updateParkingSpot(onEria)
    }

    override fun getParkingSpot(): Flow<List<Spot>> {
       return dao.getParkingSpots().map{
               spots->spots.map{it.toSpot()}
       }
    }

}