# F-09 Keamanan — Endpoint Fitur

Status: done — PIN, sembunyikan saldo, app lock, dan biometrik implemented locally

## Implemented

- PIN 4–6 digit.
- PIN disimpan sebagai hash SHA-256, bukan plaintext.
- Toggle sembunyikan saldo di layar keamanan.
- Nominal beranda dan dompet dimasking sebagai `••••••`.
- Pengaturan tersedia di `Lainnya -> Keamanan`.
- AndroidX BiometricPrompt.
- App lock saat kembali ke foreground.
- App lock setelah 5 menit di background.
- AndroidX BiometricPrompt dengan fallback PIN.

## Not Implemented Yet

- Lock timeout/reauthentication setelah periode tertentu.
- Secure storage Android Keystore untuk token/PIN.

## Local contract

`SecurityPreferences.isPinEnabled()`

`SecurityPreferences.setPin(pin)`

`SecurityPreferences.verifyPin(pin)`

`SecurityPreferences.isBalanceHidden()`

`SecurityPreferences.observeBalanceHidden(): Flow<Boolean>`

PIN hash dan preference disimpan di SharedPreferences aplikasi.

## UI route

- `Lainnya -> Keamanan`
- Toggle PIN
- Toggle sembunyikan saldo

## Verification

```text
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assembleDebug
```
