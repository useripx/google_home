# Yogi Ario Smart Protection (Google Home Protect) v10.0

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Compose Version](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-green.svg)](https://developer.android.com/jetpack/compose)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com/)

Aplikasi pelacakan dan keamanan anak komprehensif yang berjalan sepenuhnya di sisi *client* tanpa memerlukan infrastruktur berbayar tambahan. Didesain untuk efisiensi biaya menggunakan Firebase Spark Plan.

## 🚀 Fitur Utama v10.0

1. **Keamanan Mode Orang Tua (OTP)**: Otorisasi akses dashboard menggunakan kode unik yang tersimpan di database. Mendukung pendaftaran perangkat anak secara otomatis.
2. **Heatmap History**: Visualisasi area yang paling sering dikunjungi anak menggunakan lapisan Heatmap pada peta, membantu orang tua menganalisis kebiasaan lokasi anak.
2. **Asisten AI (Yogi Ario)**: Chatbot interaktif menggunakan **Model 3.1**. Kini dengan interaksi lebih manusiawi melalui **jeda respon 5 detik** dan **animasi mengetik** yang dinamis.
3. **Penyamaran Tingkat Lanjut (Stealth Mode)**: Mengubah ikon aplikasi menjadi "Kalkulator" di HP anak dengan antarmuka kalkulator fungsional. (Tahan `=` selama 10 detik untuk akses rahasia).
4. **Data Retention (TTL) 5 Hari**: Pembersihan history lokasi secara otomatis di sisi *client* untuk menghemat Firebase Spark Plan.
5. **Geofencing Client-Side**: Algoritma perhitungan radius batas aman yang diolah langsung oleh HP anak.
6. **Dering Darurat & Pemantauan Jaringan**: Membunyikan ponsel anak dengan volume maksimal dari jarak jauh dan memantau status sinyal internet.
7. **Pelacakan Real-time & Estimasi ETA**: Pembaruan lokasi tiap 10 detik dengan estimasi waktu tiba (ETA).

## 🛠️ Tech Stack & Tools

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase (Firestore, Storage, Auth)
- **AI Engine**: Groq API (Llama-3.1-8b-instant)
- **Maps**: Google Maps SDK & Maps Compose Utils (Heatmap)
- **Local Storage**: Jetpack DataStore

## 🏗️ Architecture Overview

Proyek ini mengikuti pola arsitektur **MVVM** untuk memisahkan logika bisnis dari UI.

## 📋 Changelog Terbaru

Rujuk ke [changelog.md](file:///c:/Users/yogia/AndroidStudioProjects/GoogleHome/changelog.md) untuk melihat riwayat perubahan lengkap.

## 📖 Panduan Instalasi

Silakan rujuk ke file `Dokumentasi v9.md` atau `walkthrough.md` di dalam repositori ini untuk instruksi setup mendetail.
