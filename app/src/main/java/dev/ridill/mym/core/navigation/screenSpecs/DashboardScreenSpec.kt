package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.mym.R
import dev.ridill.mym.dashboard.presentation.DashboardScreen
import dev.ridill.mym.dashboard.presentation.DashboardViewModel
import dev.ridill.mym.expenses.presentation.EXPENSE_DETAILS_ACTION

object DashboardScreenSpec : ScreenSpec {

    override val route: String = "dashboard"

    override val label: Int = R.string.app_name

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: DashboardViewModel = hiltViewModel(navBackStackEntry)

        // Expense Details Result
        val addEditExpenseResult = navBackStackEntry
            .savedStateHandle.getLiveData<String>(EXPENSE_DETAILS_ACTION).observeAsState()
        LaunchedEffect(addEditExpenseResult) {
            navBackStackEntry.savedStateHandle
                .remove<String>(EXPENSE_DETAILS_ACTION)
            addEditExpenseResult.value?.let(viewModel::onExpenseDetailsActionResult)
        }

        DashboardScreen(
            viewModel = viewModel,
            navigateToExpenseDetails = { expenseId ->
                navController.navigate(ExpenseDetailsScreenSpec.routeWithArgs(expenseId))
            },
            navigateToBottomBarSpec = {}
        )
    }
}