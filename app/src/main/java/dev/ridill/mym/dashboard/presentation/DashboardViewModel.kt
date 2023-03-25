package dev.ridill.mym.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.mym.core.data.preferences.PreferencesManager
import dev.ridill.mym.core.util.One
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.core.util.asStateFlow
import dev.ridill.mym.core.util.ifNaN
import dev.ridill.mym.dashboard.model.repository.DashboardRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repo: DashboardRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val expenditure = repo.getExpenditureForCurrentMonth()
    private val monthlyLimit = preferencesManager.preferences.map { it.monthlyLimit }
    private val balanceFromLimit = combineTuple(
        monthlyLimit,
        expenditure
    ).map { (limit, expenditure) -> limit - expenditure }

    val state = combineTuple(
        expenditure,
        monthlyLimit,
        balanceFromLimit
    ).map { (expenditure,
                monthlyLimit,
                balanceFromLimit) ->
        DashboardState(
            expenditure = expenditure,
            isMonthlyLimitSet = monthlyLimit > Long.Zero,
            monthlyLimit = monthlyLimit,
            balanceFromLimit = balanceFromLimit,
            balancePercent = (balanceFromLimit / monthlyLimit)
                .coerceIn(Double.Zero, Double.One).toFloat().ifNaN { Float.Zero }
        )
    }.asStateFlow(viewModelScope, DashboardState())
}