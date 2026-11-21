# F-07/F-08 Laporan dan Filter — Endpoint Fitur

Status: implemented locally (Room-first)

## Scope

- Laporan pengeluaran per kategori.
- Tren pemasukan/pengeluaran enam bulan terakhir.
- Filter transaksi berdasarkan catatan, tipe, dompet, kategori, dan bulan.
- Transfer tidak masuk agregasi income/expense.

Tag belum menjadi field transaksi lokal, sehingga filter tag menunggu slice tag berikutnya.

## Local contract

`ReportCalculator.categoryReport(transactions, categories)` menghasilkan rincian pengeluaran per kategori.

`ReportCalculator.monthlyReport(transactions, nowMillis)` menghasilkan enam bucket bulan dengan total income dan expense.

`ReportCalculator.filter(transactions, query, type, walletId, categoryId, month)` menghasilkan transaksi yang cocok dengan seluruh filter aktif.

## UI route

- `Lainnya -> Laporan`: tab Kategori dan Tren.
- Tab `Transaksi`: pencarian catatan dan filter tipe/dompet.

## Verification

```text
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug
```
