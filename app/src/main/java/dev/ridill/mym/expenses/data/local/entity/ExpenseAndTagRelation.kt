package dev.ridill.mym.expenses.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ExpenseAndTagRelation(
    @Embedded val expenseEntity: ExpenseEntity,
    @Relation(
        entity = TagEntity::class,
        entityColumn = "name",
        parentColumn = "tag"
    ) val tagEntity: TagEntity?
)