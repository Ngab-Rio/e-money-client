package com.emoney.app.feature.categories

data class CategoryValidation(val nameError: Boolean) {
    val hasErrors: Boolean get() = nameError
}

object CategoryFormValidator {
    fun validate(name: String): CategoryValidation = CategoryValidation(
        nameError = name.isBlank() || name.trim().length > MAX_NAME_LENGTH,
    )

    private const val MAX_NAME_LENGTH = 60
}