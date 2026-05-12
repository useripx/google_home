# Ringkasan Aplikasi & Fitur: Yogi Ario Smart Protection v10.0

Dokumen ini disusun untuk analisis AI dan dokumentasi fitur utama aplikasi Google Home Protect (Sayang Anak) versi terbaru.

## Fitur Unggulan v10.0 (Baru)

### 1. Keamanan Otorisasi (OTP Activation)

- **Verifikasi Kode Unik**: Menambahkan gerbang keamanan di mana Mode Orang Tua wajib memasukkan kode aktivasi yang diverifikasi langsung ke database sebelum dapat mengakses dashboard.
- **Auto-Registration Perangkat Anak**: Begitu orang tua terverifikasi, perangkat anak yang mereka tambahkan akan otomatis terdaftar di bawah akun mereka di database tanpa perlu input manual di sisi admin.

### 2. Heatmap History (Analisis Jejak Lokasi)

- **Visualisasi Peta Panas**: Menampilkan area yang paling sering dikunjungi anak dengan gradasi warna di atas peta.
- **Wawasan Orang Tua**: Membantu mengidentifikasi lokasi "nongkrong" anak secara visual tanpa harus melihat log koordinat satu per satu.

### 2. Peningkatan Interaksi Asisten AI

- **Human-like Delay**: Jeda 5 detik sebelum membalas pesan.
- **Typing Animation**: Teks "Yogi Ario sedang mengetik...." dengan animasi titik bergerak untuk memberikan kesan interaksi manusiawi.

## Fitur Utama Lainnya

### 1. Hirarki Akun (Parent ID)

- Struktur database NoSQL berbasis `parents` collection.
- ID Parent unik format tanggal (YYMMDDXXXX).
- Mengamankan koneksi antara banyak anak di bawah satu kendali orang tua.

### 2. AI Predictive Tracking (Free Edition)

- **Estimasi ETA**: Perhitungan waktu tiba manual tanpa API berbayar menggunakan algoritma jarak Haversine dan Road Correction Factor.
- **Anomaly Awareness**: Kerangka kerja deteksi perilaku mencurigakan berdasarkan rutinitas harian.

### 3. Keselamatan Aktif (Emergency Response) & Pemantauan Lebih Luas

- **Panic Button Tersembunyi**: Pemicu darurat mode diam pada perangkat anak (tekan tombol volume 5 kali). Merekam audio sekitar selama 30 detik secara background dan mengirimnya ke Firebase Storage.
- **Safe Arrival Notification**: Pemberitahuan otomatis ketika lokasi anak terdeteksi memasuki radius Geofencing yang telah ditetapkan orang tua.
- **Remote Ring (Dering Darurat)**: Orang tua dapat mengaktifkan alarm pada perangkat anak dengan volume penuh jika perangkat terselip atau anak tidak merespons panggilan.
- **Monitoring Status Jaringan**: Memantau koneksi perangkat anak secara real-time (WiFi, Sinyal Seluler Lemah/Kuat, atau Offline) untuk menjustifikasi kemungkinan hilangnya sinyal GPS.

### 4. Asisten AI Pintar (v8.0)

- **Chatbot Terintegrasi**: Menggantikan tab "Safety" dengan asisten virtual interaktif (Yogi Ario) yang ditenagai oleh Model 3.1.
- **Panduan & Bantuan**: Menyediakan panduan instalasi, penjelasan fitur, dan jawaban seputar penggunaan aplikasi secara langsung.
- **Pintasan Cerdas**: Tombol kueri cepat untuk pertanyaan-pertanyaan yang paling sering diajukan oleh orang tua.
- **Pembaruan v8.1**: Update model ke `llama-3.1-8b-instant` dan perbaikan responsivitas UI chat.

### 5. Penyamaran Tingkat Lanjut (Stealth Mode)

- **Dynamic Icon (Kalkulator Palsu)**: Fitur yang memungkinkan perubahan ikon dan nama aplikasi (dari Google Home menjadi Kalkulator) di perangkat anak menggunakan `activity-alias`.
- **UI Kamuflase Interaktif**: Aplikasi akan menampilkan kalkulator fungsional jika mode ini aktif. Menu utama disembunyikan di balik Easter Egg: menahan tombol `=` selama 10 detik.

### 6. Optimalisasi & Skalabilitas Database (v6.0)

- **Data Retention (Client-Side TTL)**: Pembersihan otomatis data koordinat riwayat yang berusia lebih dari 5 hari secara lokal di sisi anak untuk mencegah pembengkakan kuota Firestore (Cocok untuk Firebase Spark Plan).
- **Efisiensi Perhitungan**: Logika Geofence tetap dipertahankan pada sisi *client* (perangkat anak) yang berjalan secara asinkron tanpa membebani aplikasi orang tua atau memerlukan komputasi Cloud Functions berbayar.

### 7. Pelacakan Real-Time (v3.0)

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
- **Arsitektur**: MVVM (Model-View-ViewModel).
- **Backend**: Firebase Firestore (NoSQL), Storage, & Auth.
- **AI Engine**: Model 3.1
- **Storage Lokal**: Jetpack DataStore.
- **Peta**: Google Maps SDK for Android.
- **Layanan Latar Belakang**: Foreground Service (LocationService) untuk mode Kids.

## Tujuan Analisis AI

Dokumen ini dapat digunakan oleh AI untuk memahami konteks codebase, merencanakan fitur prediksi lokasi berbasis ML (menggunakan data CSV), atau melakukan audit keamanan pada alur sinkronisasi data.
