# PRD: Aplikasi Manajemen Keuangan (Kotlin + Supabase)

**Versi:** 1.0 | **Status:** Draft siap dikerjakan | **Platform:** Android native (Kotlin, minSdk 26) | **Bahasa UI:** Indonesia (default), Inggris

---

## 1. Ringkasan Produk

Aplikasi pencatat dan perencana keuangan pribadi yang **gratis sepenuhnya**, dengan fitur yang di aplikasi lain biasanya berbayar: anggaran amplop, transaksi berulang, scan struk (OCR), laporan ekspor, prediksi arus kas, tracker langganan, hutang-piutang, dompet bersama, dan skor kesehatan finansial.

**Value proposition:** Satu aplikasi lengkap untuk mencatat, merencanakan, dan memahami uang tanpa paywall, dengan data sinkron di semua perangkat lewat Supabase.

## 2. Tujuan dan Non-Tujuan

| Tujuan | Non-Tujuan (v1) |
| --- | --- |
| Pencatatan transaksi cepat (maks 3 ketukan) | Integrasi langsung ke rekening bank / open banking |
| Sinkronisasi multi-perangkat dan offline-first | Fitur investasi real-time (saham, kripto) |
| Anggaran, target tabungan, dan insight otomatis | Pembayaran / transfer uang sungguhan |
| Privasi: data milik user, terkunci RLS | Monetisasi iklan |
| Kode bersih agar mudah di-vibe-coding per modul | Versi web / desktop |

## 3. Target Pengguna

| Persona | Kebutuhan utama |
| --- | --- |
| **Mahasiswa / fresh graduate** | Jatah bulanan, patungan, hutang ke teman, target tabungan |
| **Karyawan** | Gaji bulanan, tagihan rutin, langganan, dana darurat |
| **Freelancer / UMKM kecil** | Pemasukan tidak tetap, pisah dompet pribadi vs usaha, laporan ekspor |
| **Pasangan / keluarga** | Dompet bersama, anggaran rumah tangga |

## 4. Tech Stack

| Layer | Pilihan | Catatan |
| --- | --- | --- |
| Bahasa dan UI | **Kotlin**, Jetpack Compose, Material 3 | Dark mode wajib; minSdk 26 |
| Arsitektur | ViewModel + StateFlow, **Hilt** (DI) | Tanpa logika bisnis di composable |
| Navigasi | Navigation Compose | Deep link ke transaksi / budget / tagihan |
| Model | data class + `kotlinx.serialization` | Immutable |
| Backend | **Supabase**: Auth, Postgres, Storage, Realtime, Edge Functions, `pg_cron` | RLS di semua tabel |
| Klien Supabase | `supabase-kt` (auth-kt, postgrest-kt, realtime-kt, storage-kt, functions-kt) + engine Ktor OkHttp | Pakai versi 3.x; versi Ktor harus sama dengan supabase-kt |
| Cache lokal (offline) | **Room** (SQLite) + Flow | Antrian sinkronisasi (outbox pattern) |
| Tugas latar belakang | **WorkManager** | `SyncWorker`, pembuatan tagihan / transaksi berulang |
| Grafik | Vico |  |
| Notifikasi | NotificationManager + AlarmManager (alarm tepat waktu) | Izin `POST_NOTIFICATIONS` (Android 13+), `SCHEDULE_EXACT_ALARM`, jadwalkan ulang setelah boot |
| Keamanan | AndroidX BiometricPrompt, DataStore + Android Keystore | PIN / biometrik |
| OCR struk | ML Kit Text Recognition (on-device) + CameraX / Photo Picker |  |
| Ekspor | CSV manual, `PdfDocument` bawaan Android, FileProvider + Intent share |  |
| Format | `java.text.NumberFormat`, `java.time`, locale `id-ID` |  |
| Login Google | Credential Manager |  |
| Widget (opsional) | Jetpack Glance | Fase 4 |
| Gambar | Coil | Foto struk |

**Aturan emas uang:** simpan nominal sebagai **bigint dalam satuan terkecil** (IDR: rupiah utuh; USD: sen). Dilarang memakai `double` untuk uang. Di Kotlin gunakan Long, bukan Double atau Float.

## 5. Daftar Fitur

### 5.1 Fitur Inti (MVP)

| ID | Fitur | Deskripsi |
| --- | --- | --- |
| F-01 | Autentikasi | Email + password, Google Sign-In, reset password, hapus akun |
| F-02 | Multi-dompet | Tunai, bank, e-wallet, kartu kredit; saldo awal; arsipkan dompet |
| F-03 | Transaksi | Pemasukan, pengeluaran, transfer antar dompet; kategori, catatan, tanggal, tag, lampiran foto |
| F-04 | Kategori | Bawaan (makan, transport, dll) + kustom, ikon dan warna, subkategori |
| F-05 | Anggaran (Budget) | Per kategori / total, bulanan, indikator sisa, peringatan 80% dan 100% |
| F-06 | Dashboard | Saldo total, arus bulan ini, grafik, transaksi terbaru, status budget |
| F-07 | Laporan | Grafik per kategori, tren bulanan, filter periode / dompet / tag |
| F-08 | Pencarian dan filter | Teks, rentang tanggal, nominal, kategori |
| F-09 | Keamanan | PIN, biometrik, sembunyikan saldo |
| F-10 | Sinkronisasi | Offline-first, sinkron otomatis saat online |
| F-11 | Pengingat tagihan | Jadwal tagihan rutin / sekali bayar, notifikasi H-3, H-1, hari H, tandai lunas otomatis menjadi transaksi |
| F-12 | Hutang dan piutang | Catat hutang (aku berutang ke siapa) dan piutang (uang yang kupinjamkan), cicilan, jatuh tempo, dan penagihan |

### 5.2 Fitur "Premium Gratis" (Diferensiator)

| ID | Fitur | Deskripsi | Prioritas |
| --- | --- | --- | --- |
| P-01 | Transaksi berulang | Harian / mingguan / bulanan / tahunan; auto-create atau minta konfirmasi | Tinggi |
| P-03 | Scan struk (OCR) | Foto struk → total, tanggal, merchant terisi otomatis | Tinggi |
| P-04 | Target tabungan | Target nominal + tenggat, setoran manual / otomatis, progres dan estimasi tercapai | Tinggi |
| P-06 | Split bill | Bagi tagihan ke teman (rata / per item), otomatis jadi piutang | Sedang |
| P-07 | Tracker langganan | Deteksi transaksi berulang, total biaya per bulan / tahun, ingatkan sebelum perpanjang | Sedang |
| P-08 | Aturan auto-kategori | "Jika catatan mengandung 'gojek' → Transport"; belajar dari riwayat | Sedang |
| P-09 | Prediksi arus kas | Proyeksi saldo 30/60/90 hari dari transaksi berulang + rata-rata historis | Sedang |
| P-10 | Skor kesehatan finansial | Skor 0-100: rasio tabungan, kepatuhan budget, dana darurat, rasio utang | Sedang |
| P-11 | Ekspor data | CSV dan PDF laporan bulanan, backup JSON | Tinggi |
| P-12 | Dompet bersama | Undang anggota via email, peran owner / editor / viewer | Rendah |
| P-13 | Multi-mata uang | Dompet beda mata uang, kurs manual atau dari API gratis, laporan dikonversi | Rendah |
| P-14 | Rollover budget | Sisa anggaran bulan lalu dibawa ke bulan berikut (opsional per kategori) | Sedang |
| P-15 | Net worth | Aset (dompet, tabungan) dikurangi liabilitas (hutang, kartu kredit) | Sedang |
| P-16 | Insight otomatis | Kartu seperti "Pengeluaran makan naik 23% dari bulan lalu" | Sedang |
| P-17 | Kalkulator zakat / pajak sederhana | Zakat penghasilan dan maal berbasis data aplikasi | Rendah |
| P-18 | Impor CSV | Impor mutasi dari file CSV rekening dengan pemetaan kolom | Rendah |
| P-19 | Widget home screen | Tambah cepat dan lihat sisa budget | Rendah |

## 6. Roadmap Fase

| Fase | Cakupan | Output |
| --- | --- | --- |
| **0 - Fondasi** | Proyek Android (Kotlin + Compose), tema, navigasi, Supabase init, skema DB + RLS | App kosong terhubung ke Supabase |
| **1 - MVP** | F-01 sampai F-12 | Pencatat keuangan fungsional |
| **2 - Planning** | P-01, P-04, P-14 | Perencanaan: berulang, target tabungan, rollover budget |
| **3 - Pintar** | P-03, P-08, P-09, P-10, P-16, P-07 | OCR, prediksi, skor, insight |
| **4 - Kolaborasi dan polish** | P-06, P-11, P-12, P-13, P-15, P-17, P-18, P-19 | Fitur lanjutan dan rilis |

## 7. Alur dan Layar

**Navigasi bawah (5 tab):** Beranda · Transaksi · **(+) Tambah** · Anggaran · Lainnya

| Layar | Isi utama |
| --- | --- |
| Splash / Onboarding | 3 slide, pilih mata uang, buat dompet pertama |
| Login / Daftar | Email, Google, lupa password |
| Beranda | Kartu saldo total, ringkasan bulan ini (masuk/keluar), donut kategori, budget teratas, transaksi terbaru, insight |
| Daftar Transaksi | Dikelompokkan per tanggal, filter, pencarian, swipe edit / hapus |
| Tambah Transaksi | Numpad besar, toggle jenis, kategori grid, dompet, tanggal, catatan, foto, tombol scan struk |
| Detail Transaksi | Semua field, lampiran, duplikat, hapus |
| Dompet | Daftar dompet dan saldo, detail dompet, atur ulang saldo |
| Anggaran | Daftar budget dengan progress bar, buat / ubah budget |
| Laporan | Tab: Kategori · Tren · Perbandingan; ekspor |
| Target Tabungan | Kartu target, progres, tambah setoran |
| Hutang / Piutang | Dua tab, cicilan, jatuh tempo |
| Tagihan dan Langganan | Kalender tagihan, total bulanan |
| Prediksi dan Skor | Grafik proyeksi, skor beserta rincian |
| Pengaturan | Profil, keamanan, mata uang, notifikasi, tema, kategori, aturan auto-kategori, ekspor, hapus akun |

**Alur kritis - catat transaksi:** Buka app → ketuk (+) → ketik nominal → pilih kategori → Simpan. Dompet terakhir dan tanggal hari ini sebagai default.

## 8. Persyaratan Fungsional dan Kriteria Penerimaan

### F-03 Transaksi

- Tipe: `income`, `expense`, `transfer`. Nominal wajib > 0.
- Transfer membuat **satu record** dengan `wallet_id` dan `to_wallet_id`; tidak masuk hitungan pemasukan / pengeluaran di laporan.
- Saldo dompet = `initial_balance` + pemasukan - pengeluaran ± transfer (dihitung via view / fungsi SQL, bukan disimpan manual).
- Edit / hapus transaksi otomatis memperbarui saldo, budget, dan laporan.
- **Diterima jika:** transaksi tersimpan lokal < 100 ms, tersinkron saat online, dan muncul di perangkat kedua dalam < 5 detik (Realtime).

### F-05 Anggaran

- Budget unik per (user, kategori, periode bulan). Terhitung dari transaksi `expense` pada kategori + subkategorinya.
- Warna: hijau < 80%, kuning 80-99%, merah >= 100%. Notifikasi lokal saat melewati 80% dan 100%.
- **Diterima jika:** menambah pengeluaran langsung mengubah progress bar tanpa refresh manual.

### F-11 Pengingat Tagihan

- Tagihan = kewajiban bayar dengan nama, nominal (tetap atau perkiraan), jatuh tempo, pengulangan (sekali / mingguan / bulanan / tahunan), kategori, dan dompet pembayar default.
- Setiap jatuh tempo menghasilkan satu *occurrence* berstatus `unpaid`, `paid`, atau `skipped`. Status "terlambat" dihitung dari `due_date` < hari ini dan `unpaid`.
- Pengingat per tagihan: default H-3, H-1, dan hari H pukul 08:00 (jam dapat diubah). Jika terlambat, notifikasi ulang tiap hari maksimal 3 kali.
- Tombol **Tandai lunas**: pilih dompet dan nominal aktual, lalu app membuat satu transaksi `expense` dan menautkannya (`paid_transaction_id`). Aksi "Bayar" di notifikasi membuka form yang sudah terisi.
- Tagihan berulang membuat occurrence berikutnya otomatis: Edge Function harian + pengecekan klien saat app dibuka; idempoten lewat unique (`bill_id`, `due_date`).
- Layar Tagihan: tab Akan datang / Terlambat / Lunas, total tagihan bulan ini, dan tampilan kalender. Beranda menampilkan kartu "Tagihan 7 hari ke depan".
- **Diterima jika:** notifikasi terjadwal ulang setelah perangkat restart atau app dibuka; menandai lunas membuat tepat satu transaksi; mengubah tanggal menjadwalkan ulang notifikasinya.

### F-12 Hutang dan Piutang

- Dua jenis: **Hutang** (`borrowed`, user berutang ke orang lain) dan **Piutang** (`lent`, user meminjamkan uang ke orang lain).
- Field: nama orang (teks bebas + autocomplete dari riwayat), nominal pokok, tanggal pinjam, jatuh tempo (opsional), catatan, foto bukti (opsional), dompet terkait.
- Toggle **Catat ke dompet**: aktif → piutang mencatat uang keluar, hutang mencatat uang masuk (kategori sistem "Hutang-Piutang"). Nonaktif → hanya pengingat, saldo dompet tidak berubah.
- Cicilan dicatat di `debt_payments` (nominal, tanggal, dompet, catatan). Sisa = pokok - total cicilan; status otomatis `open` → `partial` → `settled`. Cicilan tidak boleh melebihi sisa.
- Pengingat: H-3, H-1, hari H; opsi pengingat mingguan untuk piutang yang belum kembali. Tombol "Tagih" membuka pesan siap kirim (WhatsApp lewat `Intent.ACTION_SEND` atau `ShareCompat`).
- Layar: tab Hutang dan Piutang, total piutang belum kembali dan total hutang di bagian atas, daftar dikelompokkan per orang, detail berisi riwayat cicilan dan progress bar. Jika satu orang punya hutang dan piutang, tampilkan saldo bersih ("Budi masih berutang Rp 200.000 ke kamu").
- Integrasi: hutang masuk ke net worth (P-15) dan rasio utang di skor (P-10); split bill (P-06) membuat piutang otomatis.
- **Diterima jika:** piutang Rp500.000 dengan toggle aktif mengurangi saldo dompet Rp500.000; cicilan Rp200.000 menaikkan saldo Rp200.000, sisa menjadi Rp300.000, status `partial`; pelunasan mengubah status menjadi `settled`.

### P-01 Transaksi Berulang

- Field: template transaksi, frekuensi, interval, tanggal mulai, tanggal akhir (opsional), mode (`auto` / `confirm`).
- Pembuatan instan dilakukan oleh Edge Function terjadwal (`pg_cron` harian); klien juga mengecek saat app dibuka untuk menangani offline.
- Harus **idempoten**: kolom `next_run_date` dan constraint unik (recurring\_id, occurrence\_date) mencegah duplikat.

### P-03 Scan Struk

- Foto → ML Kit OCR on-device → heuristik: cari baris "TOTAL" / "GRAND TOTAL", pola tanggal, nama merchant di baris atas.
- Hasil ditampilkan di form **dapat diedit** sebelum simpan; foto disimpan ke Storage bucket privat.
- **Diterima jika:** pada 10 struk uji minimarket, total terdeteksi benar minimal 7.

### P-04 Target Tabungan

- Progres = total setoran / target. Estimasi tanggal tercapai = sisa / rata-rata setoran per bulan.
- Setoran dapat dihubungkan ke transaksi transfer ke dompet "Tabungan".

### P-09 Prediksi Arus Kas

- Proyeksi harian = saldo saat ini + transaksi berulang terjadwal + rata-rata pengeluaran harian variabel 90 hari terakhir.
- Tampilkan garis proyeksi dan tandai tanggal saat saldo diperkirakan negatif.

### P-10 Skor Kesehatan Finansial (0-100)

| Komponen | Bobot | Rumus ringkas |
| --- | --- | --- |
| Rasio tabungan | 30 | (pemasukan - pengeluaran) / pemasukan, target >= 20% |
| Kepatuhan budget | 25 | % kategori yang tidak melewati budget |
| Dana darurat | 25 | saldo likuid / rata-rata pengeluaran bulanan, target 6 bulan |
| Rasio utang | 20 | cicilan + utang / pemasukan, target < 30% |

## 9. Model Data (Supabase / PostgreSQL)

Semua tabel memiliki `id uuid pk default gen_random_uuid()`, `created_at`, `updated_at`, dan `deleted_at` (soft delete untuk sinkronisasi). Semua tabel **wajib RLS aktif**.

```sql
-- Profil pengguna
create table profiles (
  id uuid primary key references auth.users on delete cascade,
  display_name text,
  default_currency char(3) not null default 'IDR',
  locale text not null default 'id_ID',
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

create table wallets (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  name text not null,
  type text not null check (type in ('cash','bank','ewallet','credit_card','savings')),
  currency char(3) not null default 'IDR',
  initial_balance bigint not null default 0,
  color text, icon text,
  is_archived boolean default false,
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz
);

create table categories (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  parent_id uuid references categories(id),
  name text not null,
  kind text not null check (kind in ('income','expense')),
  icon text, color text,
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz
);

create table transactions (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  wallet_id uuid not null references wallets(id),
  to_wallet_id uuid references wallets(id),            -- hanya untuk transfer
  category_id uuid references categories(id),
  type text not null check (type in ('income','expense','transfer')),
  amount bigint not null check (amount > 0),
  occurred_at timestamptz not null,
  note text,
  merchant text,
  receipt_path text,                                   -- path di Storage
  recurring_id uuid,
  tags text[] default '{}',
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz
);
create index on transactions (owner_id, occurred_at desc);
create index on transactions (owner_id, category_id);

create table budgets (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  category_id uuid references categories(id),          -- null = total anggaran
  month date not null,                                 -- tanggal 1 bulan terkait
  amount bigint not null check (amount >= 0),
  rollover boolean default false,
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz,
  unique (owner_id, category_id, month)
);

create table recurring_rules (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  template jsonb not null,                             -- wallet, kategori, nominal, tipe, catatan
  frequency text not null check (frequency in ('daily','weekly','monthly','yearly')),
  interval_n int not null default 1,
  start_date date not null, end_date date, next_run_date date not null,
  mode text not null default 'confirm' check (mode in ('auto','confirm')),
  is_bill boolean default false,                       -- tampil di pengingat tagihan
  is_subscription boolean default false,
  is_active boolean default true,
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz
);

create table goals (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  name text not null, target_amount bigint not null, saved_amount bigint not null default 0,
  target_date date, icon text, color text,
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz
);

create table debts (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  kind text not null check (kind in ('lent','borrowed')),
  person_name text not null, total_amount bigint not null,
  paid_amount bigint not null default 0, due_date date, note text,
  status text not null default 'open' check (status in ('open','partial','settled')),
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz
);

create table categorization_rules (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  keyword text not null, category_id uuid not null references categories(id),
  created_at timestamptz default now()
);

-- Dompet bersama (Fase 4)
create table wallet_members (
  wallet_id uuid references wallets(id) on delete cascade,
  user_id uuid references auth.users on delete cascade,
  role text not null check (role in ('owner','editor','viewer')),
  primary key (wallet_id, user_id)
);
```

**Tambahan skema untuk F-11 dan F-12.** Tabel `bills` menggantikan kolom `is_bill` di `recurring_rules`, jadi hapus kolom itu. `debts.paid_amount` dan `debts.status` dijaga oleh trigger dari `debt_payments`.

```sql
create table bills (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  name text not null,
  amount bigint not null check (amount >= 0),
  is_amount_estimate boolean default false,
  category_id uuid references categories(id),
  wallet_id uuid references wallets(id),
  frequency text not null default 'monthly' check (frequency in ('once','weekly','monthly','yearly')),
  next_due_date date not null,
  remind_days_before int[] not null default '{3,1,0}',
  remind_time time not null default '08:00',
  is_active boolean default true,
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz
);

create table bill_occurrences (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  bill_id uuid not null references bills(id) on delete cascade,
  due_date date not null,
  amount bigint not null check (amount >= 0),
  status text not null default 'unpaid' check (status in ('unpaid','paid','skipped')),
  paid_transaction_id uuid references transactions(id),
  paid_at timestamptz,
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz,
  unique (bill_id, due_date)
);

alter table debts
  add column wallet_id uuid references wallets(id),
  add column borrowed_at date not null default current_date,
  add column remind_weekly boolean default false,
  add column origin_transaction_id uuid references transactions(id);

create table debt_payments (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users on delete cascade,
  debt_id uuid not null references debts(id) on delete cascade,
  amount bigint not null check (amount > 0),
  paid_at date not null default current_date,
  wallet_id uuid references wallets(id),
  transaction_id uuid references transactions(id),
  note text,
  created_at timestamptz default now(), updated_at timestamptz default now(), deleted_at timestamptz
);

-- RLS: owner_id = auth.uid() pada bills, bill_occurrences, debt_payments
```

**Pola RLS (ulangi untuk setiap tabel):**

```sql
alter table transactions enable row level security;
create policy "own rows" on transactions
  for all using (owner_id = auth.uid()) with check (owner_id = auth.uid());
```

**Trigger wajib:** `updated_at` otomatis; seed kategori default saat user baru mendaftar (trigger pada `auth.users` insert).

**Storage:** bucket privat `receipts`, path `{user_id}/{transaction_id}.jpg`, policy hanya pemilik.

**View / RPC:** `wallet_balances` (saldo per dompet), `monthly_summary(month)`, `category_spending(from, to)`, `budget_progress(month)`.

## 10. Arsitektur Aplikasi

Pola: **MVVM + clean architecture ringan**, dikelompokkan per fitur (`ui` → `domain` → `data`). Arah data satu arah: UI mengamati `StateFlow` dari ViewModel, ViewModel memanggil use case / repository.

```
app/src/main/java/.../money/
  MoneyApp.kt             (Application + Hilt)
  MainActivity.kt
  core/                   (util uang dan format, tema, komponen UI umum, error)
  data/
    local/                (Room: database, DAO, entity)
    remote/               (datasource supabase-kt)
    sync/                 (outbox, SyncWorker)
    repository/
  domain/                 (model, use case)
  feature/
    auth/ wallets/ transactions/ categories/ budgets/
    reports/ goals/ debts/ bills/ recurring/ receipts/ insights/ settings/
      (tiap fitur: Screen, ViewModel, UiState, navigasi)
supabase/migrations/      (SQL migration)
```

**Strategi offline-first:** UI hanya membaca dari Room (Flow). Perubahan ditulis ke Room + tabel `outbox`; `SyncWorker` (WorkManager) mengirim ke Supabase saat online, menarik perubahan lewat `updated_at > last_sync`, dan Realtime memicu pull. Konflik: **last-write-wins** berdasar `updated_at`.

## 11. Persyaratan Non-Fungsional

| Aspek | Target |
| --- | --- |
| Performa | Cold start < 2 dtk; scroll 60 fps pada 10.000 transaksi |
| Offline | Semua fitur inti jalan tanpa internet |
| Keamanan | RLS semua tabel; token di secure storage; tidak ada service key di klien; PIN di-hash |
| Privasi | Hapus akun menghapus seluruh data; ekspor data tersedia |
| Aksesibilitas | Dukung font besar, kontras AA, label semantik |
| Lokalisasi | `id_ID` default; format `Rp 1.250.000` |
| Kualitas | Unit test logika uang, budget, skor; UI test Compose untuk alur tambah transaksi |
| Observabilitas | Logging error (Sentry opsional) |

## 12. Desain Visual

- Material 3, warna primer hijau-teal (kesan finansial sehat), pemasukan hijau, pengeluaran merah, transfer biru.
- Angka memakai font tabular; nominal besar pada layar tambah transaksi.
- Dark mode dan light mode mengikuti sistem.
- Empty state ramah dengan ilustrasi dan ajakan aksi; animasi halus (< 300 ms).

## 13. Metrik Keberhasilan

| Metrik | Target |
| --- | --- |
| Waktu mencatat 1 transaksi | < 10 detik |
| Retensi D7 | > 35% |
| Pengguna dengan >= 1 budget | > 50% |
| Crash-free sessions | > 99% |

## 14. Risiko dan Mitigasi

| Risiko | Mitigasi |
| --- | --- |
| Konflik sinkronisasi | Outbox + LWW + soft delete; uji dua perangkat |
| Akurasi OCR rendah | Selalu bisa diedit; heuristik bertahap |
| Notifikasi dibunuh OS (Android hemat baterai) | Jadwalkan ulang saat app dibuka dan setelah boot; minta izin notifikasi (Android 13+) dan alarm tepat waktu; arahkan user atur izin baterai |
| Kebocoran data | RLS + uji policy dengan dua akun berbeda |
| Scope terlalu besar | Disiplin fase; rilis tiap akhir fase |

## 15. Panduan Vibe Coding

### Aturan untuk AI (taruh di file `RULES.md` / system prompt proyek)

1. Gunakan Kotlin + Jetpack Compose + Material 3, Hilt, Navigation Compose, Room, WorkManager, `kotlinx.serialization`, dan `supabase-kt` 3.x sesuai PRD ini.
2. Uang **selalu `Long`** (satuan terkecil). Jangan pakai `Double` atau `Float`.
3. Struktur paket feature-first; satu fitur = satu paket (`ui`, `domain`, `data`).
4. UI hanya membaca dari repository lokal (Room), bukan langsung dari Supabase.
5. Setiap tabel baru wajib RLS dan migration SQL di folder `supabase/migrations/`.
6. Jangan menaruh secret / service role key di kode Android; simpan URL dan anon key di `local.properties` (tidak di-commit) lalu baca lewat `BuildConfig`.
7. Kerjakan **satu milestone per sesi**, jalankan `./gradlew lintDebug testDebugUnitTest` sebelum lanjut.
8. Semua teks UI lewat `strings.xml` (`values/` dan `values-en/`); composable bersifat stateless dengan state hoisting.

### Urutan Milestone (satu prompt per milestone)

| # | Milestone | Hasil yang diverifikasi |
| --- | --- | --- |
| M0 | Setup proyek Android (Gradle version catalog, Compose, Hilt), tema M3, navigasi, struktur paket, `local.properties` | App jalan dengan 5 tab kosong |
| M1 | Supabase project, migration skema + RLS + trigger seed kategori | Query dengan 2 akun terisolasi |
| M2 | Auth (email + Google via Credential Manager), guard navigasi, profil | Login / logout / reset password |
| M3 | Room DB + repository + sync engine dasar (WorkManager) | Data tersimpan offline lalu sinkron |
| M4 | CRUD dompet dan kategori | Saldo dompet benar |
| M5 | CRUD transaksi (income, expense, transfer) + form numpad | Saldo dan daftar ter-update |
| M6 | Dashboard + laporan (Vico) + filter | Angka cocok dengan data uji |
| M7 | Anggaran + notifikasi ambang | Progress bar realtime |
| M8 | PIN / biometrik (BiometricPrompt), pengaturan, tema | Kunci app berfungsi |
| M9 | Pengingat tagihan (F-11, MVP) + transaksi berulang + Edge Function cron + izin notifikasi / alarm | Tidak ada duplikasi; notifikasi tetap terjadwal setelah restart perangkat |
| M10 | Hutang dan piutang (F-12, MVP) + target tabungan | Alur lunas berjalan |
| M11 | Scan struk OCR (ML Kit) | Uji 10 struk |
| M12 | Auto-kategori, insight, tracker langganan | Kartu insight muncul |
| M13 | Prediksi arus kas + skor kesehatan | Grafik dan rincian skor |
| M14 | Ekspor CSV / PDF / backup, impor CSV | File terbuka benar |
| M15 | Dompet bersama, multi-mata uang, net worth, widget (Glance) | Uji dua akun |
| M16 | Polish, aksesibilitas, test, build rilis | APK / AAB siap |

### Template Prompt per Milestone

```
Konteks: proyek Android (Kotlin + Jetpack Compose) manajemen keuangan sesuai PRD (lampirkan bagian relevan).
Tugas: kerjakan milestone M5 (CRUD transaksi).
Batasan: ikuti RULES.md, uang bertipe Long, baca dari Room, tulis ke outbox.
Output: daftar file yang dibuat/diubah, kode lengkap, migration jika ada,
dan langkah verifikasi manual + unit test untuk logika saldo.
Jangan mengubah fitur di luar milestone ini.
```

### Data Seed Kategori Default

- **Pengeluaran:** Makan dan Minum, Transportasi, Belanja, Tagihan dan Utilitas, Hiburan, Kesehatan, Pendidikan, Rumah, Donasi / Zakat, Lainnya
- **Pemasukan:** Gaji, Bonus, Freelance, Hadiah, Investasi, Lainnya

---

*Dokumen ini hidup: perbarui setelah tiap fase selesai agar konteks vibe coding tetap akurat.*
