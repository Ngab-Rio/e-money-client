# GitHub Pull Request Draft — F-09 Keamanan

## Branch

- Base: `main`
- Head: `feature/f09-security`

## Title

feat(security): add PIN and hide balance settings

## Description

Menambahkan slice F-09 untuk PIN lokal dan privasi tampilan saldo. Implementasi mencakup pengaturan PIN 4–6 digit, penyimpanan hash SHA-256, toggle sembunyikan saldo, dan masking nominal pada Beranda serta daftar Dompet.

## What Changed

- Menambah `SecurityPreferences` untuk PIN dan preferensi sembunyikan saldo.
- Menambah `SecurityViewModel` dan `SecurityScreen` di `Lainnya -> Keamanan`.
- Menambah validator PIN 4–6 digit.
- Menyimpan PIN sebagai SHA-256 hash, bukan plaintext.
- Menambah hidden-balance state reaktif pada Wallet dan Home.
- Menambah masking nominal `••••••` pada dashboard dan kartu dompet.
- Menambah unit test validator PIN.
- Menambah endpoint dokumentasi F-09.
- Menambah app lock setelah 5 menit di background.
- Menambah AndroidX BiometricPrompt dengan fallback PIN.

## Verification

- [x] `./gradlew :app:compileDebugKotlin`
- [x] `./gradlew :app:testDebugUnitTest`
- [x] `./gradlew :app:lintDebug`
- [x] `./gradlew :app:assembleDebug`

Hasil: `BUILD SUCCESSFUL`, `EXIT:0`.

## Manual Test

1. Buka `Lainnya -> Keamanan`.
2. Aktifkan PIN.
3. Masukkan PIN kurang dari 4 digit atau non-digit; pastikan ditolak.
4. Masukkan PIN dan konfirmasi yang sama; pastikan tersimpan.
5. Aktifkan sembunyikan saldo.
6. Buka Beranda dan Dompet; pastikan nominal menjadi `••••••`.
7. Nonaktifkan sembunyikan saldo; pastikan nominal kembali tampil.
8. Nonaktifkan PIN; pastikan PIN dapat dibuat kembali.
9. Biarkan aplikasi di background selama 5 menit; pastikan aplikasi terkunci.
10. Buka aplikasi yang terkunci; pastikan diminta untuk memasukkan PIN atau menggunakan biometrik.

## Issue

Closes: `#<nomor-issue>`

## Risk / Remaining Work

- PIN saat ini menggunakan SharedPreferences; Android Keystore dapat ditambahkan untuk hardening berikutnya.
- Secure storage untuk token auth belum dilaksanakan.

## Security

Tidak menambahkan service-role key atau secret Supabase ke Android. PIN disimpan sebagai digest SHA-256 dan selalu dimvalidasi sebagai digit 4–6 karakter.
