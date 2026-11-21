---
name: Anti-Slop Design
description: Use when designing or building any screen, component, theme, copy, or visual asset. Prevents generic AI-looking UI (purple gradients, emoji icons, fake stats) and enforces a distinctive, accessible finance design system for this app.
---

# Anti-Slop Design

"Slop" = UI yang terlihat dihasilkan template AI: generik, penuh dekorasi tanpa alasan,
dan tidak punya karakter produk. Skill ini menetapkan arah visual **Calm Ledger**:
tenang, padat informasi, tepercaya, dan khas aplikasi keuangan Indonesia.

## 1. Sinyal slop — dilarang

**Visual**
- Gradient ungu/biru→pink, gradient text, glow, neon.
- Glassmorphism di semua tempat, blur berlebihan, bayangan besar lembut di setiap kartu.
- Sudut membulat seragam 24–32dp untuk semua elemen.
- Ikon emoji (💰 🚀 ✨) sebagai ikon fitur/navigasi.
- Ilustrasi AI generik tanpa makna, blob abstrak, karakter 3D.
- Tiga kartu fitur identik, hero center, badge "AI-powered" tanpa fungsi.
- Animasi bounce/spring berlebihan, parallax, confetti.

**Konten**
- Angka palsu, testimoni fiktif, logo klien, metrik "99.9% akurat".
- Copy hype: "Elevate your finances", "Supercharge", "Revolusi keuanganmu".
- Lorem ipsum, teks placeholder Inggris di UI berbahasa Indonesia.
- Label tombol kabur ("Lanjutkan", "Kelola") tanpa objek jelas.
- Empty state hanya "Tidak ada data" tanpa arahan.

**Struktur**
- Semua konten di tengah, tanpa hierarki.
- Terlalu banyak whitespace sampai informasi terpencar.
- Warna acak per komponen; tidak ada token.
- Dark mode sekadar invert (hitam pekat + teks putih menyilaukan).

## 2. Arah visual proyek

- **Mood:** buku kas yang rapi, tenang, presisi. Bukan startup SaaS, bukan game.
- **Primer:** teal gelap (uang sehat, bukan "hijau bank" mencolok).
- **Netral:** warm off-white / warm charcoal, bukan putih–hitam murni.
- **Aksen semantik:** pemasukan hijau, pengeluaran merah bata, transfer biru.
- **Angka adalah bintang:** nominal besar, tabular, alignment kanan pada daftar.
- **Kepadatan:** nyaman tapi efisien; target 3–6 transaksi terlihat tanpa scroll di ponsel kecil.

## 3. Token (sumber kebenaran: `core/theme`)

### Warna
| Token | Light | Dark | Pakai |
| --- | --- | --- | --- |
| primary | `#0B5D52` | `#7FD8C7` | aksi utama, FAB, tab aktif |
| onPrimary | `#FFFFFF` | `#00382F` | teks di atas primer |
| primaryContainer | `#CDEFE7` | `#0E4A41` | chip terpilih, highlight lembut |
| background | `#FAF7F2` | `#0E1513` | kanvas |
| surface | `#FFFFFF` | `#15201D` | kartu, sheet |
| surfaceVariant | `#EFEAE2` | `#24312D` | input, separator |
| onSurface | `#1B1C1A` | `#E7E4DE` | teks utama |
| onSurfaceVariant | `#5A5F5B` | `#A9B2AD` | teks sekunder |
| outline | `#8A918C` | `#6F7A75` | border |
| income | `#2E6B3E` | `#8FD3A0` | pemasukan |
| expense | `#A63A2E` | `#F2A79C` | pengeluaran |
| transfer | `#1D5FA8` | `#9CC4F0` | transfer |
| warning | `#8A5A00` | `#F0C36B` | 80% budget |
| danger | `#B3261E` | `#F2B8B5` | ≥100%, hapus |

Jangan menambah warna baru tanpa alasan fungsional. Warna hanya boleh berarti status.

### Spasi (kelipatan 4)
`4 · 8 · 12 · 16 · 24 · 32 · 48`. Padding layar: 16dp (ponsel), 24dp (tablet).
Jarak antar grup: 24dp; antar item dalam grup: 8–12dp.

### Radius
`8` (chip kecil, input) · `12` (kartu, tombol) · `16` (sheet, dialog) · `999` (pill).
Jangan pakai radius > 16 untuk kartu konten.

### Elevasi
Gunakan border/tonal, bukan bayangan tebal. Maksimum 1 level bayangan (`1–2dp`).
Bottom sheet & dialog boleh `3dp`.

### Tipografi
| Gaya | Ukuran / line-height | Pakai |
| --- | --- | --- |
| displaySmall | 36 / 44, tabular | saldo utama |
| headlineSmall | 24 / 32, tabular | nominal besar |
| titleLarge | 20 / 28 | judul layar |
| titleMedium | 16 / 24 | judul kartu |
| bodyLarge | 16 / 24 | isi |
| bodyMedium | 14 / 20 | metadata |
| labelLarge | 14 / 20 | tombol |
| labelSmall | 12 / 16 | caption, badge |

Wajib `fontFeatureSettings = "tnum"` untuk semua gaya angka.
Font sistem (Roboto) cukup untuk v1; jangan tambah font file tanpa alasan.

### Motion
Durasi `150–250ms`, easing `FastOutSlowInEasing`. Animasi hanya untuk:
perubahan state, transisi layar, dan konfirmasi aksi. Hormati "reduce motion".

## 4. Aturan komponen

- **Kartu:** surface + border `1dp` outline-variant; radius 12; padding 16.
  Kartu saldo boleh satu-satunya elemen tonal (primaryContainer).
- **Tombol:** satu primary per layar. Secondary = outlined. Destruktif = merah, butuh konfirmasi.
- **Daftar transaksi:** ikon kategori 40dp, judul 1 baris + ellipsis, nominal kanan tabular,
  pemisah tipis, tanpa kartu per item (hindari "card soup").
- **Bottom nav:** 5 slot dengan aksi tambah di tengah, label selalu tampil, ikon garis konsisten.
- **Form:** label di atas field, error di bawah field, helper text opsional, input nominal pakai numpad besar.
- **Chart:** maksimal 2 seri warna + label langsung; tanpa gridline berlebihan; sediakan fallback teks.
- **Empty state:** ikon garis 48dp, judul 1 baris, 1 kalimat penjelas, 1 aksi jelas.
- **Loading:** skeleton untuk daftar; spinner hanya untuk aksi singkat. Jangan layar kosong.
- **Error:** bahasa manusia + aksi ("Coba lagi" / "Simpan offline"), bukan stack trace.

## 5. Copywriting (Bahasa Indonesia)

- Sentence case, sapaan "kamu", hindari "Anda" campur.
- Sebut objek konkret: "Catat pengeluaran" > "Mulai".
- Angka: `Rp 1.250.000` (spasi setelah Rp, titik ribuan). Tanggal: `20 Nov 2026`.
- Jangan pakai emoji di UI produksi.
- Jangan Mengapit Setiap Kata Dengan Huruf Kapital.
- Istilah konsisten: "Dompet", "Transaksi", "Anggaran", "Tagihan", "Piutang", "Hutang".
- Semua teks lewat `strings.xml` (`values/` id, `values-en/` en). Tidak ada hardcoded string.

## 6. Dark mode

- Background `#0E1513`, surface `#15201D`; jangan hitam `#000000`.
- Turunkan saturasi aksen; jangan pakai warna light mode apa adanya.
- Border lebih penting daripada bayangan.
- Uji kontras teks sekunder minimal 4.5:1.

## 7. Aksesibilitas (wajib, bukan opsional)

- Target sentuh ≥ 48dp; ikon tanpa label wajib `contentDescription`.
- Kontras teks ≥ 4.5:1, elemen besar ≥ 3:1.
- Dukung font scale 200%: tidak ada teks terpotong, layout tidak rusak.
- Jangan sampaikan status hanya lewat warna (tambah ikon/teks).
- Urutan fokus TalkBack logis; heading semantik untuk section.
- Chart wajib punya ringkasan teks untuk pembaca layar.

## 8. Review checklist (10 poin)

- [ ] Tidak ada gradient ungu/glassmorphism/emoji ikon.
- [ ] Warna & spasi berasal dari token, bukan angka acak.
- [ ] Hanya satu aksi primer yang dominan per layar.
- [ ] Nominal memakai angka tabular dan format `id_ID`.
- [ ] Informasi penting terlihat tanpa scroll berlebihan.
- [ ] Empty/loading/error state dirancang, bukan dibiarkan.
- [ ] Dark mode diuji, bukan hasil invert.
- [ ] Semua teks dari `strings.xml`, nada konsisten.
- [ ] Kontras & target sentuh lolos.
- [ ] Bisa dijelaskan alasannya: setiap elemen punya fungsi.

Jika ada elemen yang gagal 2+ poin, perbaiki sebelum menambah fitur baru.
