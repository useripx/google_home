# Dokumentasi Resmi: Yogi Ario Smart Protection v8.0

Dokumen ini adalah ringkasan teknis dan fungsional dari aplikasi perlindungan anak **Yogi Ario Smart Protection** hingga versi **8.0**.

## Fitur Utama

### 1. Asisten AI Pintar (Yogi Ario)
- Aplikasi orang tua (Parent Dashboard) kini memiliki tab khusus bernama **Asisten AI**.
- Asisten ini ditenagai oleh model kecerdasan buatan dari Groq API (`llama3-8b-8192`).
- Asisten bisa memberikan bantuan panduan penggunaan, cara instalasi Mode Anak, hingga penjelasan cara kerja fitur keamanan seperti geofencing dan pemantauan sinyal.
- Dilengkapi dengan *template* pintasan (*Chips*) untuk pertanyaan cepat.

### 2. Penyamaran Ikon Tingkat Lanjut (Stealth Mode)
- **Dynamic Icon**: Ikon aplikasi di perangkat anak bisa diubah secara dinamis dari ikon standar menjadi ikon "Kalkulator". Ini mencegah anak (atau pihak lain) menghapus aplikasi tanpa sadar.
- **Fake Calculator UI**: Jika aplikasi dibuka dalam mode ini, tampilannya adalah kalkulator hitam yang berfungsi penuh.
- **Akses Rahasia**: Untuk menampilkan status koneksi (Child ID) atau mengembalikan ikon aslinya, pengguna harus menahan tombol Sama Dengan (`=`) selama tepat **10 detik**.

### 3. Pemantauan Real-Time & ETA
- Melacak lokasi anak dengan jeda waktu minimal 10 detik.
- Kalkulasi estimasi waktu tiba (*Estimated Time of Arrival*) ke target rumah menggunakan perhitungan matriks jarak lokasi berdasarkan garis lintang dan bujur.

### 4. Mode Darurat & Status Jaringan
- Tombol rahasia di perangkat anak (menekan volume 5 kali) akan diam-diam merekam suara sekitar selama 30 detik dan mengirimkannya secara nirkabel ke *dashboard* orang tua.
- Aplikasi orang tua juga memantau seberapa kuat sinyal seluler di perangkat anak (Lemah, Sedang, Kuat, atau terhubung WiFi) untuk menjelaskan alasan mengapa terkadang GPS mungkin tertunda.
- Dering Jarak Jauh (*Remote Ring*) memungkinkan orang tua menyalakan alarm dengan volume 100% pada HP anak untuk mencari HP yang terselip.

### 5. Penghematan Firestore (Data Retention)
- Untuk mencegah membengkaknya ukuran database (mengingat aplikasi menggunakan *Firebase Spark Plan* yang gratis), aplikasi anak secara otomatis mendeteksi dan menghapus riwayat log lokasi (*History*) yang usianya melebihi 5 hari dari *server*.

## Panduan Instalasi Singkat
1. Pasang aplikasi ini di perangkat orang tua dan perangkat anak.
2. Di aplikasi orang tua, buka menu Pengaturan (Settings) dan salin **Parent ID** yang dibuat otomatis (misalnya `260503001`).
3. Buka aplikasi di perangkat anak, pilih mode `CHILD`. Beri semua perizinan (Lokasi, Mikrofon, Latar Belakang).
4. Catat **Child ID** di perangkat anak, lalu masukkan ke dalam Parent Dashboard. Aplikasi siap digunakan!
