package com.googlehome.protect.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

class EmergencyRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): File? {
        try {
            outputFile = File(context.cacheDir, "emergency_${System.currentTimeMillis()}.m4a")
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }
            Log.d("EmergencyRecorder", "Recording started: ${outputFile?.absolutePath}")
            return outputFile
        } catch (e: Exception) {
            Log.e("EmergencyRecorder", "Start recording failed", e)
            return null
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            Log.d("EmergencyRecorder", "Recording stopped")
        } catch (e: Exception) {
            Log.e("EmergencyRecorder", "Stop recording failed", e)
        }
    }
}
