package com.example.myeria.presentation.camera

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myeria.domain.model.Data
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.*
import com.ramcosta.composedestinations.annotation.Destination

@RequiresApi(Build.VERSION_CODES.Q)
@Destination
@Composable
fun MapScreen(code:String,viewModel: MapsViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()) {
        val gson = Gson()
        val eria = LatLng(0.512177414650849, 101.43813212084146)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(eria, 19f)
        }

        val markerState = rememberMarkerState(
            position = eria,
        )
        val context = LocalContext.current
        val uiSettings = remember {
            MapUiSettings(zoomControlsEnabled = false)
        }
        var hasBackgroundPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }
        val launcherPermission = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                hasBackgroundPermission = granted
            })

        val locationState = viewModel.locationFlow.collectAsState(initial = viewModel.newLocation())
        LaunchedEffect(key1 = true) {
            launcherPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        }
        LaunchedEffect(locationState.value) {
            Log.d(ContentValues.TAG, "Updating blue dot on map...")
            viewModel.locationSource.onLocationChanged(locationState.value)

            Log.d(ContentValues.TAG, "Updating camera position...")
            val cameraPosition = CameraPosition.fromLatLngZoom(
                LatLng(
                    locationState.value.latitude,
                    locationState.value.longitude
                ), 19f
            )
            if (viewModel.state.isMapLoaded) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    1_000
                )

            }
        }

        // Detect when the map starts moving and print the reason
        LaunchedEffect(cameraPositionState.isMoving) {
            if (cameraPositionState.isMoving) {
                Log.d(
                    ContentValues.TAG,
                    "Map camera started moving due to ${cameraPositionState.cameraMoveStartedReason.name}"
                )
            }
        }
        GoogleMap(
            modifier = modifier
                .fillMaxWidth(),
            properties = viewModel.state.properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
            locationSource = viewModel.locationSource,

            onMapLoaded = {
                viewModel.state.isMapLoaded = true
                viewModel.addGeofence()
            }
        ) {
            Marker(
                state = markerState,
                title = "Rumah Sakit Eria Pekanbaru",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
            Circle(
                center = eria,
                radius = 90.0,
                fillColor = Color(0x2200FF00),
                strokeColor = Color.Green,
                strokeWidth = 3f,

                )
        }
        if (!viewModel.state.isMapLoaded) {
            AnimatedVisibility(
                modifier = modifier
                    .matchParentSize(),
                visible = !viewModel.state.isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut(),


                ) {

                CircularProgressIndicator(
                    modifier = modifier
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentSize()
                )

            }

        }
        Column {
            Spacer(modifier.weight(1f))
            Column(
                modifier = modifier.background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                    Text(
                        text = "Nama: Muhammad Iqbal Fachry Krisbudiana",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier
                            .fillMaxWidth(),

                        color = Color.Black
                    )
                    Text(
                        text = "Nik: 1907113070",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier
                            .fillMaxWidth(),
                        color = Color.Black
                    )
                    Text(
                        text = "Jabatan: Mahasiswa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier
                            .fillMaxWidth(),
                        color = Color.Black
                    )
                    Text(
                        text = "Lokasi: ${gson.fromJson(code, Data::class.java).lokasi}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier
                            .fillMaxWidth(),
                        color = Color.Black
                    )


                if (viewModel.state.isOnEria) {
                    OutlinedButton(onClick = { /*TODO*/ }) {
                        Text("Isi Absen")
                    }
                } else {
                    Text("Anda berada di luar rumah sakit eria")
                }
            }

        }

    }

}