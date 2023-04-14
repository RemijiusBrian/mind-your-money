package dev.ridill.mym.expenses.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.ridill.mym.core.data.db.BaseDao
import dev.ridill.mym.expenses.data.local.entity.TagEntity
import dev.ridill.mym.expenses.data.local.relation.TagWithExpenditureRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface TagsDao : BaseDao<TagEntity> {

    @Query("SELECT * FROM TagEntity ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query(
        """
        SELECT tag.name as tag, tag.colorCode as colorCode,
            IFNULL((SELECT SUM(amount)
            FROM ExpenseEntity subExp
            WHERE subExp.tag = tag.name AND strftime('%m-%Y', subExp.dateTime / 1000, 'unixepoch') = :date), 0) as expenditure
        FROM TagEntity tag
        LEFT OUTER JOIN ExpenseEntity exp ON tag.name = exp.tag
        GROUP BY tag.name
        ORDER BY tag.name ASC
    """
    )
    fun getTagsWithExpendituresForDate(date: String): Flow<List<TagWithExpenditureRelation>>

    @Query("UPDATE ExpenseEntity SET tag = null WHERE tag = :tag")
    suspend fun removeTag(tag: String)

    @Query("DELETE FROM TagEntity WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Transaction
    suspend fun untagAndDeleteTag(tag: String) {
        removeTag(tag)
        deleteByName(tag)
    }

}