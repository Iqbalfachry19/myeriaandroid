package com.example.myeria.presentation.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myeria.R

import com.example.myeria.presentation.camera.LocationService

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
    onNavigateToProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAnnouncement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mContext = LocalContext.current
    LazyColumn(
        contentPadding = innerPadding, modifier = modifier.padding(16.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedCard(
                        Modifier
                            .padding(8.dp)
                            .clickable(
                                onClick = onNavigateToCamera
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
                        modifier
                            .padding(8.dp)
                            .clickable(
                                onClick = onNavigateToHistory
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
                            Text("Riwayat Absen", color = Color.Black)
                        }

                    }
                }
                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedCard(
                        modifier
                            .padding(8.dp)
                            .clickable(
                                onClick = onNavigateToProfile
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
                            Text("Profil Karyawan", color = Color.Black)
                        }

                    }
                    ElevatedCard(
                        modifier
                            .padding(8.dp)
                            .clickable(
                                onClick = onNavigateToAnnouncement
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
                            Text("Pengumuman", color = Color.Black)
                        }

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
            Spacer(modifier = modifier.height(16.dp))
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