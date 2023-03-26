package dev.ridill.mym.core.domain.util

import dev.ridill.mym.R
import dev.ridill.mym.core.domain.model.UiText
import dev.ridill.mym.expenses.domain.model.Expense

class Validator {

    fun validateExpense(expense: Expense): ValidationError? {
        if (expense.amount.toDoubleOrNull() == null) return ValidationError.InvalidAmount
        if (expense.note.isEmpty()) return ValidationError.ExpenseNoteEmpty
        return null
    }
}

sealed class ValidationError(
    val message: UiText
) {
    object InvalidAmount : ValidationError(UiText.StringResource(R.string.error_invalid_amount))
    object ExpenseNoteEmpty : ValidationError(UiText.StringResource(R.string.error_invalid_note))
}