# F-03 Transaksi — Endpoint Fitur

Status: implemented locally (Room-first)

## Scope

- Membuat transaksi `income` dan `expense`.
- Nominal disimpan sebagai `Long` rupiah, wajib lebih besar dari nol.
- Memilih dompet dan kategori.
- Catatan opsional.
- Riwayat transaksi terbaru.
- Saldo dompet dihitung reaktif dari saldo awal + pemasukan - pengeluaran.

Edit, hapus, dan sinkronisasi Supabase berada pada slice berikutnya.

## Local endpoint / contract

### Create transaction

`TransactionRepository.create(type, amount, walletId, categoryId, note)`

- `type`: `INCOME` atau `EXPENSE`
- `amount`: `Long > 0`
- `walletId`: ID dompet aktif
- `categoryId`: ID kategori dengan tipe yang sesuai
- `note`: nullable

For a transfer, call `create(TRANSFER, amount, sourceWalletId, null, note, destinationWalletId)`.
The transfer is one record: the source wallet is debited and the destination wallet is credited.

### Observe transactions

`TransactionRepository.observeTransactions(): Flow<List<Transaction>>`

Urutan: `occurred_at DESC`, kemudian `created_at DESC`.

### Database

Table `transactions`:

- `id` UUID primary key
- `type` (`income` / `expense`)
- `amount` bigint-equivalent (`Long`)
- `wallet_id`
- `category_id`
- `to_wallet_id` (nullable; required for `transfer`)
- `note`
- `occurred_at`
- `created_at`

Migration Room: version 3 → 4 for transfer fields.

Supabase migration: `20261121141546_add_transfer_transactions.sql`.

## UI route

- Tab `Tambah`: form income/expense.
- Tab `Transaksi`: daftar transaksi.

## Verification

```text
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug
```

Acceptance: transaksi valid tersimpan lokal dan perubahan saldo dompet mengikuti data transaksi tanpa refresh manual.

Transfer acceptance: one transfer record changes both wallet balances, is excluded from income/expense totals, and rejects the same source and destination wallet.
