# GitHub Issue Draft — F-09 Keamanan

## Title

F-09: Tambah PIN, sembunyikan saldo, dan app lock keamanan

## Labels (suggested)

- `feature`
- `security`
- `android`

## Summary

Tambahkan lapisan keamanan lokal F-09 dengan PIN 4–6 digit, opsi sembunyikan saldo, dan app lock. Slices PIN dan hide balance sudah ditambahkan; issue ini melengkapi implementasi dan menambahkan lapisan biometrik serta pembuktian verifikasi.

## Scope

- PIN 4–6 digit disimpan sebagai hash, bukan plaintext.
- Aktifkan/nonaktifkan PIN dari `Lainnya -> Keamanan`.
- Toggle sembunyikan saldo di Beranda dan daftar dompet.
- Tambahkan app lock saat app diluncurkan atau kembali dari background.
- Tambahkan AndroidX BiometricPrompt sebagai metode unlock opsional jika perangkat mendukung.
- Pastikan fallback PIN tetap tersedia.
- Tambahkan unit test validator/hash dan Compose test lock screen bila memungkinkan.
- Tambahkan endpoint dokumentasi F-09.

## Acceptance Criteria

- [ ] PIN hanya menerima 4–6 digit.
- [ ] PIN dan konfirmasi harus sama.
- [ ] PIN tidak disimpan sebagai plaintext.
- [ ] Menonaktifkan PIN menghapus konfigurasi PIN lokal.
- [ ] Toggle sembunyikan saldo bekerja reaktif pada Beranda dan Dompet.
- [ ] Saat app lock aktif, konten keuangan tidak terlihat sebelum unlock.
- [ ] Jika biometrik tersedia dan diaktifkan, unlock biometrik bekerja.
- [ ] Jika biometrik gagal atau dibatalkan, fallback PIN tetap tersedia.
- [ ] Tidak ada service-role key/secret Supabase ditambahkan ke aplikasi.
- [ ] Compile, unit test, lint, dan APK debug berhasil.

## Verification Commands

```bash
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleDebug
```

## Current Status

- PIN dan sembunyikan saldo: implemented.
- App lock: belum selesai.
- BiometricPrompt: belum selesai.
