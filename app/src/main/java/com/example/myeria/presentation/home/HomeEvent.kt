package com.example.myeria.presentation.home

sealed class HomeEvent {
    data class onNameChange(val name:String):HomeEvent()
    data class onNikChange(val nik:String):HomeEvent()
    data class onJabatanChange(val jabatan:String):HomeEvent()
}