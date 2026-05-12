# Rencana Peningkatan Keamanan Firebase & Aplikasi (Revisi)

Terima kasih atas masukannya. Berikut ini adalah pembaruan rencana (Implementation Plan) dan jawaban atas pertanyaan-pertanyaan Anda.

## 1. Efek Mengubah Package Name menjadi `com.dooglehome.protect`

> **Pertanyaan:** "saya ingin ganti menjadi com.dooglehome.protect apa efeknya?"

**Jawaban:**
Mengganti *package name* (nama paket) sangat bisa dilakukan, tetapi memiliki efek yang cukup besar karena nama paket adalah "KTP" dari aplikasi Anda. Efeknya:

- **Harus mendaftar ulang di Firebase:** Anda **wajib** menambahkan aplikasi Android baru di Firebase Console Anda dengan nama paket `com.yogiario.protect`, lalu mengunduh file `google-services.json` yang baru dan menimpanya di folder `app/` Android Studio Anda. Jika tidak, Firebase (Auth & Database) akan menolak koneksi.
- **Perubahan Kode:** Semua file di dalam proyek yang bertuliskan `package com.googlehome.protect` harus diubah (refactor) menjadi `com.dooglehome.protect`. Saya akan memandu Anda untuk melakukan ini nanti.

## 2. Halaman Login Khusus Orang Tua

> **Pertanyaan:** "mending gini saja ada halaman login untuk orang tua dengan google atau email ya soalnya saya inginnya orang tua mendaftar ke saya begitu jadi saya tahu apakah ini orang tua beneran pantau anak atau ingin disalah gunakan"

**Jawaban & Rencana:**
Sangat setuju. Kita akan menambahkan **Halaman Login dengan Email dan Password** khusus jika seseorang memilih mode "PARENT".
**Alurnya:**

1. Orang tua membuka aplikasi -> Pilih Mode "PARENT".
2. Muncul Halaman Login/Register.
3. Mereka mendaftar menggunakan Email. Anda dapat melihat daftar email ini di tab **Authentication** di Firebase Console Anda. Jika ada email mencurigakan, Anda bisa langsung menghapusnya/memblokirnya.
4. Setelah berhasil login, mereka akan mendapatkan akses ke Dashboard.

*(Catatan: Menggunakan Email/Password lebih mudah dan cepat disiapkan dibandingkan Google Sign-In yang membutuhkan kunci SHA-1 dan konfigurasi tambahan).*

## 3. Sistem "Menunggu Persetujuan" (Parent Approval)

> **Komentar Anda:** "ya benar akan melakukan pemantauan jika ID anak sudah terkait dengan orang tua"

**Rencana Implementasi Kode:**
Di `MainActivity.kt` dan `LocationService.kt`, saya akan memprogram agar aplikasi Anak diam (hanya terlihat seperti kalkulator biasa) sebelum ID-nya diinputkan oleh akun Parent yang sudah login tadi.

## 4. Cara Menerapkan Firestore Rules

> **Pertanyaan:** "jadi saya masukkan ini di rules atau dimana?" dan "ya boleh pandu saya seperti memandu programmer awalan ya"

**Panduan Langkah-demi-Langkah untuk Anda:**
Kita tidak mengubah kode Rules ini di Android Studio, melainkan langsung di website Firebase. Nanti setelah kita merombak kode Android-nya, Anda harus melakukan ini:

1. Buka browser dan pergi ke [Firebase Console](https://console.firebase.google.com/).
2. Pilih proyek Anda (`com-homeprotect` atau yang serupa).
3. Di menu sebelah kiri, klik **Firestore Database**.
4. Di bagian atas halaman Firestore, klik tab **Rules** (Aturan).
5. Anda akan melihat kotak teks berisi kode. Hapus **semua** teks di dalam kotak tersebut.
6. Salin kode di bawah ini, dan tempel (paste) ke kotak tersebut.
7. Klik tombol biru **Publish** (Publikasikan) di kanan atas.

**Kode Rules yang Harus Disalin (Nanti):**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
  
    match /tracking_units/{unitId} {
      // Anak HANYA bisa mengubah datanya sendiri
      allow update: if request.auth != null && resource.data.authUid == request.auth.uid;
      // Orang Tua HANYA bisa membaca/mengubah jika mereka adalah pemilik
      allow read, update, delete: if request.auth != null && resource.data.parentId == request.auth.uid;
      // Mengizinkan inisialisasi awal oleh anak
      allow create: if request.auth != null && request.resource.data.authUid == request.auth.uid;
    }

    match /parents/{parentId} {
      // Parent hanya bisa mengakses datanya sendiri
      allow read, write: if request.auth != null && request.auth.uid == parentId;
    }
  }
}
```

## User Review Required

> [!IMPORTANT]
> **Keputusan Anda Diperlukan:**
>
> 1. Apakah Anda setuju kita menggunakan **Email/Password** saja dulu untuk halaman Parent (lebih cepat diterapkan)?
> 2. Apakah Anda siap untuk mengganti `google-services.json` yang baru di folder `app/` jika kita langsung merombak *package name* ke `com.dooglehome.protect` hari ini?

Jika Anda setuju dengan **KEDUA HAL** di atas, silakan balas dengan **"Setuju, silakan ubah kodenya"**. Saya akan langsung bekerja membuatkan halamannya dan merombak *package name*-nya.
