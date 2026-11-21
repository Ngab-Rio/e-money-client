package com.emoney.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emoney.app.data.local.dao.WalletDao
import com.emoney.app.data.local.dao.CategoryDao
import com.emoney.app.data.local.dao.TransactionDao
import com.emoney.app.data.local.entity.CategoryEntity
import com.emoney.app.data.local.entity.TransactionEntity
import com.emoney.app.data.local.entity.WalletEntity
import com.emoney.app.data.local.dao.BudgetDao
import com.emoney.app.data.local.entity.BudgetEntity

@Database(
    entities = [WalletEntity::class, CategoryEntity::class, TransactionEntity::class, BudgetEntity::class],
    version = 5,
    exportSchema = true,
)
abstract class MoneyDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
}
