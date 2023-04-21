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
import dev.ridill.mym.settings.presentation.sign_in.GoogleAuthClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repo: DashboardRepository,
    preferencesManager: PreferencesManager,
    private val authClient: GoogleAuthClient
) : ViewModel() {

    private val expenditure = repo.getExpenditureForCurrentMonth()
    private val monthlyLimit = preferencesManager.preferences.map { it.monthlyLimit }
    private val balanceFromLimit = combineTuple(
        monthlyLimit,
        expenditure
    ).map { (limit, expenditure) -> limit - expenditure }

    private val expenses = repo.getExpensesForCurrentMonth()

    private val username = MutableStateFlow<String?>(null)

    val state = combineTuple(
        expenditure,
        monthlyLimit,
        balanceFromLimit,
        expenses,
        username
    ).map { (expenditure,
                monthlyLimit,
                balanceFromLimit,
                expenses,
                username
            ) ->
        DashboardState(
            expenditure = expenditure,
            isMonthlyLimitSet = monthlyLimit > Long.Zero,
            monthlyLimit = monthlyLimit,
            balanceFromLimit = balanceFromLimit,
            balancePercent = (balanceFromLimit / monthlyLimit)
                .coerceIn(Double.Zero, Double.One).toFloat().ifNaN { Float.Zero },
            expenses = expenses,
            username = username
        )
    }.asStateFlow(viewModelScope, DashboardState())

    private val eventsChannel = Channel<DashboardEvents>()
    val events get() = eventsChannel.receiveAsFlow()

    fun getSignedInUserDetails() {
        username.update {
            authClient.getSignedInUser()?.name
        }
    }

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