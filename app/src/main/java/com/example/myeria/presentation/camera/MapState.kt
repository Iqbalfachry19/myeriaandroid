package com.example.myeria.presentation.camera


import com.example.myeria.MyLocationSource
import com.example.myeria.domain.model.Spot

import com.google.android.gms.maps.LocationSource
import com.google.maps.android.compose.MapProperties

data class MapState(
    val properties: MapProperties = MapProperties(
        isMyLocationEnabled = true
    ),
    var isMapLoaded:Boolean = false,
var isOnEria:Boolean = false,
    val parkingSpots: List<Spot> = emptyList(),
    val isFalloutMap: Boolean = false
)
