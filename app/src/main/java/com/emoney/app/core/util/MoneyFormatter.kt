package com.emoney.app.core.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Satu-satunya tempat format uang untuk UI. Nominal tetap Long di seluruh layer lain.
 */
object MoneyFormatter {
    private val indonesia = Locale("id", "ID")

    fun formatRupiah(amount: Long): String =
        NumberFormat.getCurrencyInstance(indonesia).format(amount)
}
