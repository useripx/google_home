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
                close(e)
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
            
            val updates = hashMapOf<String, Any>(
                "history.$historyKey" to entry,
                "currentLat" to entry.latitude,
                "currentLon" to entry.longitude,
                "battery" to entry.battery,
                "lastSeen" to entry.timestamp
            )
            
            // set with merge is safer than update as it creates the document if it doesn't exist
            docRef.set(updates, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Failed to update location", e)
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
}
