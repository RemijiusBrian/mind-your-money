package dev.ridill.mym.expenses.presentation.add_edit_expense

import androidx.compose.ui.graphics.Color
import dev.ridill.mym.expenses.domain.model.Tag

interface AddEditExpenseActions {
    fun onNoteChange(value: String)
    fun onAmountChange(value: String)
    fun onTagSelect(tag: Tag)
    fun onDeleteClick()
    fun onDeleteDismiss()
    fun onDeleteConfirm()
    fun onNewTagClick()
    fun onNewTagNameChange(value: String)
    fun onNewTagColorSelect(value: Color)
    fun onNewTagDismiss()
    fun onNewTagConfirm()
    fun onSave()
}