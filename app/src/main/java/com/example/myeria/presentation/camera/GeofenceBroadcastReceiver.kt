package com.example.myeria.presentation.camera

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.example.myeria.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver() : BroadcastReceiver() {

    var isOnEria = mutableStateOf(false)
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent != null) {
                if (geofencingEvent.hasError()) {
                    val errorMessage = geofencingEvent?.let { GeofenceStatusCodes.getStatusCodeString(it.errorCode) }
                    if (errorMessage != null) {
                        Log.e(TAG, errorMessage)
                    }
                    if (errorMessage != null) {
                        sendNotification(context, errorMessage)
                    }
                    return
                }
            }

            val geofenceTransition = geofencingEvent?.geofenceTransition

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                isOnEria.value = true
                val geofenceTransitionString =
                    when (geofenceTransition) {
                        Geofence.GEOFENCE_TRANSITION_ENTER -> "Anda telah memasuki area"
                        Geofence.GEOFENCE_TRANSITION_DWELL -> "Anda telah di dalam area"
                        else -> "Invalid transition type"
                    }

                val triggeringGeofences = geofencingEvent.triggeringGeofences
                val requestId = triggeringGeofences?.get(0)?.requestId

                val geofenceTransitionDetails = "$geofenceTransitionString $requestId"
                Log.i(TAG, geofenceTransitionDetails)

                sendNotification(context, geofenceTransitionDetails)
            } else {
                val errorMessage = "Invalid transition type : $geofenceTransition"
                Log.e(TAG, errorMessage)
                sendNotification(context, errorMessage)
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun sendNotification(context: Context, geofenceTransitionDetails: String) {
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(geofenceTransitionDetails)
            .setContentText("Anda sudah bisa absen sekarang :)")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification = mBuilder.build()
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val TAG = "GeofenceBroadcast"
        const val ACTION_GEOFENCE_EVENT = "GeofenceEvent"
        private const val CHANNEL_ID = "1"
        private const val CHANNEL_NAME = "Geofence Channel"
        private const val NOTIFICATION_ID = 1
    }
}