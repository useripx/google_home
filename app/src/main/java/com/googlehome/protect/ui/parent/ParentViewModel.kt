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

class ParentViewModel(private val repository: FirebaseRepository, private val modeManager: ModeManager) : ViewModel() {

    private val _childData = MutableStateFlow<Child?>(null)
    val childData: StateFlow<Child?> = _childData.asStateFlow()

    private val _trackingId = MutableStateFlow<String?>(null)
    val trackingId: StateFlow<String?> = _trackingId.asStateFlow()

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
        viewModelScope.launch {
            modeManager.setTrackedChildId(childId)
            repository.getChildLocation(childId).collect {
                _childData.value = it
            }
        }
    }
}
