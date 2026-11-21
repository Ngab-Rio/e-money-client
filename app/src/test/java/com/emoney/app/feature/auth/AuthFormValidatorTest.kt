package com.emoney.app.feature.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthFormValidatorTest {
    @Test fun `valid email and password are accepted`() { assertFalse(AuthFormValidator.validate("user@example.com", "secret1").hasErrors) }
    @Test fun `invalid email is rejected`() { assertTrue(AuthFormValidator.validate("invalid", "secret1").emailError) }
    @Test fun `short password is rejected`() { assertTrue(AuthFormValidator.validate("user@example.com", "123").passwordError) }
}