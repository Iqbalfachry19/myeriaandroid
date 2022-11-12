package com.example.myeria.data


import com.example.myeria.domain.model.Spot
import com.example.myeria.domain.repository.SpotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SpotRepositoryImpl(private val dao:SpotDao): SpotRepository {
    override suspend fun insertParkingSpot(spot: Spot) {
      dao.insertParkingSpot(spot.toSpotEntity())
    }

    override suspend fun deleteParkingSpot(spot: Spot) {
        dao.deleteParkingSpot(spot.toSpotEntity())
    }

    override fun getParkingSpots(): Flow<List<Spot>> {
       return dao.getParkingSpots().map{
           spots->spots.map{it.toSpot()}
       }
    }

}