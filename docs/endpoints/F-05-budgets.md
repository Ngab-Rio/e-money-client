# F-05 Anggaran — Endpoint Fitur

Status: implemented locally (Room-first)

## Scope

- CRUD anggaran bulanan lokal.
- Anggaran per kategori atau total pengeluaran.
- Progress dihitung dari transaksi `expense`, bukan disimpan manual.
- Pengeluaran kategori induk mencakup seluruh subkategori.
- Ambang tampilan: hijau < 80%, kuning 80-99%, merah >= 100%.

## Local contract

`BudgetRepository.create(categoryId, periodMonth, amount)`

- `categoryId`: nullable untuk total pengeluaran.
- `periodMonth`: format `YYYY-MM`.
- `amount`: `Long > 0`.

`BudgetCalculator.calculate(budgets, categories, transactions)` menghasilkan progress per budget.

## Database

Room table `budgets`, migration version 4 -> 5.

Supabase migration: `20261121150000_create_budgets.sql`.

## UI route

Tab `Anggaran` menampilkan progress bulan berjalan, tambah budget, dan hapus budget.

## Verification

```text
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug
```