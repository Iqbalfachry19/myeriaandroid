package com.example.myeria.data

import com.example.myeria.domain.model.Spot

fun SpotEntity.toSpot(): Spot {
    return Spot(
        lat=lat,
        lng=lng,
        id=id
    )
}
fun Spot.toSpotEntity(): SpotEntity {
    return SpotEntity(
        lat=lat,
        lng=lng,
        id=id
    )
}