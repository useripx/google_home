# Rencana Implementasi: Parent Dashboard Desktop (v1.0)

Rencana ini merinci langkah-langkah teknis untuk membangun aplikasi desktop **Yogi Ario Smart Protection** menggunakan **.NET MAUI** dan **Visual Studio 2022 Enterprise**.

## 🏗️ Fase 1: Persiapan Lingkungan & Dependensi
Tujuan: Menyiapkan proyek agar bisa terhubung ke layanan eksternal (Firebase & Groq).

1.  **Instalasi Package NuGet**:
    - `Google.Cloud.Firestore`: Untuk sinkronisasi data lokasi real-time.
    - `FirebaseStorage.net`: Untuk mengakses rekaman suara darurat.
    - `Newtonsoft.Json`: Untuk pemrosesan data API.
    - `Microsoft.Maui.Controls.Maps`: Untuk integrasi peta dasar.
2.  **Konfigurasi Firebase**:
    - Masukkan file kredensial JSON (Service Account) ke dalam folder `Resources/Raw`.
    - Buat class `FirebaseService.cs` untuk inisialisasi koneksi Firestore.

## 🔐 Fase 2: Autentikasi & Jembatan Data
Tujuan: Menghubungkan desktop dengan Parent ID yang sudah ada di Android.

1.  **Login Parent ID**:
    - Buat layar input sederhana untuk memasukkan **Parent ID**.
    - Simpan ID tersebut secara lokal menggunakan `Preferences` di .NET MAUI.
2.  **Model Data**:
    - Salin struktur model dari Android (Child, LocationEntry) menjadi class C#.

## 🎨 Fase 3: Pengembangan UI (Modern Dark Mode)
Tujuan: Membangun antarmuka yang elegan sesuai estetika aplikasi mobile.

1.  **Layout Utama (Shell)**:
    - Gunakan `AppShell.xaml` untuk navigasi sidebar.
    - Tab: **Peta (Devices)**, **Asisten AI**, **Riwayat**, dan **Pengaturan**.
2.  **Halaman Chat (Asisten AI)**:
    - Gunakan `CollectionView` untuk daftar pesan.
    - Implementasikan animasi mengetik menggunakan `Dispatcher.StartTimer`.

## 🗺️ Fase 4: Integrasi Peta & Heatmap
Tujuan: Visualisasi lokasi anak di layar PC.

1.  **Google Maps via WebView2**:
    - Karena .NET MAUI Maps memiliki keterbatasan fitur, gunakan `WebView2` untuk memuat halaman HTML khusus yang menjalankan Google Maps JavaScript API.
2.  **Implementasi Heatmap**:
    - Kirim data koordinat dari C# ke JavaScript di WebView untuk merender lapisan heatmap.

## 🤖 Fase 5: Integrasi Groq AI & Audio
Tujuan: Memindahkan fitur pintar ke Desktop.

1.  **Groq API Service**:
    - Buat layanan HTTP Client untuk mengirim pesan ke model `llama-3.1-8b-instant`.
    - Tambahkan jeda 5 detik (Task.Delay) sebelum menampilkan respon.
2.  **Pemutar Audio Darurat**:
    - Gunakan `CommunityToolkit.Maui.MediaElement` untuk memutar audio rekaman darurat yang diunduh dari Firebase Storage.

## 📦 Fase 6: Build & Distribusi
Tujuan: Menghasilkan file instalasi Windows.

1.  **Publishing**:
    - Gunakan fitur **"Publish"** di Visual Studio 2022 Enterprise.
    - Pilih target **Windows Machine (win10-x64)**.
2.  **Installer**:
    - Buat file `.msix` atau `.exe` menggunakan *Windows App SDK*.

---

### 📝 Catatan untuk Pengembang:
Selalu merujuk pada file `desktop.md` untuk spesifikasi arsitektur teknis dan `ringkasandanfitur.md` untuk pemahaman alur kerja aplikasi secara keseluruhan.
