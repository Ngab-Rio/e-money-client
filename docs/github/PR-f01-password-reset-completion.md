# GitHub Pull Request Draft — F-01 Password Reset Completion

## Branch

- Base: `main`
- Head: `feature/f01-password-reset-completion`

## Title

feat(auth): complete email/password reset flow on Android

## Description

Melengkapi slice F-01 untuk reset password melalui Supabase dan deep link Android.
PR ini tidak mencakup Google Sign-In atau penghapusan akun.

## What Changed

- Menangani Android deep-link callback `emoney://auth/reset`.
- Menambahkan UI untuk password baru dan konfirmasi password.
- Menambahkan kontrak update password pada repository auth.
- Menangani state loading, sukses, token invalid/expired, dan kegagalan network.
- Menambahkan validasi password dan unit test.
- Memperbarui endpoint dokumentasi F-01 serta status fitur parsial.

## Issue

Closes: `#<nomor-issue>`

Ganti placeholder nomor issue setelah issue dibuat di GitHub.

## Verification

- [x] `./gradlew :app:compileDebugKotlin`
- [x] `./gradlew :app:testDebugUnitTest`
- [x] `./gradlew :app:lintDebug`
- [x] `./gradlew :app:assembleDebug`

Hasil: `BUILD SUCCESSFUL`, `EXIT:0`.

Jangan isi checklist ini sebagai berhasil sebelum perintah benar-benar dijalankan.

## Supabase Configuration

Redirect URL yang diperlukan:

```text
emoney://auth/reset
```

Tambahkan pada **Authentication → URL Configuration → Redirect URLs**.

## Manual Test

1. Buka aplikasi dan pilih **Lupa password**.
2. Masukkan email terdaftar dan kirim tautan reset.
3. Buka email di perangkat Android dengan aplikasi terpasang.
4. Pastikan aplikasi terbuka melalui deep link E-Money.
5. Masukkan password baru dan konfirmasi.
6. Pastikan password berhasil diperbarui dan user kembali ke login/session yang valid.
7. Uji juga token expired/invalid dan password yang tidak cocok.

## Risk / Remaining Work

- Google Sign-In belum dilaksanakan.
- Hapus akun belum dilaksanakan.
- Refresh-session dan profile bootstrap belum dilaksanakan.
- Supabase redirect URL harus dikonfigurasi secara manual di dashboard.

## Security

Tidak menggunakan service-role key atau secret Supabase di Android. Password harus dikirim melalui endpoint Auth Supabase dan session disimpan secara lokal hanya untuk kebutuhan aplikasi.
