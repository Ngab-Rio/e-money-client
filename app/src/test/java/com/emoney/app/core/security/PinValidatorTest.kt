package com.emoney.app.core.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PinValidatorTest {
    @Test fun `four digit pin is valid`() { assertTrue(PinValidator.isValid("1234")) }
    @Test fun `six digit pin is valid`() { assertTrue(PinValidator.isValid("123456")) }
    @Test fun `pin below four digits is invalid`() { assertFalse(PinValidator.isValid("123")) }
    @Test fun `pin above six digits is invalid`() { assertFalse(PinValidator.isValid("1234567")) }
    @Test fun `non numeric pin is invalid`() { assertFalse(PinValidator.isValid("12ab")) }
}
