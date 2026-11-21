# F-06 Dashboard — Endpoint Fitur

Status: implemented locally (Room-first)

## Scope

- Saldo total dari seluruh dompet aktif.
- Ringkasan pemasukan bulan berjalan.
- Ringkasan pengeluaran bulan berjalan.
- Lima transaksi terbaru.
- Transfer tidak dihitung sebagai income atau expense.
- State otomatis berubah saat Room memancarkan perubahan dompet/transaksi.

## Local contract

`HomeCalculator.calculate(wallets, transactions, nowMillis, zoneId)` menghasilkan `HomeUiState`:

- `totalBalance`: jumlah saldo dompet aktif.
- `monthIncome`: jumlah transaksi `income` sejak awal bulan lokal.
- `monthExpense`: jumlah transaksi `expense` sejak awal bulan lokal.
- `latestTransactions`: maksimal lima transaksi berdasarkan urutan repository.

`HomeViewModel` menggabungkan `WalletRepository.observeWallets()` dan `TransactionRepository.observeTransactions()`.

## UI route

- Tab `Beranda` menampilkan saldo total, dua kartu ringkasan, dan transaksi terbaru.
- Empty state tetap tampil jika belum ada saldo/transaksi.

## Verification

```text
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug
```

Acceptance: dashboard mengikuti perubahan data lokal tanpa refresh manual dan transfer tidak mengubah angka pemasukan/pengeluaran bulan berjalan.
