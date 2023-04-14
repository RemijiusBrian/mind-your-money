package dev.ridill.mym.expenses.presentation.add_edit_expense

import dev.ridill.mym.expenses.domain.model.Tag

data class AddEditExpenseState(
    val tagsList: List<Tag> = emptyList(),
    val selectedTagName: String? = null,
    val showDeleteConfirmation: Boolean = false
)