package com.example.myeria.presentation.camera

import android.Manifest

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


@RequiresApi(Build.VERSION_CODES.Q)
fun Context.hasLocationPermission():Boolean{
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )== PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    )== PackageManager.PERMISSION_GRANTED
}