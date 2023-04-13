package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.expenses.presentation.expense_management.ExpenseManagementScreenContent
import dev.ridill.mym.expenses.presentation.expense_management.ExpenseManagementViewModel

object ExpenseManagementScreenSpec : BottomBarSpec {
    override val route: String = "expense_management"

    override val icon: ImageVector = Icons.Default.Money

    override val label: Int = R.string.destination_expense_management

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: ExpenseManagementViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        ExpenseManagementScreenContent(
            snackbarController = rememberSnackbarController(),
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp
        )
    }
}