package dev.ridill.mym.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.mym.R
import dev.ridill.mym.core.data.preferences.PreferencesManager
import dev.ridill.mym.core.domain.model.UiText
import dev.ridill.mym.core.domain.model.toUiText
import dev.ridill.mym.core.util.One
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.core.util.asStateFlow
import dev.ridill.mym.core.util.ifNaN
import dev.ridill.mym.dashboard.model.repository.DashboardRepository
import dev.ridill.mym.expenses.presentation.add_edit_expense.EXPENSE_ADDED
import dev.ridill.mym.expenses.presentation.add_edit_expense.EXPENSE_DELETED
import dev.ridill.mym.expenses.presentation.add_edit_expense.EXPENSE_UPDATED
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repo: DashboardRepository,
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val expenditure = repo.getExpenditureForCurrentMonth()
    private val monthlyLimit = preferencesManager.preferences.map { it.monthlyLimit }
    private val balanceFromLimit = combineTuple(
        monthlyLimit,
        expenditure
    ).map { (limit, expenditure) -> limit - expenditure }
        .distinctUntilChanged()
    private val balancePercent = combineTuple(
        balanceFromLimit,
        monthlyLimit
    ).map { (balance, limit) ->
        (balance / limit)
            .toFloat()
            .ifNaN { Float.Zero }
            .coerceIn(Float.Zero, Float.One)
    }.distinctUntilChanged()
    private val showBalanceLowWarning = combineTuple(
        monthlyLimit,
        balancePercent
    ).map { (limit, percent) ->
        limit > Long.Zero
                && percent <= BALANCE_LOW_FLOAT
    }.distinctUntilChanged()

    private val expenses = repo.getExpensesForCurrentMonth()

    val state = combineTuple(
        expenditure,
        monthlyLimit,
        balanceFromLimit,
        balancePercent,
        showBalanceLowWarning,
        expenses
    ).map { (
                expenditure,
                monthlyLimit,
                balanceFromLimit,
                balancePercent,
                showBalanceLowWarning,
                expenses
            ) ->
        DashboardState(
            expenditure = expenditure,
            monthlyLimit = monthlyLimit,
            balanceFromLimit = balanceFromLimit,
            balancePercent = balancePercent,
            showBalanceLowWarning = showBalanceLowWarning,
            expenses = expenses
        )
    }.asStateFlow(viewModelScope, DashboardState.INITIAL)

    private val eventsChannel = Channel<DashboardEvents>()
    val events get() = eventsChannel.receiveAsFlow()

    fun onExpenseDetailsActionResult(result: String) = viewModelScope.launch {
        when (result) {
            EXPENSE_ADDED -> R.string.expense_added
            EXPENSE_UPDATED -> R.string.expense_updated
            EXPENSE_DELETED -> R.string.expense_deleted
            else -> null
        }?.let { res ->
            eventsChannel.send(DashboardEvents.ShowUiMessage(res.toUiText()))
        }
    }

    sealed class DashboardEvents {
        data class ShowUiMessage(val uiText: UiText) : DashboardEvents()
    }
}

private const val BALANCE_LOW_FLOAT = 0.10f