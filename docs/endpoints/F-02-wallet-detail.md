# F-02 Dompet — Detail, Edit, dan Reset Saldo

Status: implemented locally (Room-first)

## Scope

- Membuka aksi detail dari kartu dompet.
- Mengubah nama dan tipe dompet.
- Mengatur ulang saldo awal dompet.
- Riwayat transaksi tetap utuh; reset tidak menghapus transaksi.

## Local contract

`WalletRepository.updateWallet(id, name, type)`

- Mengubah `name` dan `type` pada dompet aktif.
- Nama wajib tidak kosong.

`WalletRepository.resetInitialBalance(id, initialBalance)`

- Mengubah `initial_balance` saja.
- Saldo berjalan tetap dihitung sebagai saldo awal baru ditambah seluruh mutasi transaksi.
- Nilai nol diterima.

## Database

Wallet DAO queries:

- `update(...)`
- `resetInitialBalance(...)`

Tidak ada migration Room baru untuk slice ini karena kolom yang diperlukan sudah ada.

## UI route

Buka `Lainnya -> Dompet`, lalu pada menu kartu gunakan:

- Ubah dompet
- Atur ulang saldo awal
- Arsipkan

## Verification

```text
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Acceptance

- Nama atau tipe yang valid tersimpan tanpa menghapus histori.
- Reset saldo awal memperbarui saldo berjalan secara reaktif.
- Nominal reset yang tidak valid ditolak.
