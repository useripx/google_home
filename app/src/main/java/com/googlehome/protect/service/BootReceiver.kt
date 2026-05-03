package com.googlehome.protect.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.googlehome.protect.data.ModeManager
import com.googlehome.protect.model.AppMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val modeManager = ModeManager(context)
            
            scope.launch {
                val appMode = modeManager.appMode.first()
                if (appMode == AppMode.KIDS) {
                    startLocationService(context)
                }
            }
        }
    }

    private fun startLocationService(context: Context) {
        val serviceIntent = Intent(context, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
