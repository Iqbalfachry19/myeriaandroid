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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.myeria.domain.model.Data
import com.example.myeria.presentation.destinations.CameraScreenDestination
import com.example.myeria.presentation.destinations.HomeScreenDestination
import com.example.myeria.presentation.destinations.MapScreenDestination
import com.example.myeria.presentation.destinations.ProfileScreenDestination
import com.example.myeria.util.QrCodeAnalyzer

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.*

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RequiresApi(Build.VERSION_CODES.Q)
@Destination
@Composable
fun CameraScreen(navigator: DestinationsNavigator, modifier: Modifier = Modifier) {


    var code by remember {
        mutableStateOf("")
    }




    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }


    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCamPermission = granted
    }
    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK)
            Log.d("appDebug", "Accepted")
        else {
            navigator.navigate(HomeScreenDestination)
            Log.d("appDebug", "Denied")
        }
    }
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
       checkLocationSetting(
            context = context,
            onDisabled = { intentSenderRequest ->
                settingResultRequest.launch(intentSenderRequest)
            },
            onEnabled = { /* This will call when setting is already enabled */ }
        )
    }


    Column(modifier = modifier.fillMaxSize()) {
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
            }, modifier = modifier.weight(1f))
            if (code.isNotEmpty()) {

               navigator.navigate(MapScreenDestination(code=code)){
                   popUpTo(CameraScreenDestination.route) { inclusive = true }
               }

            }


        }
    }
}


