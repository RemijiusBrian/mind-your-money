package dev.ridill.mym.expenses.presentation.all_expenses

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import java.time.Month

interface AllExpensesActions {
    fun onTagSelect(tag: String)
    fun onTagDelete(tag: String)
    fun onTagDeleteDismiss()
    fun onTagDeleteConfirm()
    fun onNewTagClick()
    fun onNewTagDismiss()
    fun onNewTagNameChange(value: String)
    fun onNewTagColorSelect(color: Color)
    fun onNewTagConfirm()
    fun onYearSelect(year: String)
    fun onMonthSelect(month: Month)
    fun onExpenseLongClick(id: Long)
    fun onExpenseSelectionToggle(id: Long)
    fun onDismissMultiSelectionMode()
    fun onSelectionStateChange(currentState: ToggleableState)
    fun onDeleteExpensesClick()
    fun onDeleteExpensesDismissed()
    fun onDeleteExpensesConfirmed()
    fun onUntagExpensesClick()
}