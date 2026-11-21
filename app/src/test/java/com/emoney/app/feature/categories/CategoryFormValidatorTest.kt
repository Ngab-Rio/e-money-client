package com.emoney.app.feature.categories

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryFormValidatorTest {
    @Test fun `blank name is rejected`() {
        assertTrue(CategoryFormValidator.validate("   ").hasErrors)
    }

    @Test fun `normal name is accepted`() {
        assertFalse(CategoryFormValidator.validate("Makanan").hasErrors)
    }

    @Test fun `name above sixty characters is rejected`() {
        assertTrue(CategoryFormValidator.validate("x".repeat(61)).hasErrors)
    }
}