package dev.ridill.mym.expenses.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.mym.R
import dev.ridill.mym.core.domain.model.UiText
import dev.ridill.mym.core.domain.util.Validator
import dev.ridill.mym.core.navigation.screenSpecs.ARG_INVALID_ID_LONG
import dev.ridill.mym.core.navigation.screenSpecs.ExpenseDetailsScreenSpec
import dev.ridill.mym.core.util.asStateFlow
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagInput
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import dev.ridill.mym.expenses.domain.repository.TagsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val expenseRepo: ExpenseRepository,
    private val tagsRepo: TagsRepository,
    private val validator: Validator
) : ViewModel() {

    private val expenseId = ExpenseDetailsScreenSpec
        .getExpenseIdFromSavedStateHandle(savedStateHandle)
    private val isEditMode = expenseId != ARG_INVALID_ID_LONG

    private val expense = savedStateHandle.getStateFlow(KEY_EXPENSE, Expense.DEFAULT)
    val amount = expense.map { it.amount }.distinctUntilChanged()
    val note = expense.map { it.note }.distinctUntilChanged()

    private val tagsList = tagsRepo.getAllTags()
    private val selectedTagName = expense.map { it.tagName }.distinctUntilChanged()
    private val showDeleteConfirmation =
        savedStateHandle.getStateFlow(SHOW_DELETE_CONFIRMATION, false)

    val newTag = savedStateHandle.getStateFlow(NEW_TAG, TagInput.INITIAL)

    val state = combineTuple(
        tagsList,
        selectedTagName,
        showDeleteConfirmation
    ).map { (
                tagsList,
                selectedTagName,
                showDeleteConfirmation
            ) ->
        ExpenseDetailsState(
            tagsList = tagsList,
            selectedTagName = selectedTagName,
            showDeleteConfirmation = showDeleteConfirmation
        )
    }.asStateFlow(viewModelScope, ExpenseDetailsState())

    init {
        onInit()
    }

    private val eventsChannel = Channel<ExpenseDetailsEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    private fun onInit() = viewModelScope.launch {
        savedStateHandle[KEY_EXPENSE] = expenseRepo.getExpenseById(expenseId)
            ?: Expense.DEFAULT
    }

    fun onNoteChange(value: String) {
        savedStateHandle[KEY_EXPENSE] = expense.value
            .copy(note = value)
    }

    fun onAmountChange(value: String) {
        savedStateHandle[KEY_EXPENSE] = expense.value
            .copy(amount = value)
    }

    fun onTagSelect(tag: Tag) {
        savedStateHandle[KEY_EXPENSE] = expense.value
            .copy(tagName = tag.name.takeIf { it != expense.value.tagName })
    }

    fun onDeleteClick() {
        savedStateHandle[SHOW_DELETE_CONFIRMATION] = true
    }

    fun onShowDeleteWarningDismiss() {
        savedStateHandle[SHOW_DELETE_CONFIRMATION] = false
    }

    fun onShowDeleteWarningConfirm() {
        savedStateHandle[SHOW_DELETE_CONFIRMATION] = false
    }

    fun onNewTagClick() {
        viewModelScope.launch {
            eventsChannel.send(ExpenseDetailsEvent.ToggleTagInput(true))
        }
    }

    fun onNewTagNameChange(value: String) {
        savedStateHandle[NEW_TAG] = newTag.value
            .copy(name = value)
    }

    fun onNewTagColorSelect(value: Color) {
        savedStateHandle[NEW_TAG] = newTag.value
            .copy(colorCode = value.toArgb())
    }

    fun onNewTagDismiss() {
        viewModelScope.launch {
            eventsChannel.send(ExpenseDetailsEvent.ToggleTagInput(false))
        }
    }

    fun onNewTagConfirm() {
        viewModelScope.launch {
            val input = newTag.value
            if (input.name.isEmpty()) return@launch

            tagsRepo.insert(input)
            savedStateHandle[KEY_EXPENSE] = expense.value
                .copy(tagName = input.name)
            eventsChannel.send(ExpenseDetailsEvent.ToggleTagInput(false))
            eventsChannel.send(ExpenseDetailsEvent.ShowUiMessage(UiText.StringResource(R.string.tag_created)))
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val expense = expense.value
            val error = validator.validateExpense(expense)

            if (error != null) {
                eventsChannel.send(ExpenseDetailsEvent.ShowUiMessage(error.message, true))
                return@launch
            }

            expenseRepo.insert(expense)
            eventsChannel.send(
                ExpenseDetailsEvent.NavigateBackWithResult(
                    if (isEditMode) EXPENSE_UPDATED
                    else EXPENSE_ADDED
                )
            )
        }
    }

    sealed class ExpenseDetailsEvent {
        data class ShowUiMessage(val uiText: UiText, val error: Boolean = false) :
            ExpenseDetailsEvent()

        data class NavigateBackWithResult(val result: String) : ExpenseDetailsEvent()

        data class ToggleTagInput(val show: Boolean) : ExpenseDetailsEvent()
    }
}

private const val KEY_EXPENSE = "KEY_EXPENSE"
private const val SHOW_DELETE_CONFIRMATION = "SHOW_DELETE_CONFIRMATION"
private const val NEW_TAG = "NEW_TAG"

const val EXPENSE_DETAILS_ACTION = "EXPENSE_DETAILS_ACTION"
const val EXPENSE_ADDED = "EXPENSE_ADDED"
const val EXPENSE_UPDATED = "EXPENSE_UPDATED"
const val EXPENSE_DELETED = "EXPENSE_DELETED"