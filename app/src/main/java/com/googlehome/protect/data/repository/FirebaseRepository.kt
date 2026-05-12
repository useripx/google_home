package com.googlehome.protect.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.googlehome.protect.model.Child
import com.googlehome.protect.model.LocationEntry
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val collection = firestore.collection("tracking_units")
    private val parentsCollection = firestore.collection("parents")

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
                "networkStatus", entry.networkStatus,
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

    fun getParent(parentId: String): Flow<com.googlehome.protect.model.Parent?> = callbackFlow {
        if (parentId.isBlank()) {
            trySend(null)
            return@callbackFlow
        }
        val listener = parentsCollection.document(parentId).addSnapshotListener { snapshot, e ->
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(com.googlehome.protect.model.Parent::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveParent(parent: com.googlehome.protect.model.Parent) {
        try {
            parentsCollection.document(parent.id).set(parent, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error saving parent", e)
        }
    }

    suspend fun addChildToParent(parentId: String, childId: String) {
        try {
            // 1. Link parent to child
            collection.document(childId).update("parentId", parentId).await()
            // 2. Add child to parent's list
            parentsCollection.document(parentId).update(
                "childrenIds", com.google.firebase.firestore.FieldValue.arrayUnion(childId)
            ).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error linking child to parent", e)
        }
    }
    
    suspend fun removeChildFromParent(parentId: String, childId: String) {
        try {
            collection.document(childId).update("parentId", null).await()
            parentsCollection.document(parentId).update(
                "childrenIds", com.google.firebase.firestore.FieldValue.arrayRemove(childId)
            ).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error unlinking child", e)
        }
    }

    suspend fun triggerEmergency(childId: String, active: Boolean) {
        try {
            collection.document(childId).update("emergencyActive", active).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error triggering emergency", e)
        }
    }

    suspend fun uploadEmergencyAudio(childId: String, file: java.io.File): String? {
        val storageRef = storage.reference.child("emergency_audio/$childId/${System.currentTimeMillis()}.m4a")
        return try {
            storageRef.putFile(android.net.Uri.fromFile(file)).await()
            val url = storageRef.downloadUrl.await().toString()
            collection.document(childId).update("emergencyAudioUrl", url).await()
            url
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Upload failed", e)
            null
        }
    }

    suspend fun updateGeofenceStatus(childId: String, status: String) {
        try {
            collection.document(childId).update("lastGeofenceStatus", status).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating geofence status", e)
        }
    }

    suspend fun updateGeofenceSettings(childId: String, lat: Double, lon: Double, radius: Float) {
        try {
            collection.document(childId).update(
                mapOf(
                    "geofenceLat" to lat,
                    "geofenceLon" to lon,
                    "geofenceRadius" to radius
                )
            ).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating geofence settings", e)
        }
    }

    suspend fun triggerRemoteRing(childId: String, active: Boolean) {
        try {
            collection.document(childId).update("remoteRingActive", active).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error triggering remote ring", e)
        }
    }

    suspend fun verifyActivationCode(code: String): com.googlehome.protect.model.Parent? {
        return try {
            val snapshot = parentsCollection
                .whereEqualTo("activationCode", code)
                .limit(1)
                .get()
                .await()
            
            if (!snapshot.isEmpty) {
                snapshot.documents[0].toObject(com.googlehome.protect.model.Parent::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error verifying activation code", e)
            null
        }
    }

    suspend fun cleanupOldHistory(childId: String, maxAgeMs: Long) {
        if (childId.isBlank()) return
        try {
            val docRef = collection.document(childId)
            val snapshot = docRef.get().await()
            val child = snapshot.toObject(Child::class.java) ?: return
            
            val currentTime = System.currentTimeMillis()
            val keysToDelete = child.history.filter { (key, entry) ->
                currentTime - entry.timestamp > maxAgeMs
            }.keys
            
            if (keysToDelete.isNotEmpty()) {
                val updates = keysToDelete.associate { key ->
                    "history.$key" to com.google.firebase.firestore.FieldValue.delete()
                }
                docRef.update(updates).await()
                Log.d("FirebaseRepository", "Cleaned up ${keysToDelete.size} old history entries")
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error cleaning up history", e)
        }
    }
}
