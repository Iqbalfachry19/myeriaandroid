package com.example.myeria.presentation.camera


import com.example.myeria.domain.model.Spot
import com.google.android.gms.maps.model.LatLng

sealed class MapEvent {
    object ToggleFalloutMap : MapEvent()
    data class OnMapLongClick(val latLng: LatLng) : MapEvent()
    data class OnInfoWindowLongClick(val spot: Spot) : MapEvent()
}