package com.googlehome.protect.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.googlehome.protect.model.Child
import com.googlehome.protect.model.LocationEntry
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("tracking_units")

    fun getChildLocation(childId: String): Flow<Child?> = callbackFlow {
        if (childId.isBlank()) {
            trySend(null)
            return@callbackFlow
        }
        val docRef = collection.document(childId)
        val registration = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FirebaseRepository", "Error listening to location", e)
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                try {
                    val child = snapshot.toObject(Child::class.java)
                    trySend(child)
                } catch (ex: Exception) {
                    Log.e("FirebaseRepository", "Error parsing Child data", ex)
                    trySend(null)
                }
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun updateLocation(childId: String, entry: LocationEntry) {
        if (childId.isBlank()) return
        try {
            val docRef = collection.document(childId)
            val historyKey = System.currentTimeMillis().toString()
            
            // Harus menggunakan update() agar dot notation ("history.xxx") bekerja sebagai nested object
            docRef.update(
                "history.$historyKey", entry,
                "currentLat", entry.latitude,
                "currentLon", entry.longitude,
                "battery", entry.battery,
                "lastSeen", entry.timestamp
            ).await()
        } catch (e: Exception) {
            // Jika document belum ada, update() akan gagal. Kita tangkap dan gunakan set() untuk inisialisasi awal.
            try {
                val docRef = collection.document(childId)
                val historyKey = System.currentTimeMillis().toString()
                val initialData = hashMapOf<String, Any>(
                    "currentLat" to entry.latitude,
                    "currentLon" to entry.longitude,
                    "battery" to entry.battery,
                    "lastSeen" to entry.timestamp,
                    "history" to hashMapOf(historyKey to entry)
                )
                docRef.set(initialData, SetOptions.merge()).await()
            } catch (ex: Exception) {
                Log.e("FirebaseRepository", "Failed to update location fallback", ex)
            }
        }
    }

    suspend fun updateChildName(childId: String, name: String) {
        if (childId.isBlank() || name.isBlank()) return
        try {
            collection.document(childId).update("name", name).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to update child name", e)
        }
    }

    suspend fun initializeChild(childId: String) {
        if (childId.isBlank()) return
        try {
            val docRef = collection.document(childId)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val initialData = hashMapOf<String, Any>(
                    "id" to childId,
                    "name" to "Google Home",
                    "battery" to 100,
                    "lastSeen" to System.currentTimeMillis(),
                    "currentLat" to 0.0,
                    "currentLon" to 0.0,
                    "history" to emptyMap<String, LocationEntry>()
                )
                docRef.set(initialData).await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to initialize child", e)
        }
    }

    suspend fun clearChildHistory(childId: String) {
        if (childId.isBlank()) return
        try {
            collection.document(childId).update("history", com.google.firebase.firestore.FieldValue.delete()).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to clear child history", e)
        }
    }

    suspend fun updateSettings(childId: String, interval: Long, powerSaving: Boolean) {
        if (childId.isBlank()) return
        try {
            collection.document(childId).update(
                "trackingInterval", interval,
                "powerSavingEnabled", powerSaving
            ).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to update child settings", e)
        }
    }
}
