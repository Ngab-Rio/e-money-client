# F-01 Auth — Endpoint Fitur

Status: partial, email/password implemented

## Implemented

- Sign in with email/password via Supabase Auth REST API.
- Sign up with email/password.
- Password reset request by email.
- Password reset redirect target: `emoney://auth/reset`.
- Persistent local session in app `SharedPreferences`.
- Auth gate before the main application.
- Sign out from `Lainnya`.

## Not implemented yet

- Google Sign-In.
- Delete account.
- Full refresh-token rotation and session revalidation on app resume.
- Password reset completion screen and token exchange inside the app.
- Supabase profile bootstrap and owner-scoped local sync.

## Local contract

`AuthRepository.signIn(email, password)`

`AuthRepository.signUp(email, password)`

`AuthRepository.requestPasswordReset(email)`

The repository calls the configured `SUPABASE_URL` with the publishable/anon key from `local.properties`. No service-role key is used in the Android app.

## UI route

The auth screen is the app entry point when no local session exists. It supports Masuk, Daftar, and Lupa password.

## Verification

```text
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug
```

Remote sign-in requires the Supabase project URL/key and email confirmation settings to be valid. Automated tests cover local validation only.

## Supabase dashboard configuration

Add this URL under Authentication -> URL Configuration -> Redirect URLs:

```text
emoney://auth/reset
```

The Android app must be installed when the reset link is opened. Existing links generated before this redirect change may still point to `localhost:3000` or be expired; request a new reset email after configuring the URL.