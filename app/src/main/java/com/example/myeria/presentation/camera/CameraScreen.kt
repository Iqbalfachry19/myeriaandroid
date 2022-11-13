package com.example.myeria.presentation.camera

import android.Manifest
import android.app.Activity

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.myeria.domain.model.Data
import com.example.myeria.QrCodeAnalyzer

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
fun CameraScreen(viewModel: MapsViewModel = hiltViewModel()) {


    var code by remember {
        mutableStateOf("")
    }

    val gson = Gson()
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }
    val eria = LatLng(0.512177414650849, 101.43813212084146)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(eria, 19f)
    }

    val locationState = viewModel.locationFlow.collectAsState(initial = viewModel.newLocation())

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    val markerState = rememberMarkerState(
        position = eria,
    )

    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasBackgroundPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        })
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    val launcherPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasBackgroundPermission = granted
        })
    LaunchedEffect(key1 = true) {
        launcherPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }
    LaunchedEffect(locationState.value) {
        Log.d(TAG, "Updating blue dot on map...")
        viewModel.locationSource.onLocationChanged(locationState.value)

        Log.d(TAG, "Updating camera position...")
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
                TAG,
                "Map camera started moving due to ${cameraPositionState.cameraMoveStartedReason.name}"
            )
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        if (hasCamPermission) {
            AndroidView(factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(previewView.width, previewView.height))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QrCodeAnalyzer { result -> code = result })
                try {
                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                previewView
            }, modifier = Modifier.weight(1f))
            if (code.isNotEmpty()) {

                Box(Modifier.fillMaxWidth()) {

                    GoogleMap(
                        modifier = Modifier
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
                            radius = 45.0,
                            fillColor = Color(0x2200FF00),
                            strokeColor = Color.Green,
                            strokeWidth = 3f,

                            )
                    }
                    if (!viewModel.state.isMapLoaded) {
                        androidx.compose.animation.AnimatedVisibility(
                            modifier = Modifier
                                .matchParentSize(),
                            visible = !viewModel.state.isMapLoaded,
                            enter = EnterTransition.None,
                            exit = fadeOut(),


                            ) {

                            CircularProgressIndicator(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .wrapContentSize()
                            )

                        }

                    }
                    Column {
                        Spacer(Modifier.weight(1f))
                        Column(
                            modifier = Modifier.background(Color.White),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nama: ${gson.fromJson(code, Data::class.java).name}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(),

                                color = Color.Black
                            )
                            Text(
                                text = "Nik: ${gson.fromJson(code, Data::class.java).nik}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.Black
                            )
                            Text(
                                text = "Jabatan: ${gson.fromJson(code, Data::class.java).jabatan}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.Black
                            )
                            Text(
                                text = "Lokasi: ${gson.fromJson(code, Data::class.java).lokasi}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
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


        }
    }
}


