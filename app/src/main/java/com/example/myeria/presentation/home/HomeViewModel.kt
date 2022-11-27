package com.example.myeria.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    var state by mutableStateOf(HomeState())
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnNameChange -> {
                state = state.copy(
                    name = event.name
                )
            }
            is HomeEvent.OnJabatanChange -> {
                state = state.copy(
                    jabatan = event.jabatan
                )
            }
            is HomeEvent.OnNikChange -> {
                state = state.copy(
                    nik = event.nik
                )
            }
        }
    }
}