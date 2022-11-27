package com.example.myeria.presentation.home

sealed class HomeEvent {
    data class OnNameChange(val name: String) : HomeEvent()
    data class OnNikChange(val nik: String) : HomeEvent()
    data class OnJabatanChange(val jabatan: String) : HomeEvent()
}