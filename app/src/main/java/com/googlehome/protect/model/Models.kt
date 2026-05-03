package com.googlehome.protect.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class LocationEntry(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Double = 0.0,
    val battery: Int = 0,
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
    val powerSavingEnabled: Boolean = false
)

enum class AppMode {
    UNSET,
    PARENT,
    KIDS
}
