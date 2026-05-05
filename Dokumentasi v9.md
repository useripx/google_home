# Dokumentasi Resmi: Yogi Ario Smart Protection v9.0

Dokumen ini adalah ringkasan teknis dan fungsional dari aplikasi perlindungan anak **Yogi Ario Smart Protection** hingga versi **9.0**.

## Fitur Utama v9.0

### 1. Heatmap History (Analisis Jejak Lokasi)
- **Visualisasi Intensitas**: Selain garis rute merah (polyline), kini tersedia lapisan **Heatmap** pada peta orang tua.
- **Deteksi Titik Kumpul**: Heatmap membantu orang tua melihat di titik mana anak menghabiskan waktu paling lama (area dengan warna lebih pekat/merah menunjukkan intensitas kunjungan tinggi).

### 2. Asisten AI Pintar (Human-like Interaction)
- **Jeda Respon Manusiawi**: Asisten AI kini memiliki jeda waktu 5 detik sebelum memberikan jawaban untuk mensimulasikan proses "berpikir".
- **Animasi Mengetik**: Dilengkapi dengan animasi titik bergerak ("Yogi Ario sedang mengetik....") seperti pada aplikasi WhatsApp/Messenger untuk meningkatkan pengalaman pengguna.
- **Model Terbaru**: Menggunakan model `llama-3.1-8b-instant` untuk stabilitas dan akurasi yang lebih baik.

### 3. Penyamaran Ikon Tingkat Lanjut (Stealth Mode)
- **Dynamic Icon**: Ikon aplikasi di perangkat anak bisa diubah secara dinamis menjadi "Kalkulator".
- **Fake Calculator UI**: Kalkulator fungsional yang menyembunyikan identitas asli aplikasi.
- **Akses Rahasia**: Tahan tombol `=` selama **10 detik** untuk membuka menu rahasia.

### 4. Pemantauan Real-Time & ETA
- Melacak lokasi dengan jeda 10 detik dan estimasi waktu tiba (ETA) ke rumah secara otomatis.

### 5. Mode Darurat & Status Jaringan
- Perekaman audio rahasia (Volume 5x), Monitoring Sinyal (WiFi/Seluler), dan Dering Jarak Jauh (Remote Ring).

### 6. Penghematan Firestore (Data Retention)
- Pembersihan otomatis riwayat log lokasi yang usianya melebihi 5 hari untuk menghemat kuota Firebase.

## Panduan Instalasi
1. Pasang aplikasi di perangkat orang tua dan anak.
2. Hubungkan menggunakan **Parent ID** dan **Child ID**.
3. Pastikan izin lokasi "Always Allow" aktif di perangkat anak.
