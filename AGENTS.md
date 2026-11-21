# AGENTS.md — E-Money Android

Instruksi global tetap berlaku. Tambahan khusus proyek ini:

## Sumber kebenaran
- PRD: `PRD Aplikasi Manajemen Keuangan (Kotlin + Supabase).md`
- Skill proyek: `.opencode/skills/` (bukan `.github/skills/`)
  - `vibe-code-optimization` — workflow hemat konteks dan verifikasi.
  - `anti-slop-design` — token, komponen, copy, dan larangan desain generik.
  - `android-money-conventions` — invariant uang, Room-first, RLS, idempotensi.

## Lingkungan build
- Gunakan **JDK 17** (`JAVA_HOME=/usr/lib/jvm/java-17-openjdk`). JDK 27 tidak didukung AGP 8.13.
- Android SDK: `/opt/android-sdk` (platform 36 tersedia).
- Gradle wrapper memakai 8.14.3 (kompatibel dengan AGP 8.13; minimum 8.13).
- Jangan mengklaim build/test lulus sebelum perintahnya benar-benar dijalankan.

## Scope
- Satu milestone per sesi sesuai PRD §15.
- Jangan menambah dependency di luar yang ada di `gradle/libs.versions.toml` tanpa alasan.
- Semua teks UI lewat `strings.xml` (`values/` = id, `values-en/` = en).
- Jangan commit `local.properties` atau key Supabase.
