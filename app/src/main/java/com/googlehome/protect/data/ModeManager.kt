package com.googlehome.protect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.googlehome.protect.model.AppMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ModeManager(private val context: Context) {

    private val MODE_KEY = stringPreferencesKey("app_mode")
    private val CHILD_ID_KEY = stringPreferencesKey("child_id")
    private val LAST_UPDATE_KEY = longPreferencesKey("last_update_date")
    private val TRACKED_CHILD_ID_KEY = stringPreferencesKey("tracked_child_id") // Keep for active child
    private val TRACKED_CHILDREN_IDS_KEY = stringSetPreferencesKey("tracked_children_ids")
    private val TRACKING_INTERVAL_KEY = longPreferencesKey("tracking_interval")
    private val POWER_SAVING_MODE_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("power_saving_enabled")
    private val GEOFENCE_RADIUS_KEY = androidx.datastore.preferences.core.floatPreferencesKey("geofence_radius")
    private val GEOFENCE_ENABLED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("geofence_enabled")

    val appMode: Flow<AppMode> = context.dataStore.data
        .map { preferences ->
            val modeName = preferences[MODE_KEY] ?: AppMode.UNSET.name
            AppMode.valueOf(modeName)
        }

    val childId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[CHILD_ID_KEY]
        }

    val trackedChildId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TRACKED_CHILD_ID_KEY]
        }

    val trackedChildrenIds: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[TRACKED_CHILDREN_IDS_KEY] ?: emptySet()
        }

    val lastUpdateDate: Flow<Long?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_UPDATE_KEY]
        }

    val trackingInterval: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[TRACKING_INTERVAL_KEY] ?: 10000L // Default 10s
        }

    val powerSavingEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[POWER_SAVING_MODE_KEY] ?: false
        }

    val geofenceRadius: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[GEOFENCE_RADIUS_KEY] ?: 500f // Default 500m
        }

    val geofenceEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[GEOFENCE_ENABLED_KEY] ?: false
        }

    suspend fun setAppMode(mode: AppMode) {
        context.dataStore.edit { preferences ->
            preferences[MODE_KEY] = mode.name
        }
    }

    suspend fun setChildId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[CHILD_ID_KEY] = id
        }
    }

    suspend fun setTrackedChildId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[TRACKED_CHILD_ID_KEY] = id
            val currentSet = preferences[TRACKED_CHILDREN_IDS_KEY] ?: emptySet()
            if (!currentSet.contains(id)) {
                preferences[TRACKED_CHILDREN_IDS_KEY] = currentSet + id
            }
        }
    }

    suspend fun setLastUpdateDate(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_UPDATE_KEY] = timestamp
        }
    }

    suspend fun removeTrackedChildId(id: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[TRACKED_CHILDREN_IDS_KEY] ?: emptySet()
            if (currentSet.contains(id)) {
                preferences[TRACKED_CHILDREN_IDS_KEY] = currentSet - id
            }
            if (preferences[TRACKED_CHILD_ID_KEY] == id) {
                preferences.remove(TRACKED_CHILD_ID_KEY)
            }
        }
    }

    suspend fun setTrackingInterval(interval: Long) {
        context.dataStore.edit { preferences ->
            preferences[TRACKING_INTERVAL_KEY] = interval
        }
    }

    suspend fun setPowerSavingMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[POWER_SAVING_MODE_KEY] = enabled
        }
    }

    suspend fun setGeofenceRadius(radius: Float) {
        context.dataStore.edit { preferences ->
            preferences[GEOFENCE_RADIUS_KEY] = radius
        }
    }

    suspend fun setGeofenceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GEOFENCE_ENABLED_KEY] = enabled
        }
    }
}
