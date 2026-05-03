package com.googlehome.protect.ui.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.googlehome.protect.data.repository.FirebaseRepository
import com.googlehome.protect.model.Child
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.googlehome.protect.data.ModeManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

class ParentViewModel(private val repository: FirebaseRepository, private val modeManager: ModeManager) : ViewModel() {

    private val _childData = MutableStateFlow<Child?>(null)
    val childData: StateFlow<Child?> = _childData.asStateFlow()

    private val _trackingId = MutableStateFlow<String?>(null)
    val trackingId: StateFlow<String?> = _trackingId.asStateFlow()

    val trackedChildrenIds: StateFlow<Set<String>> = modeManager.trackedChildrenIds
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptySet())

    val trackingInterval: StateFlow<Long> = modeManager.trackingInterval
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, 10000L)

    val powerSavingEnabled: StateFlow<Boolean> = modeManager.powerSavingEnabled
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, false)

    val geofenceRadius: StateFlow<Float> = modeManager.geofenceRadius
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, 500f)

    val geofenceEnabled: StateFlow<Boolean> = modeManager.geofenceEnabled
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, false)

    private val _batteryAlert = MutableStateFlow<String?>(null)
    val batteryAlert: StateFlow<String?> = _batteryAlert.asStateFlow()

    val parentId: StateFlow<String?> = modeManager.parentId
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, null)

    val homeLocation: StateFlow<Pair<Double, Double>?> = modeManager.homeLocation
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, null)

    private val _etaMinutes = MutableStateFlow<Int?>(null)
    val etaMinutes: StateFlow<Int?> = _etaMinutes.asStateFlow()

    private val _anomalyAlert = MutableStateFlow<String?>(null)
    val anomalyAlert: StateFlow<String?> = _anomalyAlert.asStateFlow()

    private val _emergencyAlert = MutableStateFlow<String?>(null)
    val emergencyAlert: StateFlow<String?> = _emergencyAlert.asStateFlow()

    private val _emergencyAudio = MutableStateFlow<String?>(null)
    val emergencyAudio: StateFlow<String?> = _emergencyAudio.asStateFlow()

    private val _geofenceAlert = MutableStateFlow<String?>(null)
    val geofenceAlert: StateFlow<String?> = _geofenceAlert.asStateFlow()

    private var trackingJob: kotlinx.coroutines.Job? = null
    private var lastGeofenceStatus: String? = null

    init {
        viewModelScope.launch {
            val savedId = modeManager.trackedChildId.first()
            if (!savedId.isNullOrBlank()) {
                startTracking(savedId)
            }
            
            modeManager.parentId.collect { pid ->
                if (pid == null) {
                    generateAndSaveParentId()
                } else {
                    // Observe parent document for routines/home
                    repository.getParent(pid).collect { parent ->
                        parent?.let {
                            if (it.homeLat != 0.0) {
                                modeManager.setHomeLocation(it.homeLat, it.homeLon)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun generateAndSaveParentId() {
        val sdf = java.text.SimpleDateFormat("yyMMdd", java.util.Locale.getDefault())
        val datePart = sdf.format(java.util.Date())
        // For simplicity, we use random 4 digits as sequence for now
        val randomPart = (1..9999).random().toString().padStart(4, '0')
        val newPid = "$datePart$randomPart"
        modeManager.setParentId(newPid)
        repository.saveParent(com.googlehome.protect.model.Parent(id = newPid))
    }

    fun startTracking(childId: String) {
        _trackingId.value = childId
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            modeManager.setTrackedChildId(childId)
            repository.getChildLocation(childId).collect { child ->
                _childData.value = child
                updateETAAndRoutine(child)
                child?.let {
                    if (it.battery < 15) {
                        _batteryAlert.value = "Peringatan: Baterai ${it.name} kritis (${it.battery}%)"
                    } else if (it.battery >= 20) {
                        _batteryAlert.value = null
                    }

                    if (it.emergencyActive) {
                        _emergencyAlert.value = "DARURAT! ${it.name} menekan tombol panik!"
                        _emergencyAudio.value = it.emergencyAudioUrl
                    }

                    if (it.lastGeofenceStatus == "INSIDE" && lastGeofenceStatus == "OUTSIDE") {
                        _geofenceAlert.value = "${it.name} sudah sampai di area tujuan."
                    }
                    lastGeofenceStatus = it.lastGeofenceStatus
                }
            }
        }
    }

    fun dismissEmergencyAlert() {
        _emergencyAlert.value = null
    }

    fun dismissGeofenceAlert() {
        _geofenceAlert.value = null
    }

    fun dismissBatteryAlert() {
        _batteryAlert.value = null
    }

    fun dismissAnomalyAlert() {
        _anomalyAlert.value = null
    }

    fun updateChildName(childId: String, name: String) {
        viewModelScope.launch {
            repository.updateChildName(childId, name)
        }
    }

    fun setTrackingInterval(interval: Long) {
        viewModelScope.launch {
            modeManager.setTrackingInterval(interval)
            _trackingId.value?.let { 
                repository.updateSettings(it, interval, powerSavingEnabled.value)
            }
        }
    }

    fun setPowerSavingMode(enabled: Boolean) {
        viewModelScope.launch {
            modeManager.setPowerSavingMode(enabled)
            _trackingId.value?.let { 
                repository.updateSettings(it, trackingInterval.value, enabled)
            }
        }
    }

    fun setGeofenceRadius(radius: Float) {
        viewModelScope.launch {
            modeManager.setGeofenceRadius(radius)
            syncGeofenceToChild()
        }
    }

    fun setGeofenceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            modeManager.setGeofenceEnabled(enabled)
            syncGeofenceToChild()
        }
    }

    private suspend fun syncGeofenceToChild() {
        val cid = _trackingId.value ?: return
        val enabled = modeManager.geofenceEnabled.first()
        val radius = modeManager.geofenceRadius.first()
        val home = modeManager.homeLocation.first()
        
        if (enabled && home != null) {
            repository.updateGeofenceSettings(cid, home.first, home.second, radius)
        } else {
            repository.updateGeofenceSettings(cid, 0.0, 0.0, 0f)
        }
    }

    fun clearHistory(childId: String) {
        viewModelScope.launch {
            repository.clearChildHistory(childId)
        }
    }

    private fun updateETAAndRoutine(child: Child?) {
        if (child == null) return
        
        // Calculate ETA if home location is set
        val home = homeLocation.value
        if (home != null) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                child.currentLat, child.currentLon,
                home.first, home.second,
                results
            )
            val distance = results[0]
            val speedKmH = 30 // Assume 30km/h average for driving
            val roadFactor = 1.2
            val timeMinutes = (distance * roadFactor / (speedKmH / 3.6) / 60).toInt()
            _etaMinutes.value = if (timeMinutes > 0) timeMinutes else 1
        }
    }

    fun setHomeLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            modeManager.setHomeLocation(lat, lon)
            parentId.value?.let { pid ->
                repository.saveParent(com.googlehome.protect.model.Parent(id = pid, homeLat = lat, homeLon = lon))
            }
        }
    }

    fun addChild(childId: String) {
        viewModelScope.launch {
            val pid = parentId.value ?: return@launch
            repository.addChildToParent(pid, childId)
            modeManager.setTrackedChildId(childId)
            startTracking(childId)
        }
    }

    fun removeChild(childId: String) {
        viewModelScope.launch {
            val pid = parentId.value ?: return@launch
            repository.removeChildFromParent(pid, childId)
            modeManager.removeTrackedChildId(childId)
            if (_trackingId.value == childId) {
                _trackingId.value = null
                _childData.value = null
                trackingJob?.cancel()
            }
        }
    }

    fun triggerRemoteRing(childId: String, active: Boolean) {
        viewModelScope.launch {
            repository.triggerRemoteRing(childId, active)
        }
    }
}
