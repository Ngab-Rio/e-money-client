---
name: Vibe Code Optimization
description: Use for any coding, refactor, or debugging task in this repo. Token-efficient, verification-first workflow that keeps AI-generated diffs small, correct, and fast on Android/Compose. Load before writing code.
---

# Vibe Code Optimization

Aturan kerja untuk sesi vibe coding di proyek ini. Tujuannya bukan "banyak kode cepat",
tapi **perubahan kecil yang benar, terverifikasi, dan mudah di-review manusia**.

## 1. Loop wajib (jangan dilompati)

```
RECON → PLAN (≤ 8 baris) → IMPLEMENT (1 slice) → VERIFY → REPORT
```

1. **RECON** — baca hanya yang relevan: `PRD`, skill domain, file yang akan diubah,
   dan pemanggil/implementasi terdekat. Gunakan `grep`/glob dulu, jangan baca seluruh repo.
2. **PLAN** — tulis tujuan, file yang disentuh, kriteria diterima, risiko. Jika ada
   keputusan yang tidak memblokir, ambil asumsi wajar dan lanjut.
3. **IMPLEMENT** — satu slice vertikal (UI → ViewModel → use case → repository → DAO)
   dalam satu waktu. Jangan menyentuh fitur lain di commit yang sama.
4. **VERIFY** — jalankan gate yang tersedia (lihat §6). Laporkan hasil **nyata**,
   termasuk yang gagal atau belum bisa dijalankan karena tool belum terpasang.
5. **REPORT** — apa berubah, keputusan, test, risiko, cara verifikasi manual.

## 2. Ekonomi konteks (hemat token, tetap akurat)

- Cari dengan `grep` sebelum membaca; baca file besar hanya bagian yang relevan.
- Jangan membaca ulang file setelah edit; percayai hasil edit + diff.
- Jangan menempel ulang kode panjang di jawaban; tunjuk path + ringkasan.
- Jangan menghasilkan file contoh/duplikat yang tidak dipakai build.
- Satu milestone per sesi (sesuai PRD). Jangan "sekalian" mengerjakan milestone berikutnya.

## 3. Disiplin diff

**Lakukan**
- Patch terkecil yang memenuhi kriteria diterima.
- Ikuti pola yang sudah ada di repo (penamaan, struktur, error handling).
- Hapus import/variabel mati yang dibuat sendiri.
- Tambah test untuk logika uang, saldo, budget, idempotensi.

**Jangan**
- Refactor kosmetik file yang tidak terkait.
- Menambah dependency jika stdlib/AndroidX sudah cukup (jelaskan jika terpaksa).
- Membuat abstraksi spekulatif (`BaseRepository`, `UseCase<T,R>`) tanpa kebutuhan nyata.
- Meninggalkan `TODO`, `println`, kode komentar mati, atau placeholder palsu.
- Menyembunyikan error dengan `runCatching { }.getOrNull()` tanpa logging/penanganan.
- Mengklaim "sudah dites" tanpa menjalankan perintah.

## 4. Optimasi khusus Android/Kotlin

### Compose
- Composable **stateless**; state di-hoist ke ViewModel lewat `StateFlow`.
- Pakai `collectAsStateWithLifecycle()`, bukan `collectAsState()`.
- `LazyColumn`/`LazyRow`: selalu `key = { it.id }`; hindari item lambda yang membuat
  objek baru tiap recomposition.
- Turunkan state dengan `derivedStateOf` hanya untuk nilai yang sering berubah.
- Hindari `Modifier` chain dinamis di dalam loop; hoist ke variabel.
- Jangan menaruh I/O, format tanggal, atau query DB langsung di composable.
- Preview hanya untuk komponen murni; jangan preview yang butuh ViewModel/Hilt.

### ViewModel & data
- Satu `UiState` immutable per layar; expose `StateFlow<UiState>`.
- `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)`.
- Room `Flow` + `distinctUntilChanged()`; index kolom filter (`owner_id, occurred_at`).
- Hindari N+1: pakai query join/view, bukan loop query per item.
- Tulis ke Room dulu (single source of truth), lalu outbox; UI tidak menunggu network.
- Operasi tulis berpasangan (transaksi + saldo + budget) gunakan satu `@Transaction`.

### Uang & angka
- **Wajib `Long`** satuan terkecil; dilarang `Double`/`Float` untuk nominal.
- Format hanya di layer UI (`NumberFormat` locale `id_ID`), bukan di model.
- Pembagian/rasio: hitung dengan `Long`/`BigDecimal`, bulatkan eksplisit.

### Build & runtime
- R8 aktif di release; jangan matikan untuk "biar jalan".
- WorkManager: `ExistingPeriodicWorkPolicy.KEEP`, constraint network untuk sync.
- Hindari wake lock/alarm berlebih; jadwalkan ulang saat app dibuka.
- Ukuran APK: jangan commit aset besar; kompres gambar struk sebelum upload.

## 5. Anti-pattern yang langsung ditolak

| Bau kode | Ganti dengan |
| --- | --- |
| Logika bisnis di composable | ViewModel/use case |
| `GlobalScope` | `viewModelScope` / `CoroutineScope` ter-inject |
| `LiveData` baru di proyek Compose | `StateFlow` |
| Query DB di `init` composable | `LaunchedEffect`/ViewModel |
| `catch (e: Exception) {}` | tangani spesifik + log |
| Hardcoded string/angka UI | `strings.xml`, token desain |
| `System.currentTimeMillis()` untuk tanggal bisnis | `java.time` + timezone jelas |
| `amount: Double` | `amount: Long` |
| Sinkron manual dari UI | `SyncWorker` + outbox |

## 6. Gate verifikasi

Jalankan yang tersedia, dari yang paling murah:

```bash
./gradlew :app:compileDebugKotlin      # cepat: error kompilasi
./gradlew :app:testDebugUnitTest       # unit test
./gradlew :app:lintDebug               # lint
./gradlew :app:assembleDebug           # APK
```

Jika Gradle/SDK belum siap, **jangan** mengarang hasil. Tulis:
"belum dijalankan: <alasan>" dan verifikasi statis yang bisa dilakukan (baca diff,
cek import/resource, cek signature).

## 7. Definition of done

- [ ] Kriteria diterima PRD terpenuhi dan bisa didemokan.
- [ ] Tidak ada string/ warna/ ukuran hardcoded di UI.
- [ ] Uang bertipe `Long`; format hanya di UI.
- [ ] Test untuk jalur sukses + validasi + unauthorized (jika menyentuh data).
- [ ] Gate build/test dijalankan atau alasannya dilaporkan.
- [ ] Tidak ada perubahan di luar scope milestone.
