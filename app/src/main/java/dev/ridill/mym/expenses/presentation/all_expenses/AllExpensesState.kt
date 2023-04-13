package dev.ridill.mym.expenses.presentation.all_expenses

import androidx.compose.ui.state.ToggleableState
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.model.TagOverview

data class AllExpensesState(
    val tagOverviews: List<TagOverview> = emptyList(),
    val selectedTag: String? = null,
    val totalExpenditureForDate: Double = Double.Zero,
    val showTagInput: Boolean = false,
    val yearsList: List<String> = emptyList(),
    val selectedYear: String = "",
    val selectedMonth: Int = 1,
    val expensesByTagForDate: List<Expense> = emptyList(),
    val showTagDeletionConfirmation: Boolean = false,
    val multiSelectionModeActive: Boolean = false,
    val selectedExpenseIds: List<Long> = emptyList(),
    val expenseSelectionState: ToggleableState = ToggleableState.Off,
    val showExpenseDeleteConfirmation: Boolean = false
) {
    companion object {
        val INITIAL = AllExpensesState()
    }
}