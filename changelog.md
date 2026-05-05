# Changelog

All notable changes to this project will be documented in this file.

## [9.0.0] - 2026-05-05

### Fitur Baru & Peningkatan UX
- **Heatmap History**: Menambahkan lapisan Heatmap pada peta untuk melihat intensitas kunjungan lokasi anak secara visual.
- **Interaksi AI Manusiawi**: Menambahkan jeda respon 5 detik pada Asisten AI untuk simulasi proses berpikir.
- **Typing Animation**: Implementasi animasi titik-titik bergerak ("Yogi Ario sedang mengetik....") pada chat assistant.
- **Global Upgrade v9.0**: Memperbarui identitas versi di seluruh aplikasi dan dokumentasi.

### Dokumentasi
- Mengupdate `README.md` dan `ringkasandanfitur.md`.
- Memperbarui dokumentasi teknis mendalam ke file `Dokumentasi v9.md`.

## [8.1.0] - 2026-05-05

### Perbaikan UI & UX Dashboard
- **Optimasi Gesture Peta**: Menonaktifkan swipe gesture untuk menu drawer saat berada di tab *Devices* (Map). Pengguna kini dapat berinteraksi dengan peta tanpa terganggu menu yang terbuka tidak sengaja.
- **Responsivitas Asisten AI**: Memperbaiki tata letak layar *Assistant Chat* agar kotak input teks tidak tertutup oleh bar navigasi bawah.
- **Konsistensi Dark Mode**: Menyelaraskan tema warna pada menu *Settings* dan elemen UI lainnya. Menghilangkan latar belakang putih yang tidak sinkron saat mode gelap aktif.
- **Fitur Percakapan Baru**: Menambahkan tombol "+" di layar asisten AI untuk memudahkan pengguna membersihkan riwayat chat dan memulai sesi baru.

### Pembaruan AI (Groq)
- **Model Upgrade**: Memperbarui model AI ke `llama-3.1-8b-instant` untuk stabilitas dan kecepatan respons yang lebih baik.
- **Enhanced Error Handling**: Menambahkan logika penanganan error yang lebih detail (400, 401, 404, 429) untuk memberikan informasi jaringan yang akurat kepada pengguna.

## [8.0.0] - 2026-05-03

### Integrasi AI & Pembaruan UI
- **Asisten AI Yogi Ario**: Mengganti tab *Safety* dengan *Chatbot* interaktif yang ditenagai oleh Groq API (Llama 3). AI ini didesain khusus untuk membantu orang tua memahami fitur aplikasi dan memandu proses instalasi di HP anak.
- **Pintasan Pertanyaan (Chips)**: Menambahkan tombol *template* pertanyaan di layar *chat* untuk mempermudah orang tua bertanya seputar Mode Anak, Geofencing, dan fitur lainnya.
- **Relokasi Fitur Keamanan**: Memindahkan pengaturan konfigurasi Geofence ke tab *Settings* agar antarmuka obrolan AI bisa tampil maksimal.
- **Branding Lengkap v8.0**: Mengubah semua label versi pada aplikasi menjadi versi 8.0.

## [7.1.0] - 2026-05-03

### Optimalisasi & Penyamaran
- **Dynamic Icon (Kalkulator Palsu)**: Menambahkan fitur *Stealth Mode* di layar anak yang akan menyembunyikan ikon Google Home menjadi "Kalkulator".
- **Fake Calculator UI**: Saat mode Kalkulator aktif, aplikasi akan terlihat dan berfungsi seperti kalkulator sungguhan. Menahan tombol `=` selama 10 detik akan membuka menu rahasia (Easter Egg).
- **Branding Update**: Mengubah nama aplikasi di Dashboard Parent menjadi "Yogi Ario Smart Protection v7.1".

## [6.0.0] - 2026-05-03

### Optimalisasi & Pemeliharaan
- **Data Retention Policy (Client-Side TTL)**: Menambahkan fitur pembersihan otomatis riwayat lokasi yang berusia lebih dari 5 hari untuk mencegah pembengkakan ukuran database (hemat biaya/Spark Plan).
- **Efisiensi Geofencing**: Mempertahankan komputasi Geofencing secara lokal di perangkat anak untuk menghindari kebutuhan infrastruktur berbayar (Cloud Functions).

## [5.1.0] - 2026-05-03

### Ditambahkan
- **Network Status Monitoring**: Visibilitas jenis jaringan anak (WiFi, Seluler Lemah/Kuat, atau Offline) ke riwayat lokasi.
- **Remote Ring (Dering Darurat)**: Kemampuan membunyikan perangkat anak dengan volume penuh dari jarak jauh untuk menemukan ponsel yang hilang atau menarik perhatian.
- **Izin Baru**: `READ_PHONE_STATE` ditambahkan untuk akurasi sinyal seluler.

## [4.0.0] - 2026-05-03
### Added
- **AI Predictive (Free Edition)**: Estimasi waktu sampai (ETA) manual tanpa Google Maps API berbayar.
- **Parent ID Hierarchy**: Struktur database baru menggunakan IDParent (YYMMDDXXXX).
- **Floating ETA Widget**: Informasi waktu tiba real-time di atas peta.
- **Custom Anomaly Alert**: Pesan peringatan keamanan baru yang lebih personal.
- **Home Location Management**: Pengaturan titik rumah untuk dasar perhitungan ETA.
- **Hidden Panic Button**: Pemicu darurat tersembunyi dengan menekan tombol volume 5 kali, otomatis merekam audio 30 detik.
- **Firebase Storage Integration**: Pengiriman rekaman darurat ke cloud secara real-time.
- **Safe Arrival Notification**: Lansiran otomatis ketika anak memasuki radius geofencing yang ditentukan.

## [3.0.0] - 2026-05-03

### Added
- **Major UI Redesign**: Swapped Top Bar order for better aesthetics. Google Home header is now at the top, followed by the Status Bar and scrollable child chips.
- **Device Management**: Added a dedicated management section in Settings to view and remove connected devices with a confirmation dialog.
- **Advanced Tracking Settings**: Added configurable tracking intervals (10s, 30s, 1m, 5m) and an "Auto Power Saving" toggle.
- **Geofencing UI**: Implemented a Geofencing configuration section with a radius slider (100m - 2km) and an activation toggle.
- **Data Export**: Integrated CSV (Machine Learning dataset format) and PDF (Formal Report format) export features with file sharing capabilities.
- **Battery Critical Alerts**: Real-time Snackbar notification in Parent Dashboard when a child's battery drops below 15%.
- **History Management**: Added a feature to permanently clear location history for a specific device.

### Changed
- **Location Throttling**: The background service now dynamically adjusts tracking intervals based on server-side settings and battery levels (Auto Power Saving).
- **Settings Tab**: Fully overhauled the Settings tab into a functional dashboard.

### Fixed
- **Top Bar Gaps**: Eliminated unwanted white space between the status bar and header by optimizing `WindowInsets`.
- **DataStore Sync**: Fixed issues with local settings not persisting correctly across sessions.


## [1.2.0] - 2026-05-03

### Added
- **Direct Maps Navigation**: Added a shortcut button in the Parent Dashboard to open the Google Maps app and instantly start navigating to the child's exact coordinates. Includes a web-browser fallback if the app is missing.
- **Map Zoom Controls**: Enabled native zoom in/out controls on the parent's map view for easier navigation.

### Changed
- **Enhanced Polyline UI/UX**: Improved the route history drawing by changing the line color to bright red, increasing thickness, and adding a green start marker.
- **Performance Optimization**: Limited the polyline drawing to the last 100 location points to prevent rendering lag on extended usage.

### Fixed
- **Permission Denied Crash**: Fixed a fatal crash in the Parent Dashboard occurring when Firestore Security Rules block read access.
- **Anonymous Authentication**: Integrated `FirebaseAuth.signInAnonymously()` seamlessly on app startup to satisfy secure Firestore Rules without requiring explicit user login.

## [1.1.0] - 2026-05-03

### Added
- **Fullscreen Map UI**: Redesigned Parent Dashboard utilizing `BottomSheetScaffold` for maximum map visibility.
- **Polyline Tracking**: Visual route drawing on Google Maps to display the child's movement history.
- **Multiple Children Support**: Ability to track and quickly switch between multiple children using UI chips.
- **Child Renaming Feature**: Easily assign and change custom names for tracked devices via a prompt dialog after connection or through an edit icon.
- **Battery Optimization Bypass Prompt**: Added forced prompt on Kids mode to ignore battery optimizations, preventing the system from killing the background tracker.

### Fixed
- Fixed build error caused by `<adaptive-icon>` API level mismatch by moving adaptive icons to `mipmap-anydpi-v26`.
- Adjusted `minSdk` to 24 (Android 7.0) to broaden target device compatibility.

## [1.0.0] - 2026-05-03

### Added
- Initial project structure for "Sayang Anak" (disguised as Google Home).
- Dual-mode implementation: **Parent** and **Kids**.
- **Parent Mode Features**:
    - Real-time location tracking (10s interval).
    - Child management (Add/Switch Child).
    - Google Maps integration for location visualization.
    - Navigation to Google Maps app.
    - Notification trigger to wake up Child app.
    - Location history storage and viewing.
- **Kids Mode Features**:
    - Disguised UI as "System Update" screen.
    - Background location tracking and reporting to Firebase.
    - Boot receiver to start service on device startup.
    - Hidden Child ID view (Easter Egg).
- **Core Infrastructure**:
    - MVVM Architecture.
    - Firebase Firestore integration for data storage.
    - Foreground service for persistent location tracking.
    - Modern UI using Jetpack Compose.
