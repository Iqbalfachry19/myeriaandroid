package com.example.myeria.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myeria.domain.model.Data
import com.example.myeria.QrCodeAnalyzer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.*

import com.ramcosta.composedestinations.annotation.Destination

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
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        })
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
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
            if(code.isNotEmpty()) {
                Column(modifier= Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = gson.fromJson(code, Data::class.java).name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        color = Color.White
                    )
                    GoogleMap(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        properties = viewModel.state.properties,
                        uiSettings = uiSettings,
                        cameraPositionState = cameraPositionState,
                        onMapLongClick = {
                            viewModel.onEvent(MapEvent.OnMapLongClick(it))
                        }
                    ) {
                        Marker(
                            position = eria,
                            title = "Rumah Sakit Eria Pekanbaru",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        )
                        Circle(
                            center= eria,
                            radius=45.0,
                            fillColor =Color(0x2200FF00),
                            strokeColor = Color.Green,
                            strokeWidth = 3f,

                        )
                    }
                    OutlinedButton(onClick = { /*TODO*/ }) {
                        Text("Submit")
                    }
                }

            }
        }
    }
}