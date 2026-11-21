---
name: Android Money Conventions
description: Use when writing or reviewing Kotlin, Compose, Room, WorkManager, or Supabase SQL in this project. Encodes the PRD invariants: Long money, Room-first offline sync, computed balances, RLS, idempotent bills/recurring, and feature-first MVVM.
---

# Android Money Conventions

Sumber kebenaran: `PRD Aplikasi Manajemen Keuangan (Kotlin + Supabase).md`.
Skill ini merangkum aturan yang **tidak boleh dilanggar** saat implementasi.

## 1. Uang

- Nominal selalu `Long` dalam satuan terkecil (IDR = rupiah utuh).
  Dilarang `Double`, `Float`, `Number`.
- Model domain menyimpan `amountMinor: Long`, bukan string berformat.
- Format di UI saja:
  ```kotlin
  NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount) // Rp 1.250.000
  ```
- Input nominal: parse digit saja ke `Long`, tolak negatif/nol, cegah overflow.
- Persentase/rasio: hitung `Long` atau `BigDecimal` dengan rounding eksplisit.
- Jangan menyimpan saldo dompet sebagai kolom manual; saldo berasal dari view/RPC
  (`initial_balance + income - expense ± transfer`).

## 2. Arsitektur

```
core/      util uang & format, tema, komponen, error
data/
  local/   Room: database, DAO, entity, converter
  remote/  Supabase datasource
  sync/    outbox, SyncWorker
  repository/
domain/    model, use case
feature/<nama>/  Screen, ViewModel, UiState, navigation
```

- MVVM + clean ringan. UI → ViewModel → use case/repository → DAO/remote.
- Composable **stateless**, state di-hoist; tidak ada logika bisnis di UI.
- `StateFlow<UiState>` immutable; `collectAsStateWithLifecycle()`.
- Hilt untuk DI; `@HiltViewModel`, constructor injection. Tidak ada service locator.
- Navigasi pakai Navigation Compose; route berupa konstanta/`sealed interface`, bukan string liar.
- Nama file: `HomeScreen.kt`, `HomeViewModel.kt`, `HomeUiState.kt`. Satu tanggung jawab per file.

## 3. Offline-first & sinkronisasi

- **Room adalah satu-satunya sumber baca untuk UI.** Jangan query Supabase langsung dari UI/ViewModel.
- Alur tulis: validasi → tulis Room dalam transaksi → catat `outbox` → `SyncWorker` push.
- Tarik data dengan `updated_at > last_sync`; Realtime hanya memicu pull, bukan menulis langsung ke UI.
- Konflik: last-write-wins berdasarkan `updated_at` (server time).
- Hapus = soft delete (`deleted_at`), bukan `DELETE` fisik.
- Semua operasi outbox **idempoten**: pakai `id` klien (uuid) agar retry tidak menggandakan.
- `SyncWorker` pakai constraint network + backoff; jangan blocking di main thread.
- Mutasi yang mengubah beberapa tabel (transaksi + saldo + budget) dibungkus `@Transaction`.

## 4. Invariant domain

- **Transaksi:** `income` / `expense` / `transfer`. `amount > 0`.
  Transfer = satu record dengan `wallet_id` + `to_wallet_id`, tidak dihitung di laporan
  pemasukan/pengeluaran.
- **Budget:** unik per `(owner, category, month)`; hitung kategori + subkategori.
  Ambang 80% (warning) dan 100% (danger). Total budget (`category_id IS NULL`) dijaga unik
  lewat index parsial, karena `NULL` tidak unik di Postgres.
- **Tagihan:** `bill_occurrences` unik `(bill_id, due_date)`; status
  `unpaid | paid | skipped`. "Terlambat" = `due_date < today AND status = 'unpaid'`.
  Tandai lunas membuat **tepat satu** transaksi expense dan mengisi `paid_transaction_id`
  dalam satu transaksi DB.
- **Hutang/piutang:** `lent` = uang keluar, `borrowed` = uang masuk (bila toggle dompet aktif).
  `paid_amount`/`status` hanya diubah oleh trigger dari `debt_payments`.
  Cicilan tidak boleh melebihi sisa; status `open → partial → settled`.
- **Recurring:** idempoten lewat `(recurring_id, occurrence_date)`; `next_run_date` bergerak
  hanya setelah occurrence tersimpan.
- **Skor finansial:** AI/model tidak boleh menandai aman; keputusan tetap aturan eksplisit.

## 5. Keamanan & data

- RLS aktif di **setiap** tabel; policy dasar `owner_id = auth.uid()` untuk `using` dan `with check`.
- Tabel join (`wallet_members`) butuh policy berbasis keanggotaan, bukan hanya owner.
- Jangan percaya input klien untuk: role, saldo, status, `paid_amount`, hasil verifikasi.
- Service role key **tidak boleh** ada di aplikasi Android. Hanya URL + anon key,
  dibaca dari `local.properties` → `BuildConfig`.
- Bucket `receipts` privat; path `{user_id}/{transaction_id}.jpg`; policy per pemilik.
- Validasi server-side untuk setiap aksi; validasi klien hanya UX.

## 6. SQL & migrasi

- Migration di `supabase/migrations/` dengan nama `YYYYMMDDHHMMSS_deskripsi.sql`.
- Setiap tabel: `id uuid pk default gen_random_uuid()`, `created_at`, `updated_at`,
  `deleted_at` (soft delete).
- Trigger `updated_at` untuk semua tabel yang bisa diedit.
- Seed kategori default via trigger pada `auth.users` insert; idempoten.
- Index untuk pola query nyata: `(owner_id, occurred_at desc)`, `(owner_id, category_id)`.
- View/RPC: `wallet_balances`, `monthly_summary`, `category_spending`, `budget_progress`.
- Uji policy dengan dua akun berbeda sebelum menandai selesai.

## 7. Testing minimum per fitur

| Jenis | Contoh |
| --- | --- |
| Sukses | simpan transaksi, saldo berubah benar |
| Validasi | nominal 0/negatif ditolak |
| Otorisasi | user lain tidak bisa membaca/mengubah |
| Edge case | transfer ke dompet sama, cicilan > sisa, occurance duplikat |

- Unit test logika murni (uang, budget, skor) tanpa Android framework.
- Test DAO/Room dengan in-memory database.
- Test Compose untuk alur tambah transaksi (numpad → simpan).
- Jangan mengklaim lulus tanpa menjalankan `./gradlew` yang relevan.

## 8. Checklist sebelum menandai selesai

- [ ] Uang `Long`; format hanya di UI.
- [ ] UI baca dari Room; tulis lewat outbox.
- [ ] Tidak ada logika bisnis di composable.
- [ ] RLS + migration untuk tabel baru.
- [ ] Aksi berpasangan memakai transaksi DB.
- [ ] Idempotensi tagihan/recurring/point award terjaga.
- [ ] Test sukses, validasi, unauthorized, edge case.
- [ ] Semua teks di `strings.xml` (id + en).
