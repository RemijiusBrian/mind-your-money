package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import dev.ridill.mym.R
import dev.ridill.mym.expenses.presentation.EXPENSE_DETAILS_ACTION
import dev.ridill.mym.expenses.presentation.ExpenseDetailsScreen
import dev.ridill.mym.expenses.presentation.ExpenseDetailsViewModel

object ExpenseDetailsScreenSpec : ScreenSpec {

    override val route: String = "expense_details/{$ARG_EXPENSE_ID}"

    override val label: Int = R.string.destination_expense_details

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_EXPENSE_ID) {
            type = NavType.LongType
            nullable = false
            defaultValue = ARG_INVALID_ID_LONG
        }
    )

    fun routeWithArgs(expenseId: Long? = null): String =
        route.replace("{$ARG_EXPENSE_ID}", (expenseId ?: ARG_INVALID_ID_LONG).toString())

    fun getExpenseIdFromSavedStateHandle(savedStateHandle: SavedStateHandle): Long =
        savedStateHandle.get<Long>(ARG_EXPENSE_ID) ?: ARG_INVALID_ID_LONG

    private fun getExpenseIdFromArgs(navBackStackEntry: NavBackStackEntry): Long =
        navBackStackEntry.arguments?.getLong(ARG_EXPENSE_ID, -1L) ?: ARG_INVALID_ID_LONG

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: ExpenseDetailsViewModel = hiltViewModel(navBackStackEntry)
        val isEditMode = getExpenseIdFromArgs(navBackStackEntry) > ARG_INVALID_ID_LONG

        ExpenseDetailsScreen(
            isEditMode = isEditMode,
            viewModel = viewModel,
            navigateUp = navController::navigateUp,
            navigateBackWithResult = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(EXPENSE_DETAILS_ACTION, it)

                navController.navigateUp()
            }
        )
    }
}

private const val ARG_EXPENSE_ID = "ARG_EXPENSE_ID"