# GitHub Issue Draft — F-01 Selesaikan Reset Password via Android Deep Link

## Title

F-01: Selesaikan alur reset password melalui deep link Android

## Labels (suggested)

- `feature`
- `auth`
- `android`

## Milestone (suggested)

F-01 Auth — Email/Password

## Summary

Request reset password sudah mengirim email dengan redirect `emoney://auth/reset`, dan Android sudah memiliki intent-filter untuk membuka scheme tersebut. Namun, aplikasi belum membaca token dari deep link dan belum menyediakan layar serta API untuk menyimpan password baru.

Issue ini menyelesaikan satu slice vertikal pada F-01 tanpa menambahkan Google Sign-In atau penghapusan akun.

## Scope

- Parse deep link/reset callback di `MainActivity`.
- Ambil recovery token dari fragment/query Supabase secara aman.
- Tampilkan layar **Password baru** dan **Konfirmasi password**.
- Panggil Supabase Auth untuk memperbarui password pengguna.
- Tangani token expired/invalid, loading, sukses, dan kegagalan network.
- Arahkan kembali ke layar login setelah berhasil.
- Tambahkan unit test validator password baru.
- Tambahkan endpoint Markdown/update status F-01.

## Acceptance Criteria

- [ ] Membuka `emoney://auth/reset?...` membuka aplikasi, bukan browser/localhost.
- [ ] Token recovery yang tidak valid atau kedaluwarsa menghasilkan pesan yang jelas.
- [ ] Password baru minimal 6 karakter dan sama dengan konfirmasi.
- [ ] Password berhasil diperbarui melalui Supabase Auth.
- [ ] Setelah sukses, user kembali ke layar login atau session yang konsisten.
- [ ] Email/password auth tetap berfungsi setelah perubahan.
- [ ] Tidak ada service-role key atau secret ditambahkan ke APK.
- [ ] Compile, unit test, dan lint berjalan hijau.

## Supabase Configuration

Redirect URL yang harus ada di **Authentication → URL Configuration → Redirect URLs**:

```text
emoney://auth/reset
```

## Verification Commands

```bash
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleDebug
```

## Out of Scope

- Google Sign-In
- Hapus akun
- Full refresh-token rotation
- Sinkronisasi profile dan owner-scoped local data
