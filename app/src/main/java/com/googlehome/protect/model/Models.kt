package com.googlehome.protect.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class LocationEntry(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Double = 0.0,
    val battery: Int = 0,
    val networkStatus: String = "Unknown",
    val timestamp: Long = 0L
)

@IgnoreExtraProperties
data class Child(
    val id: String = "",
    val name: String = "",
    val battery: Int = 0,
    val lastSeen: Long = 0L,
    val currentLat: Double = 0.0,
    val currentLon: Double = 0.0,
    val history: Map<String, LocationEntry> = emptyMap(),
    val trackingInterval: Long = 10000L,
    val powerSavingEnabled: Boolean = false,
    val parentId: String? = null,
    val emergencyActive: Boolean = false,
    val emergencyAudioUrl: String? = null,
    val lastGeofenceStatus: String = "OUTSIDE",
    val geofenceLat: Double = 0.0,
    val geofenceLon: Double = 0.0,
    val geofenceRadius: Float = 0f,
    val remoteRingActive: Boolean = false,
    val networkStatus: String = "Unknown"
)

@IgnoreExtraProperties
data class SafeRoutine(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Float = 200f,
    val startTime: String = "08:00",
    val endTime: String = "15:00",
    val isActive: Boolean = true
)

@IgnoreExtraProperties
data class Parent(
    val id: String = "",
    val name: String = "",
    val childrenIds: List<String> = emptyList(),
    val homeLat: Double = 0.0,
    val homeLon: Double = 0.0
)

enum class AppMode {
    UNSET,
    PARENT,
    KIDS
}
