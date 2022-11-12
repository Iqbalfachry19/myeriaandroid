package com.example.myeria.presentation.home
import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.myeria.Greeting
import com.example.myeria.presentation.camera.DefaultLocationClient
import com.example.myeria.presentation.camera.GeofenceBroadcastReceiver
import com.example.myeria.presentation.camera.LocationClient
import com.example.myeria.presentation.destinations.CameraScreenDestination
import com.example.myeria.presentation.home.HomeEvent
import com.example.myeria.presentation.home.HomeViewModel
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.internal.Contexts.getApplication

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hi, Muhammad Iqbal Fachry Krisbudiana",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Greeting(
                name = viewModel.state.name,
                nik = viewModel.state.nik,
                jabatan = viewModel.state.jabatan,
                onNameChange = { viewModel.onEvent(HomeEvent.onNameChange(it)) },
                onNikChange = { viewModel.onEvent(HomeEvent.onNikChange(it)) },
                onJabatanChange = { viewModel.onEvent(HomeEvent.onJabatanChange(it)) },
                innerPadding,
                onNavigateToCamera = {  navigator.navigate(CameraScreenDestination) }
            )
        }
    )

}