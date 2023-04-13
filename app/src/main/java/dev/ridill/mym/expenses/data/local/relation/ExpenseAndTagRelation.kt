package dev.ridill.mym.expenses.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import dev.ridill.mym.expenses.data.local.entity.TagEntity

data class ExpenseAndTagRelation(
    @Embedded val expenseEntity: ExpenseEntity,
    @Relation(
        entity = TagEntity::class,
        entityColumn = "name",
        parentColumn = "tag"
    ) val tagEntity: TagEntity?
)