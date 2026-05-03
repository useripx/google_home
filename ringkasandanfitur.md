# Ringkasan Aplikasi & Fitur: Google Home Protect v3.0

Dokumen ini disusun untuk analisis AI dan dokumentasi fitur utama aplikasi Google Home Protect (Sayang Anak).

## Deskripsi Singkat
Aplikasi pemantauan lokasi anak secara real-time yang menggunakan Firebase Firestore sebagai backend. Aplikasi mendukung dua mode: **Parent** (untuk memantau) dan **Kids** (untuk membagikan lokasi).

## Fitur Utama (v3.0)

### 1. Pelacakan Real-Time
- Pembaruan lokasi otomatis dengan interval yang dapat dikonfigurasi (10 detik - 5 menit).
- Tampilan peta interaktif dengan rute perjalanan yang dilalui.
- Penanda lokasi terakhir (Last Seen) beserta status baterai.

### 2. Manajemen Perangkat (Baru di v3.0)
- Menambah perangkat anak via ID Unik.
- Menghapus perangkat dari daftar pantauan dengan konfirmasi keamanan.
- Mengubah nama perangkat untuk kemudahan identifikasi.

### 3. Pengaturan Lanjutan & Efisiensi
- **Interval Tracking**: Kontrol frekuensi update dari sisi orang tua.
- **Auto Power Saving**: Memperlambat update secara otomatis jika baterai HP anak < 20%.
- **Geofencing**: Pengaturan radius aman (100m - 2km) dengan toggle aktivasi.

### 4. Lansiran & Keamanan
- **Battery Critical Alert**: Notifikasi otomatis saat baterai anak mencapai 15%.
- **Hapus Riwayat**: Opsi untuk membersihkan data koordinat lama untuk privasi.

### 5. Pelaporan & Analisis Data
- **Ekspor CSV**: Format dataset mentah untuk analisis data atau Machine Learning.
- **Ekspor PDF**: Laporan formal perjalanan untuk dokumentasi umum.

## Arsitektur Teknis
- **Bahasa**: Kotlin (Jetpack Compose).
- **Backend**: Firebase Firestore (NoSQL).
- **Storage Lokal**: Jetpack DataStore.
- **Peta**: Google Maps SDK for Android.
- **Layanan Latar Belakang**: Foreground Service (LocationService) untuk mode Kids.

## Tujuan Analisis AI
Dokumen ini dapat digunakan oleh AI untuk memahami konteks codebase, merencanakan fitur prediksi lokasi berbasis ML (menggunakan data CSV), atau melakukan audit keamanan pada alur sinkronisasi data.
