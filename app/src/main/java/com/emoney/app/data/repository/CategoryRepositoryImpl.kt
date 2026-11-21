package com.emoney.app.data.repository

import com.emoney.app.data.local.dao.CategoryDao
import com.emoney.app.data.local.entity.CategoryEntity
import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.CategoryType
import com.emoney.app.domain.repository.CategoryRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CategoryRepositoryImpl @Inject constructor(private val dao: CategoryDao) : CategoryRepository {
    override fun observeCategories(): Flow<List<Category>> = dao.observeActive()
        .onStart { dao.insertAll(defaults()) }
        .map { list -> list.map { it.toDomain() } }

    override suspend fun create(name: String, type: CategoryType, parentId: String?) {
        val now = Instant.now().toEpochMilli()
        dao.insert(CategoryEntity(UUID.randomUUID().toString(), name.trim(), type.name.lowercase(), parentId, createdAt = now, updatedAt = now))
    }

    override suspend fun rename(id: String, name: String) = dao.rename(id, name.trim(), Instant.now().toEpochMilli())
    override suspend fun archive(id: String) = dao.archive(id, Instant.now().toEpochMilli())

    private fun CategoryEntity.toDomain() = Category(id, name, CategoryType.valueOf(type.uppercase()), parentId, icon, color, isDefault)

    private fun defaults(): List<CategoryEntity> {
        val now = Instant.now().toEpochMilli()
        fun c(id: String, name: String, type: CategoryType, parent: String? = null) = CategoryEntity(id, name, type.name.lowercase(), parent, isDefault = true, createdAt = now, updatedAt = now)
        return listOf(
            c("default-income-salary", "Gaji", CategoryType.INCOME),
            c("default-income-other", "Pemasukan lain", CategoryType.INCOME),
            c("default-expense-food", "Makanan", CategoryType.EXPENSE),
            c("default-expense-transport", "Transportasi", CategoryType.EXPENSE),
            c("default-expense-bills", "Tagihan", CategoryType.EXPENSE),
            c("default-expense-shopping", "Belanja", CategoryType.EXPENSE),
            c("default-expense-health", "Kesehatan", CategoryType.EXPENSE),
            c("default-expense-other", "Lainnya", CategoryType.EXPENSE),
            c("default-expense-food-coffee", "Kopi", CategoryType.EXPENSE, "default-expense-food"),
        )
    }
}