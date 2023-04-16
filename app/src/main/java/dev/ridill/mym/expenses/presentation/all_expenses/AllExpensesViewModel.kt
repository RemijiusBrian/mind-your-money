package dev.ridill.mym.expenses.presentation.all_expenses

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.mym.R
import dev.ridill.mym.core.domain.model.UiText
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.asStateFlow
import dev.ridill.mym.expenses.domain.model.TagInput
import dev.ridill.mym.expenses.domain.repository.AllExpensesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Month
import javax.inject.Inject

@HiltViewModel
class AllExpensesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: AllExpensesRepository
) : ViewModel(), AllExpensesActions {

    private val yearsList = repo.getYearsList()
    private val selectedYear = savedStateHandle
        .getStateFlow(KEY_SELECTED_YEAR, DateUtil.currentDateTime().year.toString())
    private val selectedMonth = savedStateHandle
        .getStateFlow(KEY_SELECTED_MONTH, DateUtil.currentDateTime().monthValue)
    private val selectedDate = combineTuple(
        selectedYear,
        selectedMonth
    ).map { (year, month) ->
        val paddedMonth = month.toString().padStart(2, '0')
        "$paddedMonth-$year"
    }.distinctUntilChanged()
    private val totalExpenditureForDate = selectedDate.flatMapLatest { date ->
        repo.getTotalExpenditure(date)
    }.distinctUntilChanged()

    private val tagOverviews = combineTuple(
        totalExpenditureForDate,
        selectedDate
    ).flatMapLatest { (expenditure, date) ->
        repo.getTagOverviews(expenditure, date)
    }
    private val selectedTag = savedStateHandle.getStateFlow<String?>(KEY_SELECTED_TAG, null)

    private val showTagDeletionConfirmation = savedStateHandle
        .getStateFlow(KEY_SHOW_TAG_DELETE_CONFIRMATION, false)

    private val expensesByTagForDate = combineTuple(
        selectedTag,
        selectedDate
    ).flatMapLatest { (tag, date) ->
        repo.getExpensesByTagForDate(tag, date)
    }

    private val multiSelectionModeActive = savedStateHandle
        .getStateFlow(KEY_MULTI_SELECTION_ACTIVE, false)
    private val selectedExpenseIds = savedStateHandle
        .getStateFlow<List<Long>>(KEY_SELECTED_EXPENSE_IDS, emptyList())
    private val expenseSelectionState = combineTuple(
        expensesByTagForDate,
        selectedExpenseIds
    ).map { (expenses, selectedIds) ->
        val expenseIds = expenses.map { it.id }
        when {
            expenseIds.all { it in selectedIds } -> ToggleableState.On
            expenseIds.none { it in selectedIds } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
    }

    private val showExpenseDeletionConfirmation =
        savedStateHandle.getStateFlow(KEY_SHOW_EXPENSE_DELETE_CONFIRMATION, false)

    val tagInput = savedStateHandle.getStateFlow<TagInput?>(KEY_TAG_INPUT, null)

    val state = combineTuple(
        tagOverviews,
        selectedTag,
        totalExpenditureForDate,
        yearsList,
        selectedYear,
        selectedMonth,
        showTagDeletionConfirmation,
        expensesByTagForDate,
        multiSelectionModeActive,
        selectedExpenseIds,
        expenseSelectionState,
        showExpenseDeletionConfirmation
    ).map { (
                tagOverviews,
                selectedTag,
                totalExpenditureForDate,
                yearsList,
                selectedYear,
                selectedMonth,
                showTagDeletionConfirmation,
                expensesByTagForDate,
                multiSelectionModeActive,
                selectedExpenseIds,
                expenseSelectionState,
                showExpenseDeletionConfirmation
            ) ->
        AllExpensesState(
            tagOverviews = tagOverviews,
            selectedTag = selectedTag,
            totalExpenditureForDate = totalExpenditureForDate,
            yearsList = yearsList,
            selectedYear = selectedYear,
            selectedMonth = selectedMonth,
            showTagDeletionConfirmation = showTagDeletionConfirmation,
            expensesByTagForDate = expensesByTagForDate,
            multiSelectionModeActive = multiSelectionModeActive,
            selectedExpenseIds = selectedExpenseIds,
            expenseSelectionState = expenseSelectionState,
            showExpenseDeleteConfirmation = showExpenseDeletionConfirmation
        )
    }.asStateFlow(viewModelScope, AllExpensesState.INITIAL)

    init {
        collectExpenseYears()
        collectTagOverviews()
    }

    private val eventsChannel = Channel<AllExpenseEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    private fun collectExpenseYears() = viewModelScope.launch {
        repo.getYearsList().collectLatest { years ->
            savedStateHandle[KEY_SELECTED_YEAR] = selectedYear.value
                .ifEmpty { years.firstOrNull()?.toString() }.orEmpty()
        }
    }

    private fun collectTagOverviews() = viewModelScope.launch {
        combineTuple(
            totalExpenditureForDate,
            selectedDate
        ).flatMapLatest { (expenditure, date) ->
            repo.getTagOverviews(expenditure, date)
        }.collectLatest { overviews ->
            val tags = overviews.map { it.tag }
            savedStateHandle[KEY_SELECTED_TAG] = selectedTag.value.takeIf { it in tags }
        }
    }

    private suspend fun assignTagToExpenses(tag: String?) {
        val selectedExpenses = state.value.selectedExpenseIds
        repo.tagExpenses(tag, selectedExpenses)
        disableMultiSelectionMode()
    }

    override fun onTagSelect(tag: String) {
        if (multiSelectionModeActive.value) viewModelScope.launch {
            assignTagToExpenses(tag)
            eventsChannel.send(
                AllExpenseEvent.ShowUiMessage(
                    UiText.StringResource(R.string.expenses_tagged_as, tag)
                )
            )
        } else {
            savedStateHandle[KEY_SELECTED_TAG] = tag.takeIf { it != selectedTag.value }
        }
    }

    private var deletionTag: String? = null
    override fun onTagDelete(tag: String) {
        deletionTag = tag
        savedStateHandle[KEY_SHOW_TAG_DELETE_CONFIRMATION] = true
    }

    override fun onTagDeleteDismiss() {
        deletionTag = null
        savedStateHandle[KEY_SHOW_TAG_DELETE_CONFIRMATION] = false
    }

    override fun onTagDeleteConfirm() {
        deletionTag?.let {
            viewModelScope.launch {
                repo.deleteTag(it)
                savedStateHandle[KEY_SHOW_TAG_DELETE_CONFIRMATION] = false
                eventsChannel.send(AllExpenseEvent.ShowUiMessage(UiText.StringResource(R.string.tag_deleted)))
            }
        }
    }

    override fun onNewTagClick() {
        viewModelScope.launch {
            savedStateHandle[KEY_TAG_INPUT] = TagInput.INITIAL
            eventsChannel.send(AllExpenseEvent.ToggleTagInput(true))
        }
    }

    override fun dismissNewTagInput() {
        viewModelScope.launch {
            savedStateHandle[KEY_TAG_INPUT] = null
            eventsChannel.send(AllExpenseEvent.ToggleTagInput(false))
        }
    }

    override fun onNewTagNameChange(value: String) {
        savedStateHandle[KEY_TAG_INPUT] = tagInput.value
            ?.copy(name = value)
    }

    override fun onNewTagColorSelect(color: Color) {
        savedStateHandle[KEY_TAG_INPUT] = tagInput.value
            ?.copy(colorCode = color.toArgb())
    }

    override fun onNewTagConfirm() {
        val tagInput = tagInput.value ?: return
        viewModelScope.launch {
            if (tagInput.name.isEmpty()) {
                eventsChannel.send(
                    AllExpenseEvent.ShowUiMessage(
                        UiText.StringResource(R.string.error_invalid_tag_name), true
                    )
                )
                return@launch
            }
            repo.createTag(tagInput)
            eventsChannel.send(AllExpenseEvent.ToggleTagInput(false))
            eventsChannel.send(AllExpenseEvent.ShowUiMessage(UiText.StringResource(R.string.tag_created)))
        }
    }

    override fun onYearSelect(year: String) {
        savedStateHandle[KEY_SELECTED_YEAR] = year
    }

    override fun onMonthSelect(month: Month) {
        savedStateHandle[KEY_SELECTED_MONTH] = month.value
    }

    override fun onExpenseLongClick(id: Long) {
        if (!multiSelectionModeActive.value) {
            savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = listOf(id)
            savedStateHandle[KEY_SELECTED_TAG] = null
            savedStateHandle[KEY_MULTI_SELECTION_ACTIVE] = true
        } else {
            disableMultiSelectionMode()
        }
    }

    private fun disableMultiSelectionMode() {
        savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = emptyList<Long>()
        savedStateHandle[KEY_MULTI_SELECTION_ACTIVE] = false
    }

    override fun onExpenseSelectionToggle(id: Long) {
        addOrRemoveIdFromSelectedList(id)
        if (selectedExpenseIds.value.isEmpty()) disableMultiSelectionMode()
    }

    private fun addOrRemoveIdFromSelectedList(id: Long) {
        if (id in selectedExpenseIds.value) {
            savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = selectedExpenseIds.value - id
        } else {
            savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = selectedExpenseIds.value + id
        }
    }

    override fun onDismissMultiSelectionMode() {
        disableMultiSelectionMode()
    }

    override fun onSelectionStateChange(currentState: ToggleableState) {
        when (currentState) {
            ToggleableState.On -> {
                savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = emptyList<Long>()
            }

            ToggleableState.Off -> {
                state.value.expensesByTagForDate.firstOrNull()?.id?.let {
                    savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = listOf(it)
                }
            }

            ToggleableState.Indeterminate -> {
                savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = state.value.expensesByTagForDate
                    .map { it.id }
            }
        }
    }

    override fun onDeleteExpensesClick() {
        savedStateHandle[KEY_SHOW_EXPENSE_DELETE_CONFIRMATION] = true
    }

    override fun onDeleteExpensesDismissed() {
        savedStateHandle[KEY_SHOW_EXPENSE_DELETE_CONFIRMATION] = false
    }

    override fun onDeleteExpensesConfirmed() {
        state.value.selectedExpenseIds.let {
            viewModelScope.launch {
                repo.deleteExpenses(it)
                savedStateHandle[KEY_SHOW_EXPENSE_DELETE_CONFIRMATION] = false
                disableMultiSelectionMode()
                eventsChannel.send(AllExpenseEvent.ShowUiMessage(UiText.StringResource(R.string.selected_expenses_deleted)))
            }
        }
    }

    override fun onUntagExpensesClick() {
        viewModelScope.launch {
            assignTagToExpenses(null)
            eventsChannel.send(AllExpenseEvent.ShowUiMessage(UiText.StringResource(R.string.expenses_untagged)))
        }
    }

    sealed class AllExpenseEvent {
        data class ShowUiMessage(val message: UiText, val isError: Boolean = false) :
            AllExpenseEvent()

        data class ToggleTagInput(val show: Boolean) : AllExpenseEvent()
    }
}

private const val KEY_SELECTED_TAG = "KEY_SELECTED_TAG"
private const val KEY_SELECTED_YEAR = "KEY_SELECTED_YEAR"
private const val KEY_SELECTED_MONTH = "KEY_SELECTED_MONTH"
private const val KEY_SHOW_TAG_DELETE_CONFIRMATION = "KEY_SHOW_TAG_DELETE_CONFIRMATION"
private const val KEY_SHOW_EXPENSE_DELETE_CONFIRMATION = "KEY_SHOW_EXPENSE_DELETE_CONFIRMATION"
private const val KEY_MULTI_SELECTION_ACTIVE = "KEY_MULTI_SELECTION_ACTIVE"
private const val KEY_SELECTED_EXPENSE_IDS = "KEY_SELECTED_EXPENSE_IDS"
private const val KEY_TAG_INPUT = "KEY_TAG_INPUT"