package com.example.runningapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.runningapp.R
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.runningapp.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningapp.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningapp.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningapp.other.Constants.NOTIFICATION_ID
import com.example.runningapp.other.MyPermissions
import com.example.runningapp.other.Utility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private var isFirstRun = true
    private var isServiceKilled = false

    private var isTimeEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private var curNotificationBuilder: NotificationCompat.Builder? = null

    private val timeRunInSecond: MutableStateFlow<Long?> = MutableStateFlow(null)

    companion object {
        val timeRunInMillis: MutableStateFlow<Long?> = MutableStateFlow(null)
        val isTracking: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val pathPoints: MutableStateFlow<Polylines?> = MutableStateFlow(null)
    }

    override fun onCreate() {
        super.onCreate()

        curNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        subscribeToObservers()
    }

    private fun postInitialValues() {
        isTracking.value = false
        pathPoints.value = mutableListOf()
        timeRunInSecond.value = 0L
        timeRunInMillis.value = 0L
    }

    private fun killService(){
        isServiceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .cancelAll()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    killService()
                }
                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.value = true
        timeStarted = System.currentTimeMillis()
        isTimeEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value){
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.value = timeRun + lapTime
                if(timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSecond.value = timeRunInSecond.value!! + 1
                    lastSecondTimestamp += 1000L
                }
                delay(100L)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService() {
        isTracking.value = false
        isTimeEnabled = false
    }

    private fun subscribeToObservers(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isTracking.collect {
                    updateLocationTracking(it)
                    updateNotificationTrackingState(it)
                }
            }
        }
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if(isTracking) {
            val pauseIntent = Intent(
                this,
                TrackingService::class.java
            ).apply {
                action = ACTION_PAUSE_SERVICE
            }
            getService(this, 1, pauseIntent, FLAG_IMMUTABLE)
        } else {
            val resumeIntent = Intent(
                this,
                TrackingService::class.java
            ).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            getService(this, 2, resumeIntent, FLAG_IMMUTABLE)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder!!.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if(!isServiceKilled){
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_run_24, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder!!.build())
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {
            if(MyPermissions.checkAllLocationPermissions(this)){
                val request = LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
                    .setMinUpdateIntervalMillis(FASTEST_LOCATION_INTERVAL)
                    .setPriority(PRIORITY_HIGH_ACCURACY)
                    .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value) {
                result.locations.let { locations ->
                    for(location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let{
            val pos = LatLng(location.latitude, location.longitude)
            val value = pathPoints.value!!
            value.last().add(pos)
            pathPoints.value = mutableListOf()
            pathPoints.value = value
        }
    }

    private fun addEmptyPolyline() {
        if(pathPoints.value != null) {
            val value = pathPoints.value
            value!!.add(mutableListOf())
            value.add(mutableListOf())
            //TODO something with it
            pathPoints.value = mutableListOf()
            pathPoints.value = value
        } else {
            pathPoints.value = mutableListOf(mutableListOf())
        }
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.value = true

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        createNotificationChannel(notificationManager)

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                timeRunInSecond.collect{
                    if(it != null) {
                        if(!isServiceKilled){
                            val notification = curNotificationBuilder!!
                                .setContentText(Utility.getFormattedStopWatchTime(it * 1000L, false))
                            notificationManager.notify(NOTIFICATION_ID, notification.build())
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}