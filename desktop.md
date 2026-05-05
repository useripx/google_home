# Panduan Pengembangan Desktop: Yogi Ario Smart Protection

Dokumen ini disusun sebagai panduan bagi pengembang atau agen AI untuk membangun aplikasi **Parent Dashboard** versi Windows (.exe) menggunakan **Visual Studio 2022 Enterprise**.

## 🎯 Tujuan Projek
Membangun aplikasi desktop yang memungkinkan orang tua memantau lokasi anak, menganalisis heatmap, dan berinteraksi dengan asisten AI secara real-time langsung dari PC/Laptop.

## 🛠️ Spesifikasi Lingkungan
- **IDE**: Visual Studio 2022 Enterprise.
- **Framework Rekomendasi**: 
  - **.NET MAUI** (Jika ingin mendukung Cross-platform Windows/macOS).
  - **WinUI 3 / WPF** (Untuk performa Windows native yang maksimal).
- **Bahasa**: C# (.NET 8.0).

## 🧩 Arsitektur Data (Sinkronisasi Android)
Aplikasi desktop harus terhubung ke Firebase yang sama dengan aplikasi Android:
- **Database**: Cloud Firestore.
- **Collections**:
  - `parents/{parentId}`: Data profil orang tua.
  - `parents/{parentId}/children/{childId}`: Status real-time anak (lat, lng, sinyal, baterai).
  - `parents/{parentId}/children/{childId}/history`: Data koordinat untuk Polyline dan Heatmap.
- **Storage**: Firebase Storage (untuk mengunduh rekaman darurat `.wav` atau `.mp3`).

## 🚀 Fitur Utama yang Harus Diimplementasi

### 1. Pelacakan Real-time (Maps)
- **Komponen**: Gunakan `WebView2` di Visual Studio untuk merender Google Maps JavaScript API.
- **Heatmap**: Implementasikan lapisan heatmap menggunakan data dari sub-koleksi `history`.
- **ETA**: Porting logika kalkulasi ETA dari Kotlin ke C#.

### 2. Chat Assistant (Yogi Ario AI)
- **Engine**: Groq API (Llama-3.1-8b-instant).
- **UX**: Implementasikan animasi mengetik dan jeda 5 detik untuk konsistensi dengan versi mobile.
- **History**: Chat history bisa disimpan secara lokal (SQLite/LiteDB) atau di Firestore.

### 3. Monitoring Sinyal & Baterai
- Menampilkan indikator kekuatan sinyal dan persentase baterai HP anak secara visual di sidebar atau tray icon Windows.

### 4. Perekaman Darurat
- Aplikasi desktop harus memberikan notifikasi *pop-up* Windows jika ada rekaman baru yang diunggah ke Firebase Storage.

## 🔑 Langkah Migrasi (Daftar Tugas)
1. **Setup Firebase Admin SDK** untuk C# di Visual Studio.
2. **Desain UI** menggunakan XAML (sesuaikan dengan estetika Dark Mode aplikasi Android).
3. **Pindahkan Logika Bisnis**:
   - Konversi `FirebaseRepository.kt` menjadi `FirebaseService.cs`.
   - Konversi `GroqApiClient.kt` menjadi `GroqService.cs`.
4. **Optimasi Performa**: Gunakan *Async/Await* untuk memastikan UI Windows tidak membeku saat mengambil data peta yang besar.

## 🔗 Referensi Repositori
- **Android Source**: `https://github.com/useripx/google_home.git`
- **Branch Referensi**: `main` (v9.0)
