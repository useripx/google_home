package com.googlehome.protect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.googlehome.protect.data.ModeManager
import com.googlehome.protect.data.repository.FirebaseRepository
import com.googlehome.protect.model.AppMode
import com.google.firebase.auth.FirebaseAuth
import com.googlehome.protect.service.LocationService
import com.googlehome.protect.ui.MainViewModel
import com.googlehome.protect.ui.kids.KidsDisguiseScreen
import com.googlehome.protect.ui.parent.ParentDashboard
import com.googlehome.protect.ui.parent.ParentViewModel
import com.googlehome.protect.ui.setup.SetupScreen
import com.googlehome.protect.ui.theme.GoogleHomeTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    
    private lateinit var modeManager: ModeManager
    private lateinit var mainViewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Anonymous Authentication to satisfy secure Firestore Rules
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnFailureListener { e ->
                e.printStackTrace()
            }
        }
        
        modeManager = ModeManager(this)
        mainViewModel = MainViewModel(modeManager)
        
        enableEdgeToEdge()
        setContent {
            GoogleHomeTheme {
                var showBackgroundPermissionDialog by remember { mutableStateOf(false) }
                var showBatteryOptimizationDialog by remember { mutableStateOf(false) }
                
                val appMode by mainViewModel.appMode.collectAsState()
                val childId by mainViewModel.childId.collectAsState()
                val lastUpdateDate by mainViewModel.lastUpdateDate.collectAsState()

                // Permission Launcher
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val allGranted = permissions.values.all { it }
                    if (allGranted) {
                        if (appMode == AppMode.KIDS) {
                            startKidsLocationService()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                showBackgroundPermissionDialog = true
                            } else {
                                checkBatteryOptimization { showBatteryOptimizationDialog = true }
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (appMode) {
                        AppMode.UNSET -> {
                            SetupScreen(onModeSelected = { mode ->
                                mainViewModel.selectMode(mode)
                                if (mode == AppMode.KIDS) {
                                    requestInitialPermissions(permissionLauncher)
                                }
                            })
                        }
                        AppMode.PARENT -> {
                            val parentViewModel = remember { ParentViewModel(FirebaseRepository(), modeManager) }
                            ParentDashboard(parentViewModel)
                        }
                        AppMode.KIDS -> {
                            KidsDisguiseScreen(childId = childId, lastUpdateDate = lastUpdateDate)
                            LaunchedEffect(Unit) {
                                if (hasRequiredPermissions()) {
                                    startKidsLocationService()
                                } else {
                                    requestInitialPermissions(permissionLauncher)
                                }
                            }
                        }
                    }
                }

                if (showBackgroundPermissionDialog) {
                    AlertDialog(
                        onDismissRequest = { showBackgroundPermissionDialog = false },
                        title = { Text("Background Location Needed") },
                        text = { Text("To track this device continuously, please select 'Allow all the time' in the next screen settings.") },
                        confirmButton = {
                            Button(onClick = {
                                showBackgroundPermissionDialog = false
                                openAppSettings()
                                checkBatteryOptimization { showBatteryOptimizationDialog = true }
                            }) { Text("OPEN SETTINGS") }
                        }
                    )
                }

                if (showBatteryOptimizationDialog) {
                    AlertDialog(
                        onDismissRequest = { showBatteryOptimizationDialog = false },
                        title = { Text("Ignore Battery Optimizations") },
                        text = { Text("To ensure the tracker runs reliably without being killed by the system, please allow it to ignore battery optimizations.") },
                        confirmButton = {
                            Button(onClick = {
                                showBatteryOptimizationDialog = false
                                requestBatteryOptimizationBypass()
                            }) { Text("ALLOW") }
                        }
                    )
                }
            }
        }
    }

    private fun requestInitialPermissions(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        launcher.launch(permissions.toTypedArray())
    }

    private fun hasRequiredPermissions(): Boolean {
        val foreground = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val phoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        val notifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
        return foreground && notifications && audio && phoneState
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun startKidsLocationService() {
        val intent = Intent(this, LocationService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkBatteryOptimization(onNeedsBypass: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                onNeedsBypass()
            }
        }
    }

    private fun requestBatteryOptimizationBypass() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    }
    private var volumePressCount = 0
    private var lastPressTime = 0L
    private var isEmergencyActive = false

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP) {
            lifecycleScope.launch {
                if (modeManager.appMode.first() == com.googlehome.protect.model.AppMode.KIDS) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastPressTime < 1500) {
                        volumePressCount++
                    } else {
                        volumePressCount = 1
                    }
                    lastPressTime = currentTime

                    if (volumePressCount >= 5 && !isEmergencyActive) {
                        triggerEmergency()
                        volumePressCount = 0
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun triggerEmergency() {
        if (isEmergencyActive) return
        isEmergencyActive = true
        
        val recorder = com.googlehome.protect.util.EmergencyRecorder(this)
        val repository = FirebaseRepository()
        
        lifecycleScope.launch {
            val childId = modeManager.childId.first() ?: return@launch
            repository.triggerEmergency(childId, true)
            
            val audioFile = recorder.startRecording()
            delay(30000) // Record for 30 seconds
            recorder.stopRecording()
            
            audioFile?.let {
                repository.uploadEmergencyAudio(childId, it)
                it.delete()
            }
            
            repository.triggerEmergency(childId, false)
            isEmergencyActive = false
        }
    }
}