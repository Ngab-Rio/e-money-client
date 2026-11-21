package com.emoney.app.core.util

import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyFormatterTest {

    @Test
    fun `formats rupiah with id locale grouping`() {
        val formatted = MoneyFormatter.formatRupiah(1_250_000L)

        assertTrue(formatted.startsWith("Rp"))
        assertTrue(formatted.contains("1.250.000"))
    }

    @Test
    fun `formats zero`() {
        val formatted = MoneyFormatter.formatRupiah(0L)

        assertTrue(formatted.startsWith("Rp"))
        assertTrue(formatted.contains("0"))
    }
}
