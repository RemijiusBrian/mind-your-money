package dev.ridill.mym.expenses.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.ridill.mym.core.data.db.BaseDao
import dev.ridill.mym.expenses.data.local.entity.ExpenseAndTagRelation
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao : BaseDao<ExpenseEntity> {

    @Transaction
    @Query(
        """
        SELECT *
        FROM ExpenseEntity
        WHERE strftime('%m-%Y', dateTime / 1000, 'unixepoch') = :date
        ORDER BY dateTime DESC
        """
    )
    fun getExpensesForDate(date: String): Flow<List<ExpenseAndTagRelation>>

    @Query(
        """
        SELECT IFNULL(SUM(amount), 0.0)
        FROM ExpenseEntity
        WHERE strftime('%m-%Y', dateTime / 1000, 'unixepoch') = :date
        """
    )
    fun getExpenditureForDate(date: String): Flow<Double>

    @Query("SELECT * FROM ExpenseEntity WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?
}