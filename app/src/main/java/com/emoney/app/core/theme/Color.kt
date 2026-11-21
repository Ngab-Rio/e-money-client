package com.emoney.app.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand — "Calm Ledger": teal gelap, netral hangat, aksen semantik.
internal val Teal800 = Color(0xFF0B5D52)
internal val Teal900 = Color(0xFF073F38)
internal val Teal950 = Color(0xFF00382F)
internal val Teal200 = Color(0xFF7FD8C7)
internal val Teal100 = Color(0xFFCDEFE7)
internal val Teal700 = Color(0xFF0F6B5C)
internal val TealContainerDark = Color(0xFF0E4A41)

internal val WarmCanvas = Color(0xFFFAF7F2)
internal val WarmSurface = Color(0xFFFFFFFF)
internal val WarmSurfaceVariant = Color(0xFFEFEAE2)
internal val Ink = Color(0xFF1B1C1A)
internal val InkMuted = Color(0xFF5A5F5B)
internal val OutlineLight = Color(0xFF8A918C)

internal val DarkCanvas = Color(0xFF0E1513)
internal val DarkSurface = Color(0xFF15201D)
internal val DarkSurfaceVariant = Color(0xFF24312D)
internal val DarkInk = Color(0xFFE7E4DE)
internal val DarkInkMuted = Color(0xFFA9B2AD)
internal val OutlineDark = Color(0xFF6F7A75)

internal val IncomeLight = Color(0xFF2E6B3E)
internal val ExpenseLight = Color(0xFFA63A2E)
internal val TransferLight = Color(0xFF1D5FA8)
internal val WarningLight = Color(0xFF8A5A00)

internal val IncomeDark = Color(0xFF8FD3A0)
internal val ExpenseDark = Color(0xFFF2A79C)
internal val TransferDark = Color(0xFF9CC4F0)
internal val WarningDark = Color(0xFFF0C36B)

internal val LightColorScheme = lightColorScheme(
    primary = Teal800,
    onPrimary = Color.White,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal950,
    secondary = Teal700,
    onSecondary = Color.White,
    secondaryContainer = Teal100,
    onSecondaryContainer = Teal950,
    tertiary = TransferLight,
    onTertiary = Color.White,
    background = WarmCanvas,
    onBackground = Ink,
    surface = WarmSurface,
    onSurface = Ink,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = InkMuted,
    outline = OutlineLight,
    outlineVariant = Color(0xFFDCD6CC),
    error = Color(0xFFB3261E),
    onError = Color.White,
)

internal val DarkColorScheme = darkColorScheme(
    primary = Teal200,
    onPrimary = Teal950,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = Teal100,
    secondary = Teal200,
    onSecondary = Teal950,
    secondaryContainer = TealContainerDark,
    onSecondaryContainer = Teal100,
    tertiary = TransferDark,
    onTertiary = Color(0xFF06213D),
    background = DarkCanvas,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkInkMuted,
    outline = OutlineDark,
    outlineVariant = Color(0xFF3A4742),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
)
