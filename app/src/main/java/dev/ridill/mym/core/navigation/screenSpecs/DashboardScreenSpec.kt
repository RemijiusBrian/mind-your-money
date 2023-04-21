package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.dashboard.presentation.DashboardScreenContent
import dev.ridill.mym.dashboard.presentation.DashboardViewModel
import dev.ridill.mym.expenses.presentation.add_edit_expense.EXPENSE_DETAILS_ACTION

object DashboardScreenSpec : ScreenSpec {

    override val route: String = "dashboard"

    override val label: Int = R.string.app_name

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: DashboardViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        LaunchedEffect(snackbarController, context, viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    is DashboardViewModel.DashboardEvents.ShowUiMessage -> {
                        snackbarController.showSnackbar(event.uiText.asString(context))
                    }
                }
            }
        }

        // Expense Details Result
        val addEditExpenseResult = navBackStackEntry
            .savedStateHandle.getLiveData<String>(EXPENSE_DETAILS_ACTION).observeAsState()
        LaunchedEffect(addEditExpenseResult) {
            navBackStackEntry.savedStateHandle
                .remove<String>(EXPENSE_DETAILS_ACTION)
            addEditExpenseResult.value?.let(viewModel::onExpenseDetailsActionResult)
        }

        DashboardScreenContent(
            snackbarController = snackbarController,
            state = state,
            onAddFabClick = {
                navController.navigate(AddEditExpenseScreenSpec.routeWithArgs(null))
            },
            onExpenseClick = {
                navController.navigate(AddEditExpenseScreenSpec.routeWithArgs(it))
            },
            onBottomBarActionClick = {
                navController.navigate(it.route)
            },
            onLifecycleStart = viewModel::getSignedInUserDetails
        )
    }
}