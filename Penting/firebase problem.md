# Solusi Masalah ID Anak Tidak Tersimpan di Firebase

Jika ID Anak atau data lokasinya tidak tersimpan atau tidak muncul di Firebase, biasanya ada 3 penyebab utama dalam pengembangan Android dengan Firebase. Berikut adalah pemeriksaan yang perlu Anda jalankan beserta solusinya:

### 1. Masalah pada Aturan Keamanan Firebase (Firebase Rules) - *Paling Sering Terjadi*
Secara otomatis, Firebase Realtime Database mengunci akses baca/tulis (*read/write*) jika kita belum mengatur sistem Login (Authentication). Karena aplikasi ini tidak memiliki fitur Login/Register, akses tersebut ditolak oleh database secara sistem.

**Solusi:** Anda harus mengubah **Rules** di Firebase Console menjadi *Test Mode* agar bisa ditulis bebas dari aplikasi.
1. Buka [Firebase Console](https://console.firebase.google.com/) dan pilih Project Anda.
2. Di menu navigasi kiri, pilih **Realtime Database**.
3. Buka tab **Rules**.
4. Ubah valuenya menjadi `true` seperti kode di bawah ini:
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```
5. Klik **Publish**.

> [!WARNING] Peringatan Keamanan
> Mengatur Rules ke `true` artinya siapa saja yang tahu URL database Anda bisa membaca dan melihat data tersebut. Untuk proyek produksi (*live* dan di-*publish* luas), Anda disarankan harus menautkan *Anonymous Authentication* via Firebase Auth. Namun, untuk sekadar pengujian tugas/uji coba awal, cara ini adalah *bypass* paling instan.

### 2. Koneksi Internet Sempat Terputus Saat Pembuatan Pertama ID
Aplikasi mode *Kids* membuat ID pada saat pertama kali "mode Kids" ditekan. Jika pada detik tersebut HP Anak tidak ada koneksi internet (atau koneksi tidak lambat), fungsi inisialisasi ID bisa gagal dikirimkan ke cloud Firebase secara mulus.

**Solusi Tambahan di Kode:**
Beritahukan kendala ini agar skrip bisa diperkuat. Kita bisa memaksa `LocationService.kt` (layanan latar belakang) untuk mencoba menembakkan inisiasi data profil awal anak ke Firebase tiap kali ia mengirimkan lokasi, jadi Anda tidak akan pernah kehilangan data id anak di database sekalipun inisiasi gagal di awal.

### 3. File `google-services.json` Berbeda atau Package Name Keliru
Jika sebelumnya Anda membuat *project* Firebase namun **Package Name** yang Anda masukkan saat membuat project di console itu berbeda dengan nama di aplikasi (yakni `com.googlehome.protect`), aplikasinya tidak akan diizinkan berkomunikasi dengan Firebase.

**Solusi:**
Pastikan Anda mendaftarkan nama *package* `com.googlehome.protect` (*atau `com.googlehome.parent` bila dulu diregister ke app yang berbeda*) di Firebase Console dan mengunduh ulang file `google-services.json` terbaru lalu mem-paste-kannya menimpa file lama di dalam folder `app/`.

### Saran Pengecekan Mandiri (Debugging)
Silakan buka **Logcat** yang ada di sebelah paling bawah Android Studio ketika mencoba aplikasi di Mode Kids. Jika ada penolakan dari Firebase, Anda akan mendapati error tulisan warna merah berbunyi `Permission Denied` atau `DatabaseException`.
