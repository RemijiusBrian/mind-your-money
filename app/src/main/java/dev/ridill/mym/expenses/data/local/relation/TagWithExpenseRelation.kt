package dev.ridill.mym.expenses.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import dev.ridill.mym.expenses.data.local.entity.TagEntity

data class TagWithExpenseRelation(
    @Embedded
    val tag: TagEntity,
    @Relation(
        parentColumn = "name",
        entity = ExpenseEntity::class,
        entityColumn = "tag"
    )
    val expenses: List<ExpenseEntity>
)