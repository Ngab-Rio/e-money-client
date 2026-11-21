package com.emoney.app.domain.model

enum class WalletType {
    CASH,
    BANK,
    EWALLET,
    CREDIT_CARD,
    SAVINGS,
    ;

    companion object {
        fun fromStorage(value: String): WalletType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CASH
    }
}
