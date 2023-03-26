package dev.ridill.mym.expenses.data.repository

import androidx.compose.ui.graphics.Color
import dev.ridill.mym.expenses.data.local.entity.TagEntity
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagInput

fun TagEntity.toTag(): Tag = Tag(
    name = name,
    color = Color(colorCode)
)

fun TagInput.toEntity(): TagEntity = TagEntity(
    name = name,
    colorCode = colorCode
)