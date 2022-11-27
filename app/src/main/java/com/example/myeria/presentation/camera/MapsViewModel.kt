package com.example.myeria.presentation.camera

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myeria.MyLocationSource
import com.example.myeria.domain.repository.SpotRepository
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.tasks.Task
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("UnspecifiedImmutableFlag")
@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: SpotRepository,

    application: Application
) : AndroidViewModel(application) {
    var state by mutableStateOf(MapState())
    val locationSource = MyLocationSource()
    private lateinit var geofencingClient: GeofencingClient
    private var locationClient: LocationClient = DefaultLocationClient(
        getApplication<Application>().applicationContext,
        LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)
    )
    private var lat: Double = 0.0
    private var long: Double = 0.0

    init {
        viewModelScope.launch {
            repository.updateParkingSpot(false)
            repository.getParkingSpot().collectLatest { spots ->
                state = state.copy(
                    isOnEria = spots[0].isOnEria
                )
            }


        }
    }


    val locationFlow = locationClient.getLocationUpdates(10000L).catch { e ->
        e.printStackTrace()
    }.onEach { location ->
        lat = location.latitude.toString().takeLast(3).toDouble()
        long = location.longitude.toString().takeLast(3).toDouble()

    }

    fun newLocation(): Location {
        val location = Location("MyLocationProvider")
        location.apply {
            latitude = lat
            longitude = long
        }
        return location
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(application, GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(application, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private val centerLat = 0.512177414650849
    private val centerLng = 101.43813212084146
    private val geofenceRadius = 90.0

    @SuppressLint("MissingPermission")
    fun addGeofence() {
        geofencingClient =
            LocationServices.getGeofencingClient(getApplication<Application>().applicationContext)
        val geofence = Geofence.Builder()
            .setRequestId("rumah sakit eria")
            .setCircularRegion(
                centerLat,
                centerLng,
                geofenceRadius.toFloat()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(5000)
            .build()
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
                        showToast("Geofencing added")
                    }
                    addOnFailureListener {
                        showToast("Geofencing not added : ${it.message}")

                    }
                }
            }
        }

    }

    private fun showToast(text: String) {
        Toast.makeText(getApplication(getApplication()), text, Toast.LENGTH_SHORT).show()
    }


}