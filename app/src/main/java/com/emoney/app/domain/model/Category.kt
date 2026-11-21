package com.emoney.app.domain.model

enum class CategoryType { INCOME, EXPENSE;
    fun labelRes(): Int = if (this == INCOME) com.emoney.app.R.string.categories_type_income else com.emoney.app.R.string.categories_type_expense
}

data class Category(
    val id: String,
    val name: String,
    val type: CategoryType,
    val parentId: String?,
    val icon: String,
    val color: String,
    val isDefault: Boolean,
)