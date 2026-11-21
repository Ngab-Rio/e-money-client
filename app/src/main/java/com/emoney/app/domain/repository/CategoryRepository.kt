package com.emoney.app.domain.repository

import com.emoney.app.domain.model.Category
import com.emoney.app.domain.model.CategoryType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun create(name: String, type: CategoryType, parentId: String?)
    suspend fun rename(id: String, name: String)
    suspend fun archive(id: String)
}