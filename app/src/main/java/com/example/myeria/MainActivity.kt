package com.example.myeria



import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION

import android.location.Location
import android.os.Bundle


import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource


import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope

import com.example.myeria.presentation.NavGraphs
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import com.example.myeria.presentation.camera.DefaultLocationClient
import com.example.myeria.presentation.camera.LocationClient
import com.example.myeria.presentation.camera.LocationService
import com.example.myeria.ui.theme.MyEriaTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,

            ),
            0
        )

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        lifecycleScope.launch{
            delay(50)
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        setContent {
            MyEriaTheme {


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                        DestinationsNavHost(navGraph = NavGraphs.root)

                }
            }
        }
    }
}





    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Greeting(
        name: String,
        nik: String,
        jabatan: String,
        onNameChange: (String) -> Unit,
        onNikChange: (String) -> Unit,
        onJabatanChange: (String) -> Unit,
        innerPadding: PaddingValues,
        onNavigateToCamera: () -> Unit,
    ) {
        val mContext = LocalContext.current
        LazyColumn(
            contentPadding = innerPadding, modifier = Modifier.padding(16.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedCard(
                        Modifier.padding(8.dp).clickable (
                               onClick= onNavigateToCamera
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "logo"
                            )
                            Text("Isi Absen", color = Color.Black)
                        }

                    }
                    ElevatedCard(
                        Modifier.padding(8.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "logo"
                            )
                            Text("Laporan Absen", color = Color.Black)
                        }

                    }
                }

                if (name.isNotEmpty()) {
                    Text(text = "Hello $name")
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nama") })
                if (nik.isNotEmpty()) {
                    Text(text = nik)
                }
                OutlinedTextField(value = nik, onValueChange = onNikChange, label = { Text("NIK") })
                if (jabatan.isNotEmpty()) {
                    Text(text = jabatan)
                }
                OutlinedTextField(
                    value = jabatan,
                    onValueChange = onJabatanChange,
                    label = { Text("Jabatan") })
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text("Submit")
                }
                Button(onClick = {
                    Intent(mContext, LocationService::class.java).apply {
                        action = LocationService.ACTION_START
                        mContext.startService(this)
                    }
                }) {
                    Text(text = "Start")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    Intent(mContext, LocationService::class.java).apply {
                        action = LocationService.ACTION_STOP
                        mContext.startService(this)
                    }
                }) {
                    Text(text = "Stop")
                }
            }
        }

    }

 class MyLocationSource : LocationSource {

    private var listener: LocationSource.OnLocationChangedListener? = null

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        this.listener = listener
    }

    override fun deactivate() {
        listener = null
    }

    fun onLocationChanged(location: Location) {
        listener?.onLocationChanged(location)
    }
}

