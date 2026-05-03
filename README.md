# Yogi Ario Smart Protection (Google Home Protect) v8.0

Aplikasi pelacakan dan keamanan anak komprehensif yang berjalan sepenuhnya di sisi *client* tanpa memerlukan infrastruktur berbayar tambahan.

## Fitur Utama v8.0

1. **Asisten AI (Yogi Ario)**: Chatbot interaktif menggunakan Llama 3 (Groq API) untuk memandu orang tua dalam memahami fitur dan melakukan instalasi perangkat anak.
2. **Penyamaran Tingkat Lanjut (Stealth Mode)**: Mengubah ikon aplikasi menjadi "Kalkulator" di HP anak. Memiliki antarmuka kalkulator fungsional palsu. (Tahan `=` selama 10 detik untuk akses rahasia).
3. **Data Retention (TTL) 5 Hari**: Pembersihan history lokasi secara otomatis di sisi *client* untuk menghemat Firebase Spark Plan.
4. **Geofencing Client-Side**: Algoritma perhitungan radius batas aman yang diolah langsung oleh HP anak.
5. **Dering Darurat & Pemantauan Jaringan**: Membunyikan ponsel anak dengan volume maksimal dari jarak jauh dan memantau status sinyal internet.
6. **Pelacakan Real-time & Estimasi ETA**: Pembaruan tiap 10 detik dan kalkulasi waktu tempuh.
7. **Perekaman Darurat**: Tombol rahasia penekan volume 5 kali akan merekam suara sekitar dan mengunggahnya ke Cloud Storage.

---

> **NB: Catatan Masalah yang Diketahui (Known Issues) - Sedang dalam Perbaikan:**
> - Perlu perbaikan ketika memilih mode (saat pertama kali install) tidak bisa digeser ke bawah, sehingga terkadang hanya terlihat tombol 'Parent' saja.
> - Aplikasi secara umum terasa kurang responsif di beberapa bagian.
> - Tampilan obrolan (Chat) Asisten AI bermasalah: kolom untuk mengetik teks kadang tidak muncul atau tidak responsif.
> - Masih terjadi error "Masalah Jaringan" pada Chatbot Groq padahal koneksi internet sudah sangat bagus (kemungkinan masalah timeout atau parsing JSON).

---

## Panduan Instalasi
Silakan rujuk ke file `Dokumentasi v8.md` atau `walkthrough.md` di dalam repositori ini.
