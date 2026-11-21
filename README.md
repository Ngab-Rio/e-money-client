# E-Money — Android Client

Aplikasi manajemen keuangan pribadi (Kotlin + Jetpack Compose + Supabase).
Sumber kebenaran produk: [`PRD Aplikasi Manajemen Keuangan (Kotlin + Supabase).md`](./PRD%20Aplikasi%20Manajemen%20Keuangan%20(Kotlin%20+%20Supabase).md).

**Status saat ini: M0 + F-02 Dompet (slice list/tambah/arsip).** Aplikasi berisi tema,
navigasi 5 tab, Room, dan pengelolaan dompet lokal. Belum ada autentikasi atau sinkronisasi.
Lihat [`docs/FEATURES.md`](docs/FEATURES.md) untuk urutan fitur.

## Prasyarat

| Tool | Versi |
| --- | --- |
| JDK | 17 (`/usr/lib/jvm/java-17-openjdk`) — jangan pakai JDK 27 |
| Android SDK | platform 36, build-tools 35+ (`/opt/android-sdk`) |
| Gradle | 8.14.3 (via wrapper; AGP 8.13 butuh minimal 8.13) |

## Setup

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH="$JAVA_HOME/bin:$PATH"

# 1. Isi local.properties (sudah dibuat lokal, tidak di-commit)
#    sdk.dir=/opt/android-sdk
#    SUPABASE_URL=...
#    SUPABASE_ANON_KEY=...

# 2. Build (wrapper sudah tersedia)
./gradlew :app:assembleDebug
```

JDK default mesin ini adalah 27, sedangkan AGP 8.13 membutuhkan JDK 17.

## Struktur

```
app/src/main/java/com/emoney/app/
  MoneyApp.kt              Application + Hilt
  MainActivity.kt
  core/theme/              warna, tipografi, tema (Calm Ledger)
  core/components/         komponen UI bersama
  navigation/              MoneyRoot, TopLevelDestination
  feature/                 home, transactions, addtransaction, budgets, more
```

Aturan arsitektur dan domain ada di `.opencode/skills/android-money-conventions/SKILL.md`.

## Skill proyek

| Skill | Kapan dipakai |
| --- | --- |
| `vibe-code-optimization` | Sebelum menulis/mengubah kode |
| `anti-slop-design` | Sebelum membuat layar/komponen/copy |
| `android-money-conventions` | Saat menyentuh Kotlin, Room, atau SQL |

Panggil dengan `@skill-id` atau biarkan agent memuatnya otomatis.

## Gate verifikasi

```bash
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```
