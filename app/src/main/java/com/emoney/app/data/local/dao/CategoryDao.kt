package com.emoney.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emoney.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE is_archived = 0 ORDER BY type, parent_id IS NOT NULL, name COLLATE NOCASE")
    fun observeActive(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Insert
    suspend fun insert(category: CategoryEntity)

    @Query("UPDATE categories SET name = :name, updated_at = :updatedAt WHERE id = :id AND is_default = 0")
    suspend fun rename(id: String, name: String, updatedAt: Long)

    @Query("UPDATE categories SET is_archived = 1, updated_at = :updatedAt WHERE id = :id AND is_default = 0")
    suspend fun archive(id: String, updatedAt: Long)
}