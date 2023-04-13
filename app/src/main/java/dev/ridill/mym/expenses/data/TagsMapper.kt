package dev.ridill.mym.expenses.data.repository

import androidx.compose.ui.graphics.Color
import dev.ridill.mym.expenses.data.local.entity.TagEntity
import dev.ridill.mym.expenses.data.local.relation.TagWithExpenditureRelation
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagInput
import dev.ridill.mym.expenses.domain.model.TagOverview

fun TagEntity.toTag(): Tag = Tag(
    name = name,
    color = Color(colorCode)
)

fun TagInput.toEntity(): TagEntity = TagEntity(
    name = name,
    colorCode = colorCode
)

fun TagWithExpenditureRelation.toTagOverview(
    totalExpenditure: Double
): TagOverview {
    val tagObj = if (colorCode == null) Tag.Untagged
    else Tag(tag, Color(colorCode))
    return TagOverview(
        tag = tagObj.name,
        color = tagObj.color,
        amount = expenditure,
        percentOfTotal = (expenditure / totalExpenditure).toFloat()
    )
}