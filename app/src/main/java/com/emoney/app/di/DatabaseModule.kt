package com.emoney.app.di

import android.content.Context
import androidx.room.Room
import com.emoney.app.data.local.MoneyDatabase
import com.emoney.app.data.local.dao.WalletDao
import com.emoney.app.data.local.dao.CategoryDao
import com.emoney.app.data.local.dao.TransactionDao
import com.emoney.app.data.local.dao.BudgetDao
import com.emoney.app.data.repository.WalletRepositoryImpl
import com.emoney.app.domain.repository.WalletRepository
import com.emoney.app.domain.repository.CategoryRepository
import com.emoney.app.domain.repository.TransactionRepository
import com.emoney.app.domain.repository.BudgetRepository
import com.emoney.app.data.repository.CategoryRepositoryImpl
import com.emoney.app.data.repository.TransactionRepositoryImpl
import com.emoney.app.data.repository.BudgetRepositoryImpl
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoneyDatabase =
        Room.databaseBuilder(context, MoneyDatabase::class.java, "money.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .build()

    @Provides
    fun provideWalletDao(database: MoneyDatabase): WalletDao = database.walletDao()

    @Provides
    fun provideCategoryDao(database: MoneyDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: MoneyDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideBudgetDao(database: MoneyDatabase): BudgetDao = database.budgetDao()

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, type TEXT NOT NULL, parent_id TEXT, icon TEXT NOT NULL, color TEXT NOT NULL, is_default INTEGER NOT NULL, is_archived INTEGER NOT NULL, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL)")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS transactions (id TEXT NOT NULL PRIMARY KEY, type TEXT NOT NULL, amount INTEGER NOT NULL, wallet_id TEXT NOT NULL, category_id TEXT NOT NULL, note TEXT, occurred_at INTEGER NOT NULL, created_at INTEGER NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS transactions_wallet_idx ON transactions(wallet_id)")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE transactions_new (id TEXT NOT NULL PRIMARY KEY, type TEXT NOT NULL, amount INTEGER NOT NULL, wallet_id TEXT NOT NULL, category_id TEXT, to_wallet_id TEXT, note TEXT, occurred_at INTEGER NOT NULL, created_at INTEGER NOT NULL)")
            db.execSQL("INSERT INTO transactions_new (id, type, amount, wallet_id, category_id, note, occurred_at, created_at) SELECT id, type, amount, wallet_id, category_id, note, occurred_at, created_at FROM transactions")
            db.execSQL("DROP TABLE transactions")
            db.execSQL("ALTER TABLE transactions_new RENAME TO transactions")
            db.execSQL("CREATE INDEX IF NOT EXISTS transactions_wallet_idx ON transactions(wallet_id)")
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE budgets (id TEXT NOT NULL PRIMARY KEY, category_id TEXT, period_month TEXT NOT NULL, amount INTEGER NOT NULL, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL)")
            db.execSQL("CREATE INDEX budgets_period_idx ON budgets(period_month)")
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWalletRepository(impl: WalletRepositoryImpl): WalletRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository
}
