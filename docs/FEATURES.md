# Breakdown Fitur

Aturan kerja: **satu fitur per sesi**. Jangan mencampur fitur lain dalam satu perubahan.
Status: `todo` · `wip` · `done`.

## Urutan pengerjaan

Urutan ini menyimpang dari nomor milestone PRD karena Supabase/auth belum punya
kredensial. Fitur lokal dikerjakan lebih dulu, lalu disambungkan ke auth + sync.

| # | Fitur | Slice | Depends on | Status |
| --- | --- | --- | --- | --- |
| 1 | F-02 Dompet | list, tambah, arsipkan | M0 | done (build+test+lint hijau) |
| 2 | F-02 Dompet | detail, ubah nama/tipe, atur ulang saldo | #1 + F-03 | done (build+test+lint hijau) |
| 3 | F-04 Kategori | seed bawaan, list, tambah/ubah, subkategori | #1 | done (implementasi lokal + verifikasi) |
| 4 | F-03 Transaksi | income/expense + form + saldo | #1,#3 | done (build+test+lint hijau) |
| 5 | F-03 Transaksi | transfer antar dompet | #4 | done (build+test+lint hijau) |
| 6 | F-06 Dashboard | saldo total, ringkasan bulan, transaksi terbaru | #4 | done (build+test+lint hijau) |
| 7 | F-05 Anggaran | CRUD budget + progress + ambang | #3,#4 | done (build+test+lint hijau) |
| 8 | F-07/F-08 Laporan & filter | kategori, tren, filter periode/dompet/tag | #4 | done (build+test+lint hijau; tag menunggu field transaksi) |
| 9 | F-09 Keamanan | PIN, biometrik, sembunyikan saldo | #1 | done (PIN, hide balance, app lock, biometrik, lint hijau) |
| 10 | F-01 Auth | email/password, Google, reset, hapus akun | M1 Supabase | wip (email/password selesai; Google + hapus akun belum) |
| 11 | F-10 Sinkronisasi | Room + outbox + SyncWorker + Realtime | #10 | todo |
| 12 | F-11 Tagihan | bills, occurrence, notifikasi, tandai lunas | #4,#11 | todo |
| 13 | F-12 Hutang/piutang | hutang, piutang, cicilan, pengingat | #4,#11 | todo |
| 14 | P-01 Berulang | rule, occurrence idempoten, cron | #11 | todo |
| 15 | P-04 Target tabungan | target, setoran, estimasi | #4 | todo |
| 16 | P-14 Rollover budget | sisa bulan lalu | #7 | todo |
| 17 | P-03 OCR struk | ML Kit + heuristik + edit | #4 | todo |
| 18 | P-11 Ekspor | CSV, PDF, backup JSON | #4 | todo |
| 19 | P-07/P-08/P-09/P-10/P-16 | langganan, auto-kategori, prediksi, skor, insight | #4,#7 | todo |
| 20 | P-06/P-12/P-13/P-15/P-17/P-18/P-19 | split bill, dompet bersama, multi-currency, net worth, zakat, impor, widget | #11 | todo |

## Definition of done per fitur

- [ ] Satu slice vertikal: UI → ViewModel → use case/repository → DAO/SQL.
- [ ] Kriteria penerimaan PRD untuk slice tersebut terpenuhi.
- [ ] Test: sukses, validasi, unauthorized (jika ada data), edge case.
- [ ] `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug`.
- [ ] Strings id + en, warna/spasi dari token, empty/loading/error state.
- [ ] Laporan hasil verifikasi nyata (bukan klaim).

## Catatan dependency

- F-02..F-09 berjalan lokal (Room) tanpa auth; `owner_id` ditambahkan saat F-01/F-10.
- Migration SQL ditulis per fitur di `supabase/migrations/`, bukan satu dump besar.
- Saldo dompet baru benar-benar terhitung setelah F-03 (transaksi) ada.
