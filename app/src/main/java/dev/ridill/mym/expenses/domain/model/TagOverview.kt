package dev.ridill.mym.expenses.domain.model

import androidx.compose.ui.graphics.Color

data class TagOverview(
    val tag: String,
    val color: Color,
    val amount: Double,
    val percentOfTotal: Float
)