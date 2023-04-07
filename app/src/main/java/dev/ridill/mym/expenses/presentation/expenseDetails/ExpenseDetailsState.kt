package dev.ridill.mym.expenses.presentation.expenseDetails

import dev.ridill.mym.expenses.domain.model.Tag

data class ExpenseDetailsState(
    val tagsList: List<Tag> = emptyList(),
    val selectedTagName: String? = null,
    val showDeleteConfirmation: Boolean = false
)