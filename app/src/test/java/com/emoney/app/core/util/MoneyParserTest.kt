package com.emoney.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyParserTest {

    @Test
    fun `sanitize keeps digits only`() {
        assertEquals("1250000", MoneyParser.sanitize("Rp 1.250.000"))
    }

    @Test
    fun `sanitize caps input at max digits`() {
        assertEquals(
            "123456789012345",
            MoneyParser.sanitize("1234567890123456"),
        )
    }

    @Test
    fun `parse returns null for empty input`() {
        assertNull(MoneyParser.parse(""))
        assertNull(MoneyParser.parse("Rp"))
    }

    @Test
    fun `parse reads formatted currency input`() {
        assertEquals(1_250_000L, MoneyParser.parse("1.250.000"))
    }

    @Test
    fun `parseOrZero falls back to zero`() {
        assertEquals(0L, MoneyParser.parseOrZero(""))
        assertEquals(500L, MoneyParser.parseOrZero("500"))
    }

    @Test
    fun `parse never overflows Long`() {
        val maxDigits = "9".repeat(MoneyParser.MAX_DIGITS)
        assertEquals(maxDigits.toLong(), MoneyParser.parse(maxDigits))
    }
}
