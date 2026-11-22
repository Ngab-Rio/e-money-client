package com.emoney.app.feature.wallets

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletFormValidatorTest {

    @Test
    fun `blank name is rejected`() {
        val errors = WalletFormValidator.validate(name = "   ", amountInput = "")

        assertTrue(errors.nameError)
        assertTrue(errors.hasErrors)
    }

    @Test
    fun `valid name with empty amount is accepted`() {
        val errors = WalletFormValidator.validate(name = "Dompet utama", amountInput = "")

        assertFalse(errors.hasErrors)
    }

    @Test
    fun `invalid amount is rejected`() {
        val errors = WalletFormValidator.validate(name = "Dompet", amountInput = "abc")

        assertTrue(errors.amountError)
    }

    @Test
    fun `formatted amount is accepted`() {
        val errors = WalletFormValidator.validate(name = "Dompet", amountInput = "1.250.000")

        assertFalse(errors.hasErrors)
    }

    @Test
    fun `reset balance accepts zero`() {
        val result = WalletFormValidator.validate("Cash", "0")
        assertFalse(result.hasErrors)
    }

    @Test
    fun `reset balance rejects invalid amount`() {
        val result = WalletFormValidator.validate("Cash", "abc")
        assertTrue(result.amountError)
    }
}
