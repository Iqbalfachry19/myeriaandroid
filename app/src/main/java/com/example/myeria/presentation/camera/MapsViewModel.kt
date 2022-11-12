package com.example.myeria.presentation.camera

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myeria.MyLocationSource
import com.example.myeria.domain.model.Spot
import com.example.myeria.domain.repository.SpotRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MapsViewModel @Inject  constructor(
    private val repository: SpotRepository,
    application: Application
) :  AndroidViewModel(application) {


    var state by mutableStateOf(MapState())
    val locationSource = MyLocationSource()
    private lateinit var geofencingClient: GeofencingClient
     var locationClient: LocationClient= DefaultLocationClient(
    getApplication<Application>().applicationContext,
    LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)
    )
var lat:Double = 0.0
var long :Double= 0.0
    init{
        viewModelScope.launch {
            repository.getParkingSpots().collectLatest { spots->
                state = state.copy(
                    parkingSpots = spots
                )
            }


        }
    }


   val locationFlow =   locationClient.getLocationUpdates(10000L).catch { e ->
       e.printStackTrace()
   }.onEach {
           location-> lat = location.latitude.toString().takeLast(3).toDouble()
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

    private val centerLat =    0.4867559087709209
    private val centerLng = 101.45587392752422
    private val geofenceRadius = 45.0
    @SuppressLint("MissingPermission")
     fun addGeofence() {
        geofencingClient = LocationServices.getGeofencingClient(getApplication<Application>().applicationContext)
        val geofence = Geofence.Builder()
            .setRequestId("kampus")
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