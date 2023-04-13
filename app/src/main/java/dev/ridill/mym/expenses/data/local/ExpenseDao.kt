package dev.ridill.mym.expenses.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.ridill.mym.core.data.db.BaseDao
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import dev.ridill.mym.expenses.data.local.relation.ExpenseAndTagRelation
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

    @Query("SELECT DISTINCT(strftime('%Y', dateTime / 1000, 'unixepoch')) FROM ExpenseEntity ORDER BY dateTime DESC")
    fun getDistinctYears(): Flow<List<Int>>

    @Query(
        """
        SELECT *
        FROM ExpenseEntity
        WHERE (tag = :tag OR :tag IS NULL) AND strftime('%m-%Y', dateTime / 1000, 'unixepoch') = :date
    """
    )
    fun getExpensesByTagForDate(
        tag: String?,
        date: String
    ): Flow<List<ExpenseEntity>>

    @Query("UPDATE ExpenseEntity SET tag = :tag WHERE id IN (:ids)")
    suspend fun setTagToExpenses(tag: String?, ids: List<Long>)

    @Query("DELETE FROM ExpenseEntity WHERE id IN (:ids)")
    suspend fun deleteMultipleExpenses(ids: List<Long>)
}