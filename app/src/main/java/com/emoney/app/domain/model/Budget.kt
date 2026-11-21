package com.emoney.app.domain.model

data class Budget(
    val id: String,
    val categoryId: String?,
    val periodMonth: String,
    val amount: Long,
)

enum class BudgetThreshold { SAFE, WARNING, OVER }