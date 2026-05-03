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
    private var cachedChild: com.googlehome.protect.model.Child? = null
    private var lastGeofenceStatus = "OUTSIDE"

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
            startHistoryCleanup(childId)
            repository.getChildLocation(childId).collect { child ->
                cachedChild = child
                child?.let {
                    if (it.trackingInterval != currentInterval || it.powerSavingEnabled != powerSavingEnabled) {
                        currentInterval = it.trackingInterval
                        powerSavingEnabled = it.powerSavingEnabled
                        requestLocationUpdates() // Restart with new interval
                    }
                    lastGeofenceStatus = it.lastGeofenceStatus
                    handleRemoteRing(it.remoteRingActive, childId)
                }
            }
        }
    }

    private fun startHistoryCleanup(childId: String) {
        serviceScope.launch {
            while (true) {
                // 5 days in ms = 432_000_000L
                repository.cleanupOldHistory(childId, 432_000_000L)
                // Check again every 12 hours
                kotlinx.coroutines.delay(12L * 60 * 60 * 1000)
            }
        }
    }

    private var isRinging = false
    private var mediaPlayer: android.media.MediaPlayer? = null

    private fun handleRemoteRing(active: Boolean, childId: String) {
        if (active && !isRinging) {
            isRinging = true
            serviceScope.launch {
                try {
                    val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                    val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_ALARM)
                    audioManager.setStreamVolume(android.media.AudioManager.STREAM_ALARM, maxVolume, 0)

                    val ringtoneUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                    mediaPlayer = android.media.MediaPlayer().apply {
                        setAudioAttributes(
                            android.media.AudioAttributes.Builder()
                                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        setDataSource(this@LocationService, ringtoneUri)
                        isLooping = true
                        prepare()
                        start()
                    }

                    kotlinx.coroutines.delay(15000) // Ring for 15 seconds
                    
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    isRinging = false
                    
                    repository.triggerRemoteRing(childId, false)
                } catch (e: Exception) {
                    android.util.Log.e("LocationService", "Error remote ring", e)
                    isRinging = false
                    repository.triggerRemoteRing(childId, false)
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
                networkStatus = getNetworkStatus(),
                timestamp = System.currentTimeMillis()
            )
            repository.updateLocation(childId, entry)
            
            // Geofence check
            cachedChild?.let { child ->
                if (child.geofenceRadius > 0) {
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        location.latitude, location.longitude,
                        child.geofenceLat, child.geofenceLon,
                        results
                    )
                    val distance = results[0]
                    val currentStatus = if (distance <= child.geofenceRadius) "INSIDE" else "OUTSIDE"
                    
                    if (currentStatus != lastGeofenceStatus) {
                        lastGeofenceStatus = currentStatus
                        repository.updateGeofenceStatus(childId, currentStatus)
                    }
                }
            }
        }
    }

    private fun getBatteryLevel(): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            this.registerReceiver(null, filter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return (level * 100 / scale.toFloat()).toInt()
    }

    private fun getNetworkStatus(): String {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return "Offline"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "Offline"

        return when {
            capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val signalStrength = telephonyManager.signalStrength
                        if (signalStrength != null) {
                            val level = signalStrength.level // 0 to 4
                            when (level) {
                                0, 1 -> "Cellular (Lemah)"
                                2 -> "Cellular (Sedang)"
                                else -> "Cellular (Kuat)"
                            }
                        } else "Cellular"
                    } else "Cellular"
                } else {
                    "Cellular"
                }
            }
            else -> "Offline"
        }
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
