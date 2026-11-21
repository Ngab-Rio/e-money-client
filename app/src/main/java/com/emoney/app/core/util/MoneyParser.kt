package com.emoney.app.core.util

/**
 * Parser nominal untuk input pengguna. Hanya digit yang diterima; tidak pernah
 * memakai Double/Float. Batas 15 digit mencegah overflow Long.
 */
object MoneyParser {
    const val MAX_DIGITS = 15

    fun sanitize(raw: String): String = raw.filter { it.isDigit() }.take(MAX_DIGITS)

    fun parse(raw: String): Long? {
        val digits = sanitize(raw)
        if (digits.isEmpty()) return null
        return digits.toLongOrNull()
    }

    fun parseOrZero(raw: String): Long = parse(raw) ?: 0L
}
