# GitHub Pull Request Draft — F-02 Wallet Detail

## Branch

- Base: `main`
- Head: `feature/f02-wallet-detail`

## Title

feat(wallets): add detail, edit, and reset opening balance

## Description

Melengkapi slice F-02 setelah daftar/tambah/arsip dompet. PR ini menambahkan akses detail dari kartu dompet, perubahan nama/tipe, dan pengaturan ulang saldo awal tanpa menghapus histori transaksi.

## What Changed

- Menambah aksi Ubah dompet pada menu dompet.
- Menambah aksi Atur ulang saldo awal.
- Menambah DAO/repository untuk update nama/tipe dan reset saldo awal.
- Menambah mode sheet Create/Edit/Reset di ViewModel/UI.
- Menambah string Indonesia dan Inggris.
- Menambah edge test untuk saldo nol dan nominal invalid.
- Menambah endpoint `docs/endpoints/F-02-wallet-detail.md`.

## Verification

- [x] `./gradlew :app:compileDebugKotlin`
- [x] `./gradlew :app:testDebugUnitTest`
- [x] `./gradlew :app:lintDebug`
- [x] `./gradlew :app:assembleDebug`

Hasil: `BUILD SUCCESSFUL`, `EXIT:0`.

## Manual Test

1. Buka Lainnya → Dompet.
2. Pilih menu tiga titik pada dompet.
3. Ubah nama dan tipe, lalu simpan.
4. Pilih Atur ulang saldo awal, ubah nilainya, lalu simpan.
5. Pastikan transaksi lama tetap ada dan saldo berjalan mengikuti saldo awal baru.
6. Uji nama kosong dan nominal invalid.

## Issue

Closes: `#<nomor-issue>`

## Remaining Scope

- Sinkronisasi Supabase
- Restore dompet arsip
- Adjustment transaksi terpisah untuk audit saldo
