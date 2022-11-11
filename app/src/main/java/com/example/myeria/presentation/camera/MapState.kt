package com.example.myeria.presentation.camera

import com.example.myeria.domain.model.Spot
import com.google.maps.android.compose.MapProperties

data class MapState(
    val properties: MapProperties = MapProperties(

    ),
    val parkingSpots: List<Spot> = emptyList(),
    val isFalloutMap: Boolean = false
)
