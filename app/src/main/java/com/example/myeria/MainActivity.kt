package com.example.myeria



import android.annotation.SuppressLint
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope

import com.example.myeria.presentation.NavGraphs

import com.example.myeria.ui.theme.MyEriaTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            }
        }

    }

