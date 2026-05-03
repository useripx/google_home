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

    private var trackingJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            val savedId = modeManager.trackedChildId.first()
            if (!savedId.isNullOrBlank()) {
                startTracking(savedId)
            }
        }
    }

    fun startTracking(childId: String) {
        _trackingId.value = childId
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            modeManager.setTrackedChildId(childId)
            repository.getChildLocation(childId).collect { child ->
                _childData.value = child
                child?.let {
                    if (it.battery < 15) {
                        _batteryAlert.value = "Peringatan: Baterai ${it.name} kritis (${it.battery}%)"
                    } else if (it.battery >= 20) {
                        _batteryAlert.value = null
                    }
                }
            }
        }
    }

    fun dismissBatteryAlert() {
        _batteryAlert.value = null
    }

    fun updateChildName(childId: String, name: String) {
        viewModelScope.launch {
            repository.updateChildName(childId, name)
        }
    }

    fun removeChild(childId: String) {
        viewModelScope.launch {
            modeManager.removeTrackedChildId(childId)
            if (_trackingId.value == childId) {
                _trackingId.value = null
                _childData.value = null
                trackingJob?.cancel()
            }
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
        }
    }

    fun setGeofenceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            modeManager.setGeofenceEnabled(enabled)
        }
    }

    fun clearHistory(childId: String) {
        viewModelScope.launch {
            repository.clearChildHistory(childId)
        }
    }
}
