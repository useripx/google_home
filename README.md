# Yogi Ario Smart Protection (Google Home Protect) v8.1

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![Compose Version](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-green.svg)](https://developer.android.com/jetpack/compose)
[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com/)

Aplikasi pelacakan dan keamanan anak komprehensif yang berjalan sepenuhnya di sisi *client* tanpa memerlukan infrastruktur berbayar tambahan. Didesain untuk efisiensi biaya menggunakan Firebase Spark Plan.

## 🚀 Fitur Utama v8.1

1.  **Asisten AI (Yogi Ario)**: Chatbot interaktif menggunakan **Llama 3.1 (Groq API)** untuk memandu orang tua dalam memahami fitur dan melakukan instalasi perangkat anak. Kini lebih responsif, stabil, dan dilengkapi fitur **Percakapan Baru (+)**.
2.  **Penyamaran Tingkat Lanjut (Stealth Mode)**: Mengubah ikon aplikasi menjadi "Kalkulator" di HP anak dengan antarmuka kalkulator fungsional. (Tahan `=` selama 10 detik untuk akses rahasia).
3.  **Data Retention (TTL) 5 Hari**: Pembersihan history lokasi secara otomatis di sisi *client* untuk menghemat Firebase Spark Plan.
4.  **Geofencing Client-Side**: Algoritma perhitungan radius batas aman yang diolah langsung oleh HP anak.
5.  **Dering Darurat & Pemantauan Jaringan**: Membunyikan ponsel anak dengan volume maksimal dari jarak jauh dan memantau status sinyal internet (WiFi/Seluler).
6.  **Pelacakan Real-time & Estimasi ETA**: Pembaruan lokasi tiap 10 detik dengan estimasi waktu tiba (ETA) berbasis algoritma internal.
7.  **Perekaman Darurat**: Pemicu rahasia (tekan tombol volume 5 kali) untuk merekam audio sekitar dan mengunggahnya ke Cloud Storage secara otomatis.

## 🛠️ Tech Stack & Tools

-   **Language**: Kotlin
-   **UI Framework**: Jetpack Compose (Modern Declarative UI)
-   **Architecture**: MVVM (Model-View-ViewModel)
-   **Backend**: 
    -   Firebase Firestore (Real-time NoSQL Database)
    -   Firebase Storage (Audio Emergency Recording)
    -   Firebase Auth (Anonymous Authentication)
-   **AI Engine**: Groq API (Llama-3.1-8b-instant)
-   **Maps**: Google Maps SDK for Android
-   **Local Storage**: Jetpack DataStore

## 🏗️ Architecture Overview

Proyek ini mengikuti pola arsitektur **MVVM (Model-View-ViewModel)** untuk memisahkan logika bisnis dari UI, memastikan kode mudah diuji dan dikembangkan.

-   **Model**: Representasi data dari Firebase Firestore (Child, LocationEntry, dll).
-   **View**: Komponen UI yang dibangun dengan Jetpack Compose (`ParentDashboard`, `AssistantChatScreen`).
-   **ViewModel**: Menangani logika bisnis, sinkronisasi data dengan Firebase, dan manajemen state UI menggunakan `StateFlow`.
-   **Repository**: Abstraksi akses data ke Firebase dan DataStore.

## 📋 Changelog Terbaru
Rujuk ke [changelog.md](file:///c:/Users/yogia/AndroidStudioProjects/GoogleHome/changelog.md) untuk melihat riwayat perubahan lengkap.

## 📖 Panduan Instalasi
Silakan rujuk ke file `Dokumentasi v8.md` atau `walkthrough.md` di dalam repositori ini untuk instruksi setup mendetail.
