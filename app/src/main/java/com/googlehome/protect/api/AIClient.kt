package com.googlehome.protect.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AIClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val authKey = "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"
    private val apiEndpoint = "https://api.groq.com/openai/v1/chat/completions"
    private val MODEL_NAME = "llama-3.1-8b-instant"

    private val systemPrompt = """
        Anda adalah Yogi Ario, Asisten AI resmi untuk aplikasi Yogi Ario Smart Protection. 
        Tugas Anda adalah membantu orang tua memahami fitur aplikasi seperti Geofencing, Stealth Mode (Kalkulator), Pemantauan Jaringan, dan ETA. 
        Anda juga harus memiliki kemampuan memberikan tutorial cara instalasi dan pengaturan aplikasi di HP anak jika ditanya.
        
        Panduan Singkat Instalasi Mode Anak:
        1. Instal aplikasi Yogi Ario Smart Protection di HP anak.
        2. Buka aplikasi, di halaman awal pilih mode "CHILD" (Anak).
        3. Aplikasi akan meminta izin lokasi, mikrofon (untuk fitur darurat), dan notifikasi. Ijinkan semuanya.
        4. (Penting) Aplikasi akan meminta mematikan "Battery Optimization" agar pelacakan tidak dimatikan paksa oleh sistem.
        5. Setelah itu, HP anak akan memunculkan "Child ID" (misal: YYMMDDxxxx).
        6. Berikan Child ID ini kepada orang tua untuk dimasukkan ke aplikasi versi Parent.
        7. Jika fitur Stealth (Kalkulator) aktif, tekan dan tahan tombol '=' di kalkulator selama 10 detik untuk melihat ID kembali.

        Jawablah selalu dengan ramah, profesional, dan gunakan bahasa Indonesia. Jangan menjawab hal di luar konteks aplikasi keamanan dan perlindungan anak.
    """.trimIndent()

    suspend fun sendMessage(userMessage: String, chatHistory: List<Pair<String, String>>): String {
        return withContext(Dispatchers.IO) {
            try {
                val messagesArray = JSONArray()
                
                // Add system prompt
                val systemMsg = JSONObject()
                systemMsg.put("role", "system")
                systemMsg.put("content", systemPrompt)
                messagesArray.put(systemMsg)

                // Add history
                for (chat in chatHistory) {
                    val userHist = JSONObject()
                    userHist.put("role", "user")
                    userHist.put("content", chat.first)
                    messagesArray.put(userHist)

                    val asstHist = JSONObject()
                    asstHist.put("role", "assistant")
                    asstHist.put("content", chat.second)
                    messagesArray.put(asstHist)
                }

                // Add new message
                val newMsg = JSONObject()
                newMsg.put("role", "user")
                newMsg.put("content", userMessage)
                messagesArray.put(newMsg)

                val requestBodyJson = JSONObject()
                requestBodyJson.put("model", MODEL_NAME)
                requestBodyJson.put("messages", messagesArray)
                requestBodyJson.put("temperature", 0.7)

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = requestBodyJson.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(apiEndpoint)
                    .addHeader("Authorization", "Bearer ${authKey}")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    val jsonResponse = JSONObject(responseData)
                    val choices = jsonResponse.getJSONArray("choices")
                    if (choices.length() > 0) {
                        val messageObj = choices.getJSONObject(0).getJSONObject("message")
                        return@withContext messageObj.getString("content")
                    }
                }
                
                val errorMessage = when(response.code) {
                    400 -> "Permintaan tidak valid (Error 400). Mohon hubungi pengembang."
                    401 -> "Kunci API tidak valid (Error 401)."
                    404 -> "Layanan AI tidak ditemukan (Error 404). Silakan periksa endpoint."
                    429 -> "Terlalu banyak permintaan. Silakan tunggu sebentar."
                    else -> "Kesalahan jaringan (${response.code})."
                }
                return@withContext "Maaf, saya sedang mengalami kendala: $errorMessage"
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext "Maaf, terjadi kesalahan tak terduga. Mohon periksa koneksi internet Anda."
            }
        }
    }
}
