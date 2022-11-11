package com.example.myeria.presentation.camera

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myeria.domain.model.Spot
import com.example.myeria.domain.repository.SpotRepository

import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject  constructor(
    private val repository: SpotRepository
) :  ViewModel() {
    var state by mutableStateOf(MapState())
    init{
        viewModelScope.launch {
            repository.getParkingSpots().collectLatest { spots->
                state = state.copy(
                    parkingSpots = spots
                )
            }
        }
    }
    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.ToggleFalloutMap -> {
                state = state.copy(
                    properties = state.properties.copy(
                        mapStyleOptions = if(state.isFalloutMap){null} else MapStyleOptions(MapStyle.json),
                    ),
                    isFalloutMap = !state.isFalloutMap
                )
            }
            is MapEvent.OnMapLongClick ->{
                viewModelScope.launch {
                    repository.insertParkingSpot(
                        Spot(event.latLng.latitude,event.latLng.longitude)
                    )
                }
            }
            is MapEvent.OnInfoWindowLongClick ->{
                viewModelScope.launch {
                    repository.deleteParkingSpot(event.spot)
                }
            }
        }
    }
}