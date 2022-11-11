package com.example.myeria.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myeria.domain.model.Spot
import com.example.myeria.presentation.camera.MapEvent
import com.example.myeria.presentation.camera.MapStyle
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel@Inject constructor():  ViewModel() {
    var state by mutableStateOf(HomeState())
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.onNameChange -> {
                state = state.copy(
                   name = event.name
                )
            }
         is HomeEvent.onJabatanChange->{
             state = state.copy(
                 jabatan = event.jabatan
             )
         }
            is HomeEvent.onNikChange->{
                state = state.copy(
                    nik = event.nik
                )
            }
        }
    }
}