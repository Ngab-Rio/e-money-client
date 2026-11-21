package com.emoney.app.domain.model

data class Wallet(
    val id: String,
    val name: String,
    val type: WalletType,
    val currency: String,
    val initialBalance: Long,
    /**
     * F-02 belum memiliki transaksi, jadi saldo = saldo awal.
     * F-03 akan mengganti nilai ini dengan initial_balance + mutasi transaksi.
     */
    val balance: Long,
    val isArchived: Boolean,
)
