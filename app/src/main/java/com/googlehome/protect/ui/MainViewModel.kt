package com.googlehome.protect.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.googlehome.protect.data.ModeManager
import com.googlehome.protect.model.AppMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

import com.googlehome.protect.data.repository.FirebaseRepository
import com.google.firebase.installations.FirebaseInstallations

class MainViewModel(private val modeManager: ModeManager) : ViewModel() {

    private val _appMode = MutableStateFlow(AppMode.UNSET)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    private val _childId = MutableStateFlow<String?>(null)
    val childId: StateFlow<String?> = _childId.asStateFlow()

    private val _lastUpdateDate = MutableStateFlow<Long>(0L)
    val lastUpdateDate: StateFlow<Long> = _lastUpdateDate.asStateFlow()

    private val repository = FirebaseRepository()

    init {
        viewModelScope.launch {
            _appMode.value = modeManager.appMode.first()
            _childId.value = modeManager.childId.first()
            
            // Handle last update date logic for Kids Mode
            val savedDate = modeManager.lastUpdateDate.first() ?: 0L
            val currentTime = System.currentTimeMillis()
            val threeDaysInMillis = 3L * 24 * 60 * 60 * 1000
            
            if (savedDate == 0L || currentTime > savedDate + threeDaysInMillis) {
                modeManager.setLastUpdateDate(currentTime)
                _lastUpdateDate.value = currentTime
            } else {
                _lastUpdateDate.value = savedDate
            }
        }
    }

    fun selectMode(mode: AppMode) {
        viewModelScope.launch {
            modeManager.setAppMode(mode)
            _appMode.value = mode
            if (mode == AppMode.KIDS) {
                FirebaseInstallations.getInstance().id.addOnSuccessListener { id ->
                    if (id != null) {
                        val shortId = id.take(8).uppercase()
                        viewModelScope.launch {
                            modeManager.setChildId(shortId)
                            _childId.value = shortId
                            repository.initializeChild(shortId)
                        }
                    }
                }
            }
        }
    }
}
