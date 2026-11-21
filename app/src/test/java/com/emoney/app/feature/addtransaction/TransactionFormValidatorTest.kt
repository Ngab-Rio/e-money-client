package com.emoney.app.feature.addtransaction

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionFormValidatorTest {
    @Test fun `zero amount is rejected`() { assertTrue(TransactionFormValidator.validate(0, "wallet", "category").amountError) }
    @Test fun `negative amount is rejected`() { assertTrue(TransactionFormValidator.validate(-1, "wallet", "category").hasErrors) }
    @Test fun `wallet and category are required`() { assertTrue(TransactionFormValidator.validate(1000, null, null).selectionError) }
    @Test fun `valid transaction is accepted`() { assertFalse(TransactionFormValidator.validate(1000, "wallet", "category").hasErrors) }
    @Test fun `transfer requires a different destination wallet`() { assertTrue(TransactionFormValidator.validate(1000, "wallet", null, "wallet", true).selectionError) }
    @Test fun `valid transfer is accepted without category`() { assertFalse(TransactionFormValidator.validate(1000, "wallet", null, "other-wallet", true).hasErrors) }
}