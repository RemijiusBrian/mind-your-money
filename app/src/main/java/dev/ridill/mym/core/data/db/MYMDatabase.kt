package dev.ridill.mym.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.ridill.mym.expenses.data.local.ExpenseDao
import dev.ridill.mym.expenses.data.local.TagsDao
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import dev.ridill.mym.expenses.data.local.entity.TagEntity

@Database(
    entities = [
        ExpenseEntity::class,
        TagEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class MYMDatabase : RoomDatabase() {
    companion object {
        const val NAME = "mym.db"
    }

    // Dao
    abstract fun expenseDao(): ExpenseDao
    abstract fun tagsDao(): TagsDao
}