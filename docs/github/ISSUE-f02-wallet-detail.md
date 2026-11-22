# GitHub Issue Draft — F-02 Detail, Edit, dan Reset Saldo Dompet

## Title

F-02: Tambah detail dompet, edit nama/tipe, dan reset saldo awal

## Summary

Fitur daftar/tambah/arsip dompet sudah ada, tetapi kartu dompet belum memiliki aksi detail, perubahan nama/tipe, atau pengaturan ulang saldo awal.

## Scope

- Tambah detail informasi dompet.
- Ubah nama dan tipe dompet.
- Atur ulang `initial_balance`.
- Saldo berjalan dihitung ulang reaktif dari transaksi yang tersimpan.
- Reset tidak menghapus transaksi atau riwayat mutasi dompet.
- Tambah validasi dan unit test.
- Tambah endpoint dokumentasi F-02.

## Acceptance Criteria

- [ ] Menu dompet menyediakan Ubah dompet dan Atur ulang saldo awal.
- [ ] Nama dompet tidak boleh kosong.
- [ ] Tipe dompet dapat diubah.
- [ ] Saldo awal nol dan nominal positif diterima.
- [ ] Nominal tidak valid ditolak.
- [ ] Reset saldo awal tidak menghapus transaksi.
- [ ] Saldo kartu/dashboard berubah mengikuti saldo awal baru.
- [ ] Compile, unit test, lint, dan APK debug berhasil.

## Verification Commands

```bash
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleDebug
```

## Out of Scope

- Sinkronisasi Supabase
- Arsip/restore lanjutan
- Saldo adjustment sebagai transaksi terpisah
